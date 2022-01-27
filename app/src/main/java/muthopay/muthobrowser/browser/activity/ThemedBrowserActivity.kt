package com.muthopay.muthobrowser.browser.activity

import android.content.Intent
import android.os.Bundle
import com.muthopay.muthobrowser.AccentTheme
import com.muthopay.muthobrowser.AppTheme
import com.muthopay.muthobrowser.R
import com.muthopay.muthobrowser.ThemedActivity
import com.muthopay.muthobrowser.di.injector


abstract class ThemedBrowserActivity : ThemedActivity() {

    private var shouldRunOnResumeActions = false


    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        super.onCreate(savedInstanceState)
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && shouldRunOnResumeActions) {
            shouldRunOnResumeActions = false
            onWindowVisibleToUserAfterResume()
        }
    }


    override fun onResume() {
        super.onResume()
        resetPreferences()
        shouldRunOnResumeActions = true
        if (themeId != userPreferences.useTheme) {
            restart()
        }
        if (accentId != userPreferences.useAccent) {
            restart()
        }
    }

    /**
     * Using this instead of recreate() because it does not work when handling resource changes I guess.
     */
    protected fun restart() {
        finish()
        startActivity(Intent(this, javaClass))
    }

    /**
     * From ThemedActivity
     */
    override fun themeStyle(aTheme: AppTheme): Int {
        return when (aTheme) {
            AppTheme.DEFAULT -> R.style.Theme_App_DayNight
            AppTheme.LIGHT -> R.style.Theme_App_Light
            AppTheme.DARK ->  R.style.Theme_App_Dark
            AppTheme.BLACK -> R.style.Theme_App_Black
        }
    }

    override fun accentStyle(accentTheme: AccentTheme): Int {
        return when (accentTheme) {
            AccentTheme.DEFAULT_ACCENT -> R.style.Accent_Red
            AccentTheme.PINK -> R.style.Accent_Pink
            AccentTheme.PURPLE ->  R.style.Accent_Puple
            AccentTheme.DEEP_PURPLE -> R.style.Accent_Deep_Purple
            AccentTheme.INDIGO -> R.style.Accent_Indigo
            AccentTheme.BLUE -> R.style.Accent_Blue
            AccentTheme.LIGHT_BLUE -> R.style.Accent_Light_Blue
            AccentTheme.CYAN -> R.style.Accent_Cyan
            AccentTheme.TEAL -> R.style.Accent_Teal
            AccentTheme.GREEN -> R.style.Accent_Green
            AccentTheme.LIGHT_GREEN -> R.style.Accent_Light_Green
            AccentTheme.LIME -> R.style.Accent_Lime
            AccentTheme.YELLOW -> R.style.Accent_Yellow
            AccentTheme.AMBER -> R.style.Accent_Amber
            AccentTheme.ORANGE -> R.style.Accent_Orange
            AccentTheme.DEEP_ORANGE -> R.style.Accent_Deep_Orange
            AccentTheme.BROWN -> R.style.Accent_Brown
        }
    }

}
