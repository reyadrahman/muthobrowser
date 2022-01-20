package com.muthopay.muthobrowser.browser.bookmarks

import android.graphics.Bitmap
import com.muthopay.muthobrowser.database.Bookmark

/**
 * The data model representing a [Bookmark] in a list.
 *
 * @param bookmark The bookmark backing this view model, either an entry or a folder.
 * @param icon The icon for this bookmark.
 */
data class BookmarksViewModel(
    val bookmark: Bookmark,
    var icon: Bitmap? = null
)
