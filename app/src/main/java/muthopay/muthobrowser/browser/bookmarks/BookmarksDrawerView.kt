package com.muthopay.muthobrowser.browser.bookmarks

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.webkit.CookieManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muthopay.muthobrowser.R
import com.muthopay.muthobrowser.adblock.allowlist.AllowListModel
import com.muthopay.muthobrowser.animation.AnimationUtils
import com.muthopay.muthobrowser.browser.BookmarksView
import com.muthopay.muthobrowser.browser.JavaScriptChoice
import com.muthopay.muthobrowser.browser.TabsManager
import com.muthopay.muthobrowser.controller.UIController
import com.muthopay.muthobrowser.database.Bookmark
import com.muthopay.muthobrowser.database.bookmark.BookmarkRepository
import com.muthopay.muthobrowser.databinding.BookmarkDrawerViewBinding
import com.muthopay.muthobrowser.di.DatabaseScheduler
import com.muthopay.muthobrowser.di.MainScheduler
import com.muthopay.muthobrowser.di.NetworkScheduler
import com.muthopay.muthobrowser.di.injector
import com.muthopay.muthobrowser.dialog.BrowserDialog
import com.muthopay.muthobrowser.dialog.DialogItem
import com.muthopay.muthobrowser.dialog.StyxDialogBuilder
import com.muthopay.muthobrowser.extensions.*
import com.muthopay.muthobrowser.favicon.FaviconModel
import com.muthopay.muthobrowser.preference.UserPreferences
import com.muthopay.muthobrowser.utils.*
import com.muthopay.muthobrowser.view.CodeView
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import java.net.URL
import javax.inject.Inject


/**
 * The view that displays bookmarks in a list and some controls.
 */
@SuppressLint("ViewConstructor")
class BookmarksDrawerView @JvmOverloads constructor(
        context: Context,
        private val activity: Activity,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        userPreferences: UserPreferences
) : LinearLayout(context, attrs, defStyleAttr), BookmarksView {

    @Inject internal lateinit var bookmarkModel: BookmarkRepository
    @Inject internal lateinit var allowListModel: AllowListModel
    @Inject internal lateinit var bookmarksDialogBuilder: StyxDialogBuilder
    @Inject internal lateinit var faviconModel: FaviconModel
    @Inject lateinit var userPreferences: UserPreferences
    @Inject @field:DatabaseScheduler internal lateinit var databaseScheduler: Scheduler
    @Inject @field:NetworkScheduler internal lateinit var networkScheduler: Scheduler
    @Inject @field:MainScheduler internal lateinit var mainScheduler: Scheduler

    private val uiController: UIController

    // Adapter
    private var iAdapter: BookmarksAdapter
    // Drag & drop support
    private var iItemTouchHelper: ItemTouchHelper? = null

    // Colors
    private var scrollIndex: Int = 0

    private var bookmarksSubscription: Disposable? = null
    private var bookmarkUpdateSubscription: Disposable? = null

    private val uiModel = BookmarkUiModel()

    var iBinding: BookmarkDrawerViewBinding

    private var addBookmarkView: ImageView? = null

    init {

        context.injector.inject(this)

        uiController = context as UIController

        iBinding = BookmarkDrawerViewBinding.inflate(context.inflater,this, true)

        iBinding.uiController = uiController


        iBinding.bookmarkBackButton.setOnClickListener {
            if (!uiModel.isCurrentFolderRoot()) {
                setBookmarksShown(null, true)
                iBinding.listBookmarks.layoutManager?.scrollToPosition(scrollIndex)
            }
        }

        addBookmarkView = findViewById(R.id.menuItemAddBookmark)
        addBookmarkView?.setOnClickListener { uiController.bookmarkButtonClicked() }

        iAdapter = BookmarksAdapter(
                context,
                uiController,
                faviconModel,
                networkScheduler,
                mainScheduler,
                ::showBookmarkMenu,
                ::openBookmark
        )

        iBinding.listBookmarks.apply {
            // Reverse layout if using bottom tool bars
            // LinearLayoutManager.setReverseLayout is also adjusted from BrowserActivity.setupToolBar
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, userPreferences.toolbarsBottom)
            adapter = iAdapter
        }

        // Enable drag & drop but not swipe
        val callback: ItemTouchHelper.Callback = ItemDragDropSwipeHelper(iAdapter, aLongPressDragEnabled = true, aSwipeEnabled = false)
        iItemTouchHelper = ItemTouchHelper(callback)
        iItemTouchHelper?.attachToRecyclerView(iBinding.listBookmarks)

        setBookmarksShown(null, true)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        bookmarksSubscription?.dispose()
        bookmarkUpdateSubscription?.dispose()

        iAdapter.cleanupSubscriptions()
    }

    private fun getTabsManager(): TabsManager = uiController.getTabModel()

    private fun updateBookmarkIndicator(url: String) {
        bookmarkUpdateSubscription?.dispose()
        bookmarkUpdateSubscription = bookmarkModel.isBookmark(url)
            .subscribeOn(databaseScheduler)
            .observeOn(mainScheduler)
            .subscribe { isBookmark ->
                bookmarkUpdateSubscription = null
                addBookmarkView?.isSelected = isBookmark
                addBookmarkView?.isEnabled = !url.isSpecialUrl() && !url.isHomeUri() && !url.isBookmarkUri() && !url.isHistoryUri()
            }
    }

    override fun handleBookmarkDeleted(bookmark: Bookmark) = when (bookmark) {
        is Bookmark.Folder -> setBookmarksShown(null, false)
        is Bookmark.Entry -> iAdapter.deleteItem(BookmarksViewModel(bookmark))
    }

    /**
     *
     */
    private fun setBookmarksShown(folder: String?, animate: Boolean) {
        bookmarksSubscription?.dispose()
        bookmarksSubscription = bookmarkModel.getBookmarksFromFolderSorted(folder)
            .concatWith(Single.defer {
                if (folder == null) {
                    bookmarkModel.getFoldersSorted()
                } else {
                    Single.just(emptyList())
                }
            })
            .toList()
            .map { it.flatten() }
            .subscribeOn(databaseScheduler)
            .observeOn(mainScheduler)
            .subscribe { bookmarksAndFolders ->
                uiModel.currentFolder = folder
                setBookmarkDataSet(bookmarksAndFolders, animate)
                iBinding.textTitle.text = if (folder.isNullOrBlank()) resources.getString(R.string.action_bookmarks) else folder
            }
    }

    /**
     *
     */
    private fun setBookmarkDataSet(items: List<Bookmark>, animate: Boolean) {
        iAdapter.updateItems(items.map { BookmarksViewModel(it) })
        val resource = if (uiModel.isCurrentFolderRoot()) {
            R.drawable.round_star_border_24
        } else {
            R.drawable.ic_action_back
        }

        if (animate) {
            iBinding.bookmarkBackButton.let {
                val transition = AnimationUtils.createRotationTransitionAnimation(it, resource)
                it.startAnimation(transition)
            }
        } else {
            iBinding.bookmarkBackButton.setImageResource(resource)
        }
    }

    /**
     *
     */
    private fun showBookmarkMenu(bookmark: Bookmark): Boolean {
        (context as AppCompatActivity?)?.let {
            when (bookmark) {
                is Bookmark.Folder -> bookmarksDialogBuilder.showBookmarkFolderLongPressedDialog(it, uiController, bookmark)
                is Bookmark.Entry -> bookmarksDialogBuilder.showLongPressedDialogForBookmarkUrl(it, uiController, bookmark)
            }
        }
        return true
    }

    /**
     *
     */
    private fun openBookmark(bookmark: Bookmark) = when (bookmark) {
        is Bookmark.Folder -> {
            scrollIndex = (iBinding.listBookmarks.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            setBookmarksShown(bookmark.title, true)
        }
        is Bookmark.Entry -> uiController.bookmarkItemClicked(bookmark)
    }

    private fun stringContainsItemFromList(inputStr: String, items: Array<String>): Boolean {
        for (i in items.indices) {
            if (inputStr.contains(items[i])) {
                return true
            }
        }
        return false
    }

    /**
     * Show the page tools dialog.
     */
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATION")
    fun showPageToolsDialog(context: Context, userPreferences: UserPreferences) {
        val currentTab = getTabsManager().currentTab ?: return
        val isAllowedAds = allowListModel.isUrlAllowedAds(currentTab.url)
        val whitelistString = if (isAllowedAds) {
            R.string.dialog_adblock_enable_for_site
        } else {
            R.string.dialog_adblock_disable_for_site
        }
        val arrayOfURLs = userPreferences.javaScriptBlocked
        val strgs: Array<String> = if (arrayOfURLs.contains(", ")) {
            arrayOfURLs.split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        } else {
            arrayOfURLs.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        }
        val jsEnabledString = if (userPreferences.javaScriptChoice == JavaScriptChoice.BLACKLIST && !stringContainsItemFromList(currentTab.url, strgs) || userPreferences.javaScriptChoice == JavaScriptChoice.WHITELIST && stringContainsItemFromList(currentTab.url, strgs)) {
            R.string.allow_javascript
        } else{
            R.string.blocked_javascript
        }

        BrowserDialog.showWithIcons(context, context.getString(R.string.dialog_tools_title),
                DialogItem(
                        icon = context.drawable(R.drawable.outline_remove_circle_outline_24),
                        colorTint = context.attrColor(R.attr.colorPrimary).takeIf { isAllowedAds },
                        title = whitelistString
                ) {
                    if (isAllowedAds) {
                        allowListModel.removeUrlFromAllowList(currentTab.url)
                    } else {
                        allowListModel.addUrlToAllowList(currentTab.url)
                    }
                    getTabsManager().currentTab?.reload()
                },
                DialogItem(
                        icon = context.drawable(R.drawable.ic_baseline_code_24),
                        title = R.string.page_source
                ) {
                    currentTab.webView?.evaluateJavascript("""(function() {
                        return "<html>" + document.getElementsByTagName('html')[0].innerHTML + "</html>";
                     })()""".trimMargin()) {
                        // Hacky workaround for weird WebView encoding bug
                        var name = it?.replace("\\u003C", "<")
                        name = name?.replace("\\n", System.getProperty("line.separator").toString())
                        name = name?.replace("\\t", "")
                        name = name?.replace("\\\"", "\"")
                        name = name?.substring(1, name.length - 1)

                        val builder = MaterialAlertDialogBuilder(context)
                        val inflater = activity.layoutInflater
                        builder.setTitle(R.string.page_source)
                        val dialogLayout = inflater.inflate(R.layout.dialog_view_source, null)
                        val codeView: CodeView = dialogLayout.findViewById(R.id.dialog_multi_line)
                        codeView.setText(name)
                        builder.setView(dialogLayout)
                        builder.setNegativeButton(R.string.action_cancel) { _, _ -> }
                        builder.setPositiveButton(R.string.action_ok) { _, _ ->
                            codeView.text?.toString()?.replace("\'", "\\\'")
                            currentTab.loadUrl("javascript:(function() { document.documentElement.innerHTML = '" + codeView.text.toString() + "'; })()")
                        }
                        builder.show()
                    }
                },
                DialogItem(
                        icon= context.drawable(R.drawable.ic_script_add),
                        title = R.string.inspect
                ){
                    val builder = MaterialAlertDialogBuilder(context)
                    val inflater = activity.layoutInflater
                    builder.setTitle(R.string.inspect)
                    val dialogLayout = inflater.inflate(R.layout.dialog_code_editor, null)
                    val codeView: CodeView = dialogLayout.findViewById(R.id.dialog_multi_line)
                    codeView.text.toString()
                    builder.setView(dialogLayout)
                    builder.setNegativeButton(R.string.action_cancel) { _, _ -> }
                    builder.setPositiveButton(R.string.action_ok) { _, _ -> currentTab.loadUrl("javascript:(function() {" + codeView.text.toString() + "})()") }
                    builder.show()
                },
                DialogItem(
                        icon = context.drawable(R.drawable.outline_script_text_key_outline),
                        colorTint = context.attrColor(R.attr.colorPrimary).takeIf { userPreferences.javaScriptChoice == JavaScriptChoice.BLACKLIST && !stringContainsItemFromList(currentTab.url, strgs) || userPreferences.javaScriptChoice == JavaScriptChoice.WHITELIST && stringContainsItemFromList(currentTab.url, strgs) },
                        title = jsEnabledString
                ) {
                    val url = URL(currentTab.url)
                    if (userPreferences.javaScriptChoice != JavaScriptChoice.NONE) {
                        if (!stringContainsItemFromList(currentTab.url, strgs)) {
                            if (userPreferences.javaScriptBlocked == "") {
                                userPreferences.javaScriptBlocked = url.host
                            } else {
                                userPreferences.javaScriptBlocked = userPreferences.javaScriptBlocked + ", " + url.host
                            }
                        } else {
                            if (!userPreferences.javaScriptBlocked.contains(", " + url.host)) {
                                userPreferences.javaScriptBlocked = userPreferences.javaScriptBlocked.replace(url.host, "")
                            } else {
                                userPreferences.javaScriptBlocked = userPreferences.javaScriptBlocked.replace(", " + url.host, "")
                            }
                        }
                    } else {
                        userPreferences.javaScriptChoice = JavaScriptChoice.WHITELIST
                    }
                    getTabsManager().currentTab?.reload()
                    Handler().postDelayed({
                        getTabsManager().currentTab?.reload()
                    }, 250)
                },
                DialogItem(
                        icon = context.drawable(R.drawable.cookie_outline),
                        title = R.string.edit_cookies
                ) {

                    val cookieManager = CookieManager.getInstance()
                    if (cookieManager.getCookie(currentTab.url) != null) {
                        val builder = MaterialAlertDialogBuilder(context)
                        val inflater = activity.layoutInflater
                        builder.setTitle(R.string.site_cookies)
                        val dialogLayout = inflater.inflate(R.layout.dialog_code_editor, null)
                        val codeView: CodeView = dialogLayout.findViewById(R.id.dialog_multi_line)
                        codeView.setText(cookieManager.getCookie(currentTab.url))
                        builder.setView(dialogLayout)
                        builder.setNegativeButton(R.string.action_cancel) { _, _ -> }
                        builder.setPositiveButton(R.string.action_ok) { _, _ ->
                            val cookiesList = codeView.text.toString().split(";")
                            cookiesList.forEach { item ->
                                CookieManager.getInstance().setCookie(currentTab.url, item)
                            }
                        }
                        builder.show()
                    }

                }
        )

    }

    override fun navigateBack() {
        if (uiModel.isCurrentFolderRoot()) {
            uiController.onBackButtonPressed()
        } else {
            setBookmarksShown(null, true)
            iBinding.listBookmarks.layoutManager?.scrollToPosition(scrollIndex)
        }
    }

    override fun handleUpdatedUrl(url: String) {
        updateBookmarkIndicator(url)
        val folder = uiModel.currentFolder
        setBookmarksShown(folder, false)
    }

}
