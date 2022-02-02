package com.muthopay.muthobrowser.di

import android.app.Application
import com.muthopay.muthobrowser.BrowserApp
import com.muthopay.muthobrowser.ThemedActivity
import com.muthopay.muthobrowser.adblock.BloomFilterAdBlocker
import com.muthopay.muthobrowser.adblock.NoOpAdBlocker
import com.muthopay.muthobrowser.browser.BrowserPopupMenu
import com.muthopay.muthobrowser.browser.SearchBoxModel
import com.muthopay.muthobrowser.browser.activity.BrowserActivity
import com.muthopay.muthobrowser.browser.activity.ThemedBrowserActivity
import com.muthopay.muthobrowser.browser.bookmarks.BookmarksAdapter
import com.muthopay.muthobrowser.browser.bookmarks.BookmarksDrawerView
import com.muthopay.muthobrowser.browser.sessions.SessionsPopupWindow
import com.muthopay.muthobrowser.browser.tabs.TabsDrawerView
import com.muthopay.muthobrowser.device.BuildInfo
import com.muthopay.muthobrowser.dialog.StyxDialogBuilder
import com.muthopay.muthobrowser.download.StyxDownloadListener
import com.muthopay.muthobrowser.reading.ReadingActivity
import com.muthopay.muthobrowser.search.SuggestionsAdapter
import com.muthopay.muthobrowser.settings.activity.SettingsActivity
import com.muthopay.muthobrowser.settings.activity.ThemedSettingsActivity
import com.muthopay.muthobrowser.settings.fragment.*
import com.muthopay.muthobrowser.view.StyxChromeClient
import com.muthopay.muthobrowser.view.StyxView
import com.muthopay.muthobrowser.view.StyxWebClient
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class), (AppBindsModule::class)])
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun buildInfo(buildInfo: BuildInfo): Builder

        fun build(): AppComponent
    }

    fun inject(activity: BrowserActivity)

    fun inject(fragment: ImportExportSettingsFragment)

    fun inject(builder: StyxDialogBuilder)

    fun inject(styxView: StyxView)

    fun inject(activity: ThemedBrowserActivity)

    fun inject(app: BrowserApp)

    fun inject(activity: ReadingActivity)

    fun inject(webClient: StyxWebClient)

    fun inject(activity: SettingsActivity)

    fun inject(activity: ThemedSettingsActivity)

    fun inject(listener: StyxDownloadListener)

    fun inject(fragment: PrivacySettingsFragment)

    fun inject(fragment: ExtensionsSettingsFragment)

    fun inject(suggestionsAdapter: SuggestionsAdapter)

    fun inject(chromeClient: StyxChromeClient)

    fun inject(searchBoxModel: SearchBoxModel)

    fun inject(generalSettingsFragment: GeneralSettingsFragment)

    fun inject(displaySettingsFragment: DisplaySettingsFragment)

    fun inject(adBlockSettingsFragment: AdBlockSettingsFragment)

    fun inject(aboutSettingsFragment: AboutSettingsFragment)

    fun inject(bookmarksView: BookmarksDrawerView)

    fun inject(popupMenu: BrowserPopupMenu)

    fun inject(popupMenu: SessionsPopupWindow)

    fun inject(appsSettingsFragment: AppsSettingsFragment)

    fun inject(themedActivity: ThemedActivity)

    fun inject(tabsDrawerView: TabsDrawerView)

    fun inject(bookmarksAdapter: BookmarksAdapter)

    fun provideBloomFilterAdBlocker(): BloomFilterAdBlocker

    fun provideNoOpAdBlocker(): NoOpAdBlocker

}
