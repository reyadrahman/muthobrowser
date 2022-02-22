package com.muthopay.muthobrowser.search.engine

import com.muthopay.muthobrowser.R

/**
 * The Google search engine.
 */
class GoogleSearch : BaseSearchEngine(
    "file:///android_asset/google.webp",
    "https://www.google.com/search?client=styx&ie=UTF-8&oe=UTF-8&q=",
    R.string.search_engine_google
)
