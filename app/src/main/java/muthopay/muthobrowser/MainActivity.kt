package com.muthopay.muthobrowser
import com.onesignal.OneSignal

import android.content.Intent
import android.view.KeyEvent
import android.webkit.CookieManager
import com.muthopay.muthobrowser.browser.activity.BrowserActivity
import io.reactivex.Completable

const val ONESIGNAL_APP_ID = "67d604e2-6295-47d8-8c72-65112d53b161"


class MainActivity : BrowserActivity() {

    @Suppress("DEPRECATION")
    public override fun updateCookiePreference(): Completable = Completable.fromAction {
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(userPreferences.cookiesEnabled)
    }

    override fun onNewIntent(intent: Intent) =
        if (intent.action == INTENT_PANIC_TRIGGER) {
            panicClean()
        } else {
            handleNewIntent(intent)
            super.onNewIntent(intent)
            // Logging set to help debug issues, remove before releasing your app.
            OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

            // OneSignal Initialization
            OneSignal.initWithContext(this)
            OneSignal.setAppId(ONESIGNAL_APP_ID)

        }


    /**
     * This is called once our activity is not visible anymore.
     * That's where we should save our data according to the docs.
     * https://developer.android.com/guide/components/activities/activity-lifecycle#onstop
     * Saving data can't wait for onDestroy as there is no guarantee onDestroy will ever be called.
     * In fact even when user closes our Task from recent Task list our activity is just terminated without getting any notifications.
     */
    override fun onStop() {
        super.onStop()
        saveOpenTabsIfNeeded()
    }

    override fun updateHistory(title: String?, url: String) = addItemToHistory(title, url)

    override fun isIncognito() = false

    override fun closeActivity() = closePanels {
        performExitCleanUp()
        moveTaskToBack(true)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN && event.isCtrlPressed) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_P ->
                    // Open a new private window
                    if (event.isShiftPressed) {
                        startActivity(IncognitoActivity.createIntent(this))
                        return true
                    }
            }
        }
        return super.dispatchKeyEvent(event)
    }

}
