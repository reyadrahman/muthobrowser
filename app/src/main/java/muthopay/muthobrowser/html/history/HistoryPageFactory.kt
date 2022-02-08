package com.muthopay.muthobrowser.html.history

import android.app.Application
import com.muthopay.muthobrowser.BrowserApp
import com.muthopay.muthobrowser.R
import com.muthopay.muthobrowser.constant.FILE
import com.muthopay.muthobrowser.database.history.HistoryRepository
import com.muthopay.muthobrowser.html.HtmlPageFactory
import com.muthopay.muthobrowser.html.ListPageReader
import com.muthopay.muthobrowser.html.jsoup.*
import com.muthopay.muthobrowser.utils.ThemeUtils
import com.muthopay.muthobrowser.utils.htmlColor
import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File
import java.io.FileWriter
import javax.inject.Inject

/**
 * Factory for the history page.
 */
@Reusable
class HistoryPageFactory @Inject constructor(
    private val listPageReader: ListPageReader,
    private val application: Application,
    private val historyRepository: HistoryRepository
) : HtmlPageFactory {

    private val title = application.getString(R.string.action_history)

    override fun buildPage(): Single<String> = historyRepository
        .lastHundredVisitedHistoryEntries()
        .map { list ->
            parse(listPageReader.provideHtml()
                    .replace("\${pageTitle}", application.getString(R.string.action_history))
                    .replace("\${backgroundColor}", htmlColor(ThemeUtils.getPrimaryColor(BrowserApp.currentContext())))
                    .replace("\${searchBarColor}", htmlColor(ThemeUtils.getColor(BrowserApp.currentContext(),R.attr.trackColor)))
                    .replace("\${textColor}", htmlColor(ThemeUtils.getColor(BrowserApp.currentContext(),R.attr.colorSecondary)))
                    .replace("\${secondaryTextColor}", htmlColor(ThemeUtils.getColor(BrowserApp.currentContext(),R.attr.colorOnBackground)))
            ) andBuild {
                title { title }
                body {
                    val repeatedElement = id("repeated").removeElement()
                    id("content") {
                        list.forEach {
                            appendChild(repeatedElement.clone {
                                tag("a") { attr("href", it.url) }
                                id("title") { text(it.title) }
                                id("url") { text(it.url) }
                            })
                        }
                    }
                }
            }
        }
        .map { content -> Pair(createHistoryPage(), content) }
        .doOnSuccess { (page, content) ->
            FileWriter(page, false).use { it.write(content) }
        }
        .map { (page, _) -> "$FILE$page" }

    /**
     * Use this observable to immediately delete the history page. This will clear the cached
     * history page that was stored on file.
     *
     * @return a completable that deletes the history page when subscribed to.
     */
    fun deleteHistoryPage(): Completable = Completable.fromAction {
        with(createHistoryPage()) {
            if (exists()) {
                delete()
            }
        }
    }

    private fun createHistoryPage() = File(application.filesDir, FILENAME)

    companion object {
        const val FILENAME = "history.html"
    }

}
