package com.muthopay.muthobrowser

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.StrictMode
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.library.BuildConfig
import com.muthopay.muthobrowser.database.bookmark.BookmarkExporter
import com.muthopay.muthobrowser.database.bookmark.BookmarkRepository
import com.muthopay.muthobrowser.device.BuildInfo
import com.muthopay.muthobrowser.device.BuildType
import com.muthopay.muthobrowser.di.AppComponent
import com.muthopay.muthobrowser.di.DaggerAppComponent
import com.muthopay.muthobrowser.di.DatabaseScheduler
import com.muthopay.muthobrowser.di.injector
import com.muthopay.muthobrowser.log.Logger
import com.muthopay.muthobrowser.preference.DeveloperPreferences
import com.muthopay.muthobrowser.utils.FileUtils
import com.muthopay.muthobrowser.utils.MemoryLeakUtils
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import javax.inject.Inject
import kotlin.system.exitProcess

class BrowserApp : Application() {

    @Inject internal lateinit var developerPreferences: DeveloperPreferences
    @Inject internal lateinit var bookmarkModel: BookmarkRepository
    @Inject @field:DatabaseScheduler internal lateinit var databaseScheduler: Scheduler
    @Inject internal lateinit var logger: Logger
    @Inject internal lateinit var buildInfo: BuildInfo

    lateinit var applicationComponent: AppComponent

    var justStarted: Boolean = true

    override fun onCreate() {
        // SL: Use this to debug when launched from another app for instance
        //Debug.waitForDebugger()

        super.onCreate()
        instance = this
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())
        }

        if (Build.VERSION.SDK_INT >= 28) {
            if (getProcessName() == "$packageName:incognito") {
                WebView.setDataDirectorySuffix("incognito")
            }
        }

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, ex ->
            if (BuildConfig.DEBUG) {
                FileUtils.writeCrashToStorage(ex)
            }

            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, ex)
            } else {
                exitProcess(2)
            }
        }

        RxJavaPlugins.setErrorHandler { throwable: Throwable? ->
            if (BuildConfig.DEBUG && throwable != null) {
                FileUtils.writeCrashToStorage(throwable)
                throw throwable
            }
        }

        applicationComponent = DaggerAppComponent.builder()
            .application(this)
            .buildInfo(createBuildInfo())
            .build()

        injector.inject(this)

        Single.fromCallable(bookmarkModel::count)
            .filter { it == 0L }
            .flatMapCompletable {
                val assetsBookmarks = BookmarkExporter.importBookmarksFromAssets(this@BrowserApp)
                bookmarkModel.addBookmarkList(assetsBookmarks)
            }
            .subscribeOn(databaseScheduler)
            .subscribe()

        if (buildInfo.buildType == BuildType.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        registerActivityLifecycleCallbacks(object : MemoryLeakUtils.LifecycleAdapter() {
            override fun onActivityDestroyed(activity: Activity) {
                logger.log(TAG, "Cleaning up after the Android framework")
                MemoryLeakUtils.clearNextServedView(activity as AppCompatActivity, this@BrowserApp)
            }

            // Track current activity
            override fun onActivityResumed(activity: Activity) {
                resumedActivity = activity as AppCompatActivity
            }

            // Track current activity
            override fun onActivityPaused(activity: Activity) {
                resumedActivity = null
            }
        })
    }

    /**
     * Create the [BuildType] from the [BuildConfig].
     */
    private fun createBuildInfo() = BuildInfo(when {
        BuildConfig.DEBUG -> BuildType.DEBUG
        else -> BuildType.RELEASE
    })

    companion object {
        private const val TAG = "BrowserApp"
        lateinit var instance: BrowserApp
        // Used to track current activity
        var resumedActivity: AppCompatActivity? = null

        /**
         * Used to get current activity context in order to access current theme.
         */
        fun currentContext() : Context {
            return resumedActivity ?: instance.applicationContext
        }
    }
}
