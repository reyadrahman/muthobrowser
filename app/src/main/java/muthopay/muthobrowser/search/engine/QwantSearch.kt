package com.muthopay.muthobrowser.search.engine

import com.muthopay.muthobrowser.R

/**
 * The Qwant search engine.
 */
class QwantSearch : BaseSearchEngine(
    "file:///android_asset/qwant.webp",
    "https://www.qwant.com/?q=",
    R.string.search_engine_qwant
)
