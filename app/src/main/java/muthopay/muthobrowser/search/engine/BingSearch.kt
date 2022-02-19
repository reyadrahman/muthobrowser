package com.muthopay.muthobrowser.search.engine

import com.muthopay.muthobrowser.R

/**
 * The Bing search engine.
 */
class BingSearch : BaseSearchEngine(
    "file:///android_asset/bing.webp",
    "https://www.bing.com/search?q=",
    R.string.search_engine_bing
)
