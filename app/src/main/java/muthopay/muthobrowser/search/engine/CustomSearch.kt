package com.muthopay.muthobrowser.search.engine

import com.muthopay.muthobrowser.R

/**
 * A custom search engine.
 */
class CustomSearch(queryUrl: String) : BaseSearchEngine(
    "file:///android_asset/styx.webp",
    queryUrl,
    R.string.search_engine_custom
)
