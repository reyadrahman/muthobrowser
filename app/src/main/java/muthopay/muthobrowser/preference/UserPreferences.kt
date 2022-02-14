package com.muthopay.muthobrowser.preference

import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import com.muthopay.muthobrowser.AccentTheme
import com.muthopay.muthobrowser.AppTheme
import com.muthopay.muthobrowser.BrowserApp
import com.muthopay.muthobrowser.R
import com.muthopay.muthobrowser.browser.*
import com.muthopay.muthobrowser.constant.DEFAULT_ENCODING
import com.muthopay.muthobrowser.constant.Uris
import com.muthopay.muthobrowser.device.ScreenSize
import com.muthopay.muthobrowser.di.UserPrefs
import com.muthopay.muthobrowser.preference.delegates.*
import com.muthopay.muthobrowser.search.SearchEngineProvider
import com.muthopay.muthobrowser.search.engine.GoogleSearch
import com.muthopay.muthobrowser.settings.NewTabPosition
import com.muthopay.muthobrowser.utils.FileUtils
import com.muthopay.muthobrowser.view.RenderingMode
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The user's preferences.
 */
@Singleton
class UserPreferences @Inject constructor(
    @UserPrefs preferences: SharedPreferences,
    screenSize: ScreenSize
) {

    /**
     * True if Web RTC is enabled in the browser, false otherwise.
     */
    var webRtcEnabled by preferences.booleanPreference(R.string.pref_key_webrtc, R.bool.pref_default_webrtc)

    /**
     * True if the browser should block ads, false otherwise.
     */
    var adBlockEnabled by preferences.booleanPreference(R.string.pref_key_block_ads, R.bool.pref_default_block_ads)

    /**
     * True if the browser should block images from being loaded, false otherwise.
     */
    var loadImages by preferences.booleanPreference(R.string.pref_key_load_images, R.bool.pref_default_load_images)

    /**
     * True if the browser should clear the browser cache when the app is exited, false otherwise.
     */
    var clearCacheExit by preferences.booleanPreference(R.string.pref_key_clear_cache_exit, R.bool.pref_default_clear_cache_exit)

    /**
     * True if the browser should allow websites to store and access cookies, false otherwise.
     */
    var cookiesEnabled by preferences.booleanPreference(R.string.pref_key_cookies, R.bool.pref_default_cookies)

    /**
     * True if cookies should be enabled in incognito mode, false otherwise.
     *
     * WARNING: Cookies will be shared between regular and incognito modes if this is enabled.
     */
    var incognitoCookiesEnabled by preferences.booleanPreference(R.string.pref_key_cookies_incognito, R.bool.pref_default_cookies_incognito)

    /**
     * The folder into which files will be downloaded.
     */
    var downloadDirectory by preferences.stringPreference(R.string.pref_key_download_directory, FileUtils.DEFAULT_DOWNLOAD_PATH)

    /**
     * True if the browser should hide the navigation bar when scrolling, false if it should be
     * immobile.
     */
    var hideToolBarInPortrait by preferences.booleanPreference(R.string.pref_key_portrait_hide_tool_bar, R.bool.pref_default_portrait_hide_tool_bar)

    /**
     * True if the browser should hide the navigation bar when scrolling, false if it should be
     * immobile.
     */
    var hideToolBarInLandscape by preferences.booleanPreference(R.string.pref_key_landscape_hide_tool_bar, R.bool.pref_default_landscape_hide_tool_bar)

    /**
     */
    var showToolBarOnScrollUpInPortrait by preferences.booleanPreference(R.string.pref_key_portrait_show_tool_bar_on_scroll_up, R.bool.pref_default_portrait_show_tool_bar_on_scroll_up)

    /**
     */
    var showToolBarOnScrollUpInLandscape by preferences.booleanPreference(R.string.pref_key_landscape_show_tool_bar_on_scroll_up, R.bool.pref_default_landscape_show_tool_bar_on_scroll_up)

    /**
     */
    var showToolBarOnPageTopInPortrait by preferences.booleanPreference(R.string.pref_key_portrait_show_tool_bar_on_page_top, R.bool.pref_default_portrait_show_tool_bar_on_page_top)

    /**
     */
    var showToolBarOnPageTopInLandscape by preferences.booleanPreference(R.string.pref_key_landscape_show_tool_bar_on_page_top, R.bool.pref_default_landscape_show_tool_bar_on_page_top)

    /**
     * True if the system status bar should be hidden throughout the app, false if it should be
     * visible.
     */
    var hideStatusBarInPortrait by preferences.booleanPreference(R.string.pref_key_portrait_hide_status_bar, R.bool.pref_default_portrait_hide_status_bar)

    /**
     * True if the system status bar should be hidden throughout the app, false if it should be
     * visible.
     */
    var hideStatusBarInLandscape by preferences.booleanPreference(R.string.pref_key_landscape_hide_status_bar, R.bool.pref_default_landscape_hide_status_bar)

    /**
     * Defines if a new tab should be opened when user is doing a new search.
     */
    var searchInNewTab by preferences.booleanPreference(R.string.pref_key_search_in_new_tab, R.bool.pref_default_search_in_new_tab)

    /**
     * Defines if a new tab should be opened when user provided a new URL.
     */
    var urlInNewTab by preferences.booleanPreference(R.string.pref_key_url_in_new_tab, R.bool.pref_default_url_in_new_tab)

    /**
     * Defines if a new tab should be opened when user taps on homepage button.
     */
    var homepageInNewTab by preferences.booleanPreference(R.string.pref_key_homepage_in_new_tab, R.bool.pref_default_homepage_in_new_tab)

    /**
     * Defines if a new tab should be opened when user selects a bookmark.
     */
    var bookmarkInNewTab by preferences.booleanPreference(R.string.pref_key_bookmark_in_new_tab, R.bool.pref_default_bookmark_in_new_tab)

    /**
     * Value of our new tab position enum.
     * Defines where a new tab should be created in our tab list.
     *
     * @see NewTabPosition
     */
    var newTabPosition by preferences.enumPreference(R.string.pref_key_new_tab_position, NewTabPosition.AFTER_CURRENT_TAB)

    /**
     * True if desktop mode should be enabled by default for new tabs, false otherwise.
     */
    var desktopModeDefault by preferences.booleanPreference(R.string.pref_key_desktop_mode_default, R.bool.pref_default_desktop_mode_default)

    /**
     * The URL of the selected homepage.
     */
    var homepage by preferences.stringPreference(R.string.pref_key_homepage, Uris.AboutHome)

    /**
     * True if the browser should allow execution of javascript, false otherwise.
     */
    var javaScriptEnabled by preferences.booleanPreference(R.string.pref_key_javascript, R.bool.pref_default_javascript)

    /**
     * True if the device location should be accessible by websites, false otherwise.
     *
     * NOTE: If this is enabled, permission will still need to be granted on a per-site basis.
     */
    var locationEnabled by preferences.booleanPreference(R.string.pref_key_location, R.bool.pref_default_location)

    /**
     * True if the browser should load pages zoomed out instead of zoomed in so that the text is
     * legible, false otherwise.
     */
    var overviewModeEnabled by preferences.booleanPreference(R.string.pref_key_overview_mode, R.bool.pref_default_overview_mode)

    /**
     * True if the browser should allow websites to open new windows, false otherwise.
     */
    var popupsEnabled by preferences.booleanPreference(R.string.pref_key_support_multiple_window, R.bool.pref_default_support_multiple_window)

    /**
     * True if the app should remember which browser tabs were open and restore them if the browser
     * is automatically closed by the system.
     */
    var restoreTabsOnStartup by preferences.booleanPreference(R.string.pref_key_restore_tabs_on_startup, R.bool.pref_default_restore_tabs_on_startup)

    /**
     * True if the browser should save form input, false otherwise.
     */
    var savePasswordsEnabled by preferences.booleanPreference(R.string.pref_key_save_passwords, R.bool.pref_default_save_passwords)

    /**
     * The index of the chosen search engine.
     *
     * @see SearchEngineProvider
     */
    var searchChoice by preferences.intPreference(R.string.pref_key_search, 1)

    /**
     * The custom URL which should be used for making searches.
     */
    var searchUrl by preferences.stringPreference(R.string.pref_key_search_url, GoogleSearch().queryUrl)

    /**
     * True if the browser should attempt to reflow the text on a web page after zooming in or out
     * of the page.
     */
    var textReflowEnabled by preferences.booleanPreference(R.string.pref_key_text_reflow, R.bool.pref_default_text_reflow)

    /**
     * The index of the text size that should be used in the browser.
     * Default to 50 to have 100% as default text size.
     */
    var browserTextSize by preferences.intPreference(R.string.pref_key_browser_text_size, 50)

    /**
     * True if the browser should fit web pages to the view port, false otherwise.
     */
    var useWideViewPortEnabled by preferences.booleanPreference(R.string.pref_key_wide_viewport, R.bool.pref_default_wide_viewport)

    /**
     * The index of the user agent choice that should be used by the browser.
     *
     * @see UserPreferences.userAgent
     */
    var userAgentChoice by preferences.intPreference(R.string.pref_key_user_agent, 1)

    /**
     * The custom user agent that should be used by the browser.
     */
    var userAgentString by preferences.stringPreference(R.string.pref_key_user_agent_string, "")

    /**
     * True if the browser should clear the navigation history on app exit, false otherwise.
     */
    var clearHistoryExitEnabled by preferences.booleanPreference(R.string.pref_key_clear_history_exit, R.bool.pref_default_clear_history_exit)

    /**
     * True if the browser should clear the browser cookies on app exit, false otherwise.
     */
    var clearCookiesExitEnabled by preferences.booleanPreference(R.string.pref_key_clear_cookies_exit, R.bool.pref_default_clear_cookies_exit)

    /**
     * The index of the rendering mode that should be used by the browser.
     */
    var renderingMode by preferences.enumPreference(R.string.pref_key_rendering_mode, RenderingMode.NORMAL)

    /**
     * True if third party cookies should be disallowed by the browser, false if they should be
     * allowed.
     */
    var blockThirdPartyCookiesEnabled by preferences.booleanPreference(R.string.pref_key_block_third_party, R.bool.pref_default_block_third_party)

    /**
     * True if the browser should extract the theme color from a website and color the UI with it,
     * false otherwise.
     */
    var colorModeEnabled by preferences.booleanPreference(R.string.pref_key_web_page_theme, R.bool.pref_default_color_mode)

    /**
     * The index of the URL/search box display choice/
     *
     * @see SearchBoxModel
     */
    var urlBoxContentChoice by preferences.enumPreference(R.string.pref_key_tool_bar_text_display, SearchBoxDisplayChoice.TITLE)

    /**
     * True if the browser should invert the display colors of the web page content, false
     * otherwise.
     */
    var invertColors by preferences.booleanPreference(R.string.pref_key_invert_colors, R.bool.pref_default_invert_colors)

    /**
     * The index of the reading mode text size.
     */
    var readingTextSize by preferences.intPreference(R.string.pref_key_reading_text_size, 2)

    /**
     * The index of the theme used by the application.
     */
    var useTheme by preferences.enumPreference(R.string.pref_key_theme, AppTheme.DEFAULT)

    var useAccent by preferences.enumPreference(R.string.pref_key_accent, AccentTheme.DEFAULT_ACCENT)

    /**
     * The text encoding used by the browser.
     */
    var textEncoding by preferences.stringPreference(R.string.pref_key_default_text_encoding, DEFAULT_ENCODING)

    /**
     * True if the web page storage should be cleared when the app exits, false otherwise.
     */
    var clearWebStorageExitEnabled by preferences.booleanPreference(R.string.pref_key_clear_web_storage_exit, R.bool.pref_default_clear_web_storage_exit)

    /**
     * True if the app should use the navigation drawer UI, false if it should use the traditional
     * desktop browser tabs UI.
     */
    private var verticalTabBarInPortrait by preferences.booleanPreference(R.string.pref_key_portrait_tab_bar_vertical, !screenSize.isTablet())
    private var verticalTabBarInLandscape by preferences.booleanPreference(R.string.pref_key_landscape_tab_bar_vertical, !screenSize.isTablet())

    var verticalTabBar : Boolean = false
        get() = if (Resources.getSystem().configuration.orientation == Configuration.ORIENTATION_PORTRAIT) verticalTabBarInPortrait else verticalTabBarInLandscape
        private set

    /**
     *
     */
    private var toolbarsBottomInPortrait by preferences.booleanPreference(R.string.pref_key_portrait_toolbars_bottom, R.bool.pref_default_toolbars_bottom)
    private var toolbarsBottomInLandscape by preferences.booleanPreference(R.string.pref_key_landscape_toolbars_bottom, R.bool.pref_default_toolbars_bottom)

    var toolbarsBottom : Boolean = false
        get() = toolbarsBottom()
        private set

    private fun toolbarsBottom(aConf: Configuration=Resources.getSystem().configuration) : Boolean {
        return if (aConf.orientation == Configuration.ORIENTATION_PORTRAIT) toolbarsBottomInPortrait else toolbarsBottomInLandscape
    }

    /**
     * True if the browser should send a do not track (DNT) header with every GET request, false
     * otherwise.
     */
    var doNotTrackEnabled by preferences.booleanPreference(R.string.pref_key_do_not_track, R.bool.pref_default_do_not_track)

    /**
     * True if the browser should save form data, false otherwise.
     */
    var saveDataEnabled by preferences.booleanPreference(R.string.pref_key_request_save_data, R.bool.pref_default_request_save_data)

    /**
     * True if the browser should attempt to remove identifying headers in GET requests, false if
     * the default headers should be left along.
     */
    var removeIdentifyingHeadersEnabled by preferences.booleanPreference(R.string.pref_key_identifying_headers, R.bool.pref_default_identifying_headers)

    /**
     * True if the bookmarks tab should be on the opposite side of the screen, false otherwise. If
     * the navigation drawer UI is used, the tab drawer will be displayed on the opposite side as
     * well.
     */
    var bookmarksAndTabsSwapped by preferences.booleanPreference(R.string.pref_key_swap_tabs_and_bookmarks, R.bool.pref_default_swap_tabs_and_bookmarks)

    /**
     * Disable gesture actions on drawer.
     */
    var lockedDrawers by preferences.booleanPreference(R.string.pref_key_locked_drawers, R.bool.pref_default_locked_drawers)

    /**
     * Use bottom sheets instead of drawers to display tabs and bookmarks.
     */
    var useBottomSheets by preferences.booleanPreference(R.string.pref_key_use_bottom_sheets, R.bool.pref_default_use_bottom_sheets)

    /**
     *
     */
    var pullToRefreshInPortrait by preferences.booleanPreference(R.string.pref_key_portrait_pull_to_refresh, R.bool.pref_default_portrait_pull_to_refresh)
    var pullToRefreshInLandscape by preferences.booleanPreference(R.string.pref_key_landscape_pull_to_refresh, R.bool.pref_default_landscape_pull_to_refresh)

    /**
     * True if the status bar of the app should always be high contrast, false if it should follow
     * the theme of the app.
     */
    var useBlackStatusBar by preferences.booleanPreference(R.string.pref_key_black_status_bar, R.bool.pref_default_black_status_bar)

    /**
     * The index of the proxy choice.
     */
    var proxyChoice by preferences.enumPreference(R.string.pref_key_proxy_choice, ProxyChoice.NONE)

    /**
     * The proxy host used when [proxyChoice] is [ProxyChoice.MANUAL].
     */
    var proxyHost by preferences.stringPreference(R.string.pref_key_proxy_host, "localhost")

    /**
     * The proxy port used when [proxyChoice] is [ProxyChoice.MANUAL].
     */
    var proxyPort by preferences.intPreference(R.string.pref_key_use_proxy_port, 8118)

    /**
     * The index of the search suggestion choice.
     *
     * @see SearchEngineProvider
     */
    var searchSuggestionChoice by preferences.intPreference(R.string.pref_key_search_suggestions, 1)

    /**
     * The index of the ad blocking hosts file source.
     */
    var hostsSource by preferences.intPreference(R.string.pref_key_hosts_source, 0)

    /**
     * The local file from which ad blocking hosts should be read, depending on the [hostsSource].
     */
    var hostsLocalFile by preferences.nullableStringPreference(R.string.pref_key_hosts_local_file)

    /**
     * The remote URL from which ad blocking hosts should be read, depending on the [hostsSource].
     */
    var hostsRemoteFile by preferences.nullableStringPreference(R.string.pref_key_hosts_remote_file)

    /**
     * Toggle visibility of close tab button on drawer tab list items.
     */
    var showCloseTabButton by preferences.booleanPreference(R.string.pref_key_tab_list_item_show_close_button, if (screenSize.isTablet())  R.bool.pref_default_tab_list_item_show_close_buttons else R.bool.pref_default_tab_list_item_show_close_button)

    /**
     * Define viewport width for desktop mode in portrait
     */
    var desktopWidthInPortrait by preferences.intPreference(R.string.pref_key_portrait_desktop_width, BrowserApp.instance.resources.getInteger(R.integer.pref_default_portrait_desktop_width))

    /**
     * Define viewport width for desktop mode in landscape
     */
    var desktopWidthInLandscape by preferences.intPreference(R.string.pref_key_landscape_desktop_width, BrowserApp.instance.resources.getInteger(R.integer.pref_default_landscape_desktop_width))

    /**
     * True if dark mode should be enabled by default for new tabs, false otherwise.
     */
    var darkModeDefault by preferences.booleanPreference(R.string.pref_key_dark_mode_default, R.bool.pref_default_dark_mode_default)

    /**
     * Block JavaScript for Websites
     */
    var javaScriptChoice by preferences.enumPreference(R.string.pref_key_use_js_block, JavaScriptChoice.NONE)

    var javaScriptBlocked by preferences.stringPreference(R.string.pref_key_block_js, "")

    var siteBlockNames by preferences.stringPreference(R.string.pref_key_use_site_block, "")

    /**
     * Malware blocker
     */
    var blockMalwareEnabled by preferences.booleanPreference(R.string.pref_key_block_malware, R.bool.pref_default_block_malware)

    /**
     * Force Zoom for Websites
     */
    var forceZoom by preferences.booleanPreference(R.string.pref_key_force_zoom, R.bool.pref_default_force_zoom)

    /**
     * Always in Incognito mode
     */
    var incognito by preferences.booleanPreference(R.string.pref_key_always_incognito, R.bool.pref_default_always_incognito)

    /**
     * SSL Warn Dialog
     */
    var ssl by preferences.booleanPreference(R.string.pref_key_ssl_dialog, R.bool.pref_default_ssl_dialog)

    /**
     * Show second navbar at the bottom of the screen
     */
    var navbar by preferences.booleanPreference(R.string.pref_key_second_nav_bar, R.bool.pref_default_second_nav_bar)

    /**
     * Define close on last tab
     */
    var closeOnLastTab by preferences.booleanPreference(R.string.pref_key_close_on_last_tab, R.bool.pref_default_close_on_last_tab)

    var imageUrlString by preferences.stringPreference(R.string.pref_key_image_url, "")

    /**
     * Define Suggestion number Choice
     */
    var suggestionChoice by preferences.enumPreference(R.string.pref_key_search_suggestions_number, SuggestionNumChoice.FIVE)

    /**
     * Show download dialog before downloading a file
     */
    var showDownloadConfirmation by preferences.booleanPreference(R.string.pref_key_show_download_confirmation, R.bool.pref_default_show_download_confirmation)
}
