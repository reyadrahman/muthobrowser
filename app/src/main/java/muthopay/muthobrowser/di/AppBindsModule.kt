package com.muthopay.muthobrowser.di

import com.muthopay.muthobrowser.adblock.allowlist.AllowListModel
import com.muthopay.muthobrowser.adblock.allowlist.SessionAllowListModel
import com.muthopay.muthobrowser.adblock.source.AssetsHostsDataSource
import com.muthopay.muthobrowser.adblock.source.HostsDataSource
import com.muthopay.muthobrowser.adblock.source.HostsDataSourceProvider
import com.muthopay.muthobrowser.adblock.source.PreferencesHostsDataSourceProvider
import com.muthopay.muthobrowser.browser.cleanup.DelegatingExitCleanup
import com.muthopay.muthobrowser.browser.cleanup.ExitCleanup
import com.muthopay.muthobrowser.database.adblock.HostsDatabase
import com.muthopay.muthobrowser.database.adblock.HostsRepository
import com.muthopay.muthobrowser.database.allowlist.AdBlockAllowListDatabase
import com.muthopay.muthobrowser.database.allowlist.AdBlockAllowListRepository
import com.muthopay.muthobrowser.database.bookmark.BookmarkDatabase
import com.muthopay.muthobrowser.database.bookmark.BookmarkRepository
import com.muthopay.muthobrowser.database.downloads.DownloadsDatabase
import com.muthopay.muthobrowser.database.downloads.DownloadsRepository
import com.muthopay.muthobrowser.database.history.HistoryDatabase
import com.muthopay.muthobrowser.database.history.HistoryRepository
import com.muthopay.muthobrowser.database.javascript.JavaScriptDatabase
import com.muthopay.muthobrowser.database.javascript.JavaScriptRepository
import com.muthopay.muthobrowser.ssl.SessionSslWarningPreferences
import com.muthopay.muthobrowser.ssl.SslWarningPreferences
import dagger.Binds
import dagger.Module

/**
 * Dependency injection module used to bind implementations to interfaces.
 */
@Module
interface AppBindsModule {

    @Binds
    fun bindsExitCleanup(delegatingExitCleanup: DelegatingExitCleanup): ExitCleanup

    @Binds
    fun bindsBookmarkModel(bookmarkDatabase: BookmarkDatabase): BookmarkRepository

    @Binds
    fun bindsDownloadsModel(downloadsDatabase: DownloadsDatabase): DownloadsRepository

    @Binds
    fun bindsHistoryModel(historyDatabase: HistoryDatabase): HistoryRepository

    @Binds
    fun bindsJavaScriptModel(javaScriptDatabase: JavaScriptDatabase): JavaScriptRepository

    @Binds
    fun bindsAdBlockAllowListModel(adBlockAllowListDatabase: AdBlockAllowListDatabase): AdBlockAllowListRepository

    @Binds
    fun bindsAllowListModel(sessionAllowListModel: SessionAllowListModel): AllowListModel

    @Binds
    fun bindsSslWarningPreferences(sessionSslWarningPreferences: SessionSslWarningPreferences): SslWarningPreferences

    @Binds
    fun bindsHostsDataSource(assetsHostsDataSource: AssetsHostsDataSource): HostsDataSource

    @Binds
    fun bindsHostsRepository(hostsDatabase: HostsDatabase): HostsRepository

    @Binds
    fun bindsHostsDataSourceProvider(preferencesHostsDataSourceProvider: PreferencesHostsDataSourceProvider): HostsDataSourceProvider
}
