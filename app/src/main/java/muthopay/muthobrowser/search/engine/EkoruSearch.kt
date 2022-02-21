package com.muthopay.muthobrowser.search.engine

import com.muthopay.muthobrowser.R

/**
 * The Ekoru search engine.
 */
class EkoruSearch : BaseSearchEngine(
    "file:///android_asset/ekoru.webp",
    "https://www.ekoru.org/?ext=styx&q=",
    R.string.search_engine_ekoru
)
