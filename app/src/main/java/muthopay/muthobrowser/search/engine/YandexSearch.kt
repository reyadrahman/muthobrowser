package com.muthopay.muthobrowser.search.engine

import com.muthopay.muthobrowser.R

/**
 * The Yandex search engine.
 */
class YandexSearch : BaseSearchEngine(
    "file:///android_asset/yandex.webp",
    "https://yandex.ru/yandsearch?lr=21411&text=",
    R.string.search_engine_yandex
)
