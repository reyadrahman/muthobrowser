package com.muthopay.muthobrowser.search.suggestions

import android.app.Application
import com.muthopay.muthobrowser.R
import com.muthopay.muthobrowser.constant.UTF8
import com.muthopay.muthobrowser.database.SearchSuggestion
import com.muthopay.muthobrowser.extensions.map
import com.muthopay.muthobrowser.extensions.preferredLocale
import com.muthopay.muthobrowser.log.Logger
import com.muthopay.muthobrowser.preference.UserPreferences
import io.reactivex.Single
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONArray

/**
 * The search suggestions provider for the Baidu search engine.
 */
class BaiduSuggestionsModel(
    okHttpClient: Single<OkHttpClient>,
    requestFactory: RequestFactory,
    application: Application,
    logger: Logger,
    userPreferences: UserPreferences
) : BaseSuggestionsModel(okHttpClient, requestFactory, UTF8, application.preferredLocale, logger, userPreferences) {

    private val searchSubtitle = application.getString(R.string.suggestion)

    // see http://unionsug.baidu.com/su?wd={encodedQuery}
    // see http://suggestion.baidu.com/s?wd={encodedQuery}&action=opensearch
    override fun createQueryUrl(query: String, language: String): HttpUrl = HttpUrl.Builder()
        .scheme("http")
        .host("suggestion.baidu.com")
        .encodedPath("/su")
        .addEncodedQueryParameter("wd", query)
        .addQueryParameter("action", "opensearch")
        .build()


    @Throws(Exception::class)
    override fun parseResults(responseBody: ResponseBody): List<SearchSuggestion> {
        return JSONArray(responseBody.string())
            .getJSONArray(1)
            .map { it as String }
            .map { SearchSuggestion("$searchSubtitle \"$it\"", it) }
    }

}
