package com.muthopay.muthobrowser.search.engine

import com.muthopay.muthobrowser.R

/**
 * The DuckDuckGo Lite search engine.
 */
class DuckLiteNoJSSearch : BaseSearchEngine(
    "file:///android_asset/duckduckgo.webp",
    "https://duckduckgo.com/lite/?q=",
    R.string.search_engine_duckduckgo_lite_no_js
)
