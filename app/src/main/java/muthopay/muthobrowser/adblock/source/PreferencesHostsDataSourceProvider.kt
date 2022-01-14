package com.muthopay.muthobrowser.adblock.source

import android.app.Application
import android.content.res.AssetManager
import com.muthopay.muthobrowser.di.HostsClient
import com.muthopay.muthobrowser.log.Logger
import com.muthopay.muthobrowser.preference.UserPreferences
import dagger.Reusable
import io.reactivex.Single
import okhttp3.OkHttpClient
import javax.inject.Inject

/**
 * A [HostsDataSourceProvider] backed by [UserPreferences].
 */
@Reusable
class PreferencesHostsDataSourceProvider @Inject constructor(
    private val userPreferences: UserPreferences,
    private val assetManager: AssetManager,
    private val logger: Logger,
    @HostsClient private val okHttpClient: Single<OkHttpClient>,
    private val application: Application
) : HostsDataSourceProvider {

    override fun createHostsDataSource(): HostsDataSource =
        when (val source = userPreferences.selectedHostsSource()) {
            HostsSourceType.Default -> AssetsHostsDataSource(assetManager, logger)
            is HostsSourceType.Local -> FileHostsDataSource(logger, source.file)
            is HostsSourceType.Remote -> UrlHostsDataSource(source.httpUrl, okHttpClient, logger, userPreferences, application)
        }

}
