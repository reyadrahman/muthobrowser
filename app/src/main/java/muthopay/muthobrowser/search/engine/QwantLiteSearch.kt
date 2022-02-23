package com.muthopay.muthobrowser.search.engine

import com.muthopay.muthobrowser.R

/**
 * The Qwant Lite search engine.
 */
class QwantLiteSearch : BaseSearchEngine(
    "file:///android_asset/qwant.webp",
    "https://lite.qwant.com/?q=",
    R.string.search_engine_qwant_lite
)
