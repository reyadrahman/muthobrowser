package com.muthopay.muthobrowser.download

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.text.format.Formatter
import android.view.View
import android.webkit.DownloadListener
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muthopay.muthobrowser.R
import com.muthopay.muthobrowser.database.downloads.DownloadsRepository
import com.muthopay.muthobrowser.di.injector
import com.muthopay.muthobrowser.dialog.BrowserDialog.setDialogSize
import com.muthopay.muthobrowser.log.Logger
import com.muthopay.muthobrowser.permissions.PermissionsManager
import com.muthopay.muthobrowser.permissions.PermissionsResultAction
import com.muthopay.muthobrowser.preference.UserPreferences
import com.muthopay.muthobrowser.utils.guessFileName
import javax.inject.Inject

class StyxDownloadListener(context: Activity) : DownloadListener {
    private val mActivity: Activity

    @JvmField
    @Inject
    var userPreferences: UserPreferences? = null

    @JvmField
    @Inject
    var downloadHandler: DownloadHandler? = null

    @JvmField
    @Inject
    var downloadsRepository: DownloadsRepository? = null

    @JvmField
    @Inject
    var logger: Logger? = null

    override fun onDownloadStart(url: String, userAgent: String,
                                 contentDisposition: String, mimetype: String, contentLength: Long) {
        if (Build.VERSION.SDK_INT <= 29) {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                object : PermissionsResultAction() {
                    override fun onGranted() {
                        val fileName = guessFileName(contentDisposition, null, url, mimetype)
                        val downloadSize: String = if (contentLength > 0) {
                            Formatter.formatFileSize(mActivity, contentLength)
                        } else {
                            mActivity.getString(R.string.unknown_size)
                        }
                        val checkBoxView = View.inflate(mActivity, R.layout.download_dialog, null)
                        val checkBox = checkBoxView.findViewById<View>(R.id.checkbox) as CheckBox
                        checkBox.setOnCheckedChangeListener { _, isChecked -> userPreferences!!.showDownloadConfirmation = !isChecked }
                        checkBox.text = mActivity.resources.getString(R.string.dont_ask_again)
                        val dialogClickListener = DialogInterface.OnClickListener { _: DialogInterface?, which: Int ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE ->
                                    downloadHandler?.onDownloadStartNoStream(mActivity as AppCompatActivity, userPreferences!!, url, userAgent, contentDisposition, mimetype)
                                DialogInterface.BUTTON_NEGATIVE -> {
                                }
                            }
                        }
                        if (userPreferences!!.showDownloadConfirmation) {
                            val builder = MaterialAlertDialogBuilder(mActivity) // dialog
                            val message = mActivity.getString(R.string.dialog_download, downloadSize)
                            val dialog: Dialog = builder.setTitle(fileName)
                                    .setMessage(message)
                                    .setView(checkBoxView)
                                    .setPositiveButton(mActivity.resources.getString(R.string.action_download),
                                            dialogClickListener)
                                    .setNegativeButton(mActivity.resources.getString(R.string.action_cancel),
                                            dialogClickListener).show()
                            setDialogSize(mActivity, dialog)
                            logger!!.log(TAG, "Downloading: $fileName")
                        } else {
                            downloadHandler!!.onDownloadStartNoStream(mActivity as AppCompatActivity, userPreferences!!, url, userAgent, contentDisposition, mimetype)
                        }
                    }

                    override fun onDenied(permission: String) {
                        //
                    }
                })
        } else {
            val fileName = guessFileName(contentDisposition, null, url, mimetype)
            val downloadSize: String = if (contentLength > 0) {
                Formatter.formatFileSize(mActivity, contentLength)
            } else {
                mActivity.getString(R.string.unknown_size)
            }
            val checkBoxView = View.inflate(mActivity, R.layout.download_dialog, null)
            val checkBox = checkBoxView.findViewById<View>(R.id.checkbox) as CheckBox
            checkBox.setOnCheckedChangeListener { _, isChecked -> userPreferences!!.showDownloadConfirmation = !isChecked }
            checkBox.text = mActivity.resources.getString(R.string.dont_ask_again)
            val dialogClickListener = DialogInterface.OnClickListener { _: DialogInterface?, which: Int ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE ->
                        downloadHandler?.onDownloadStartNoStream(mActivity as AppCompatActivity, userPreferences!!, url, userAgent, contentDisposition, mimetype)
                    DialogInterface.BUTTON_NEGATIVE -> {
                    }
                }
            }
            if (userPreferences!!.showDownloadConfirmation) {
                val builder = MaterialAlertDialogBuilder(mActivity) // dialog
                val message = mActivity.getString(R.string.dialog_download, downloadSize)
                val dialog: Dialog = builder.setTitle(fileName)
                    .setMessage(message)
                    .setView(checkBoxView)
                    .setPositiveButton(mActivity.resources.getString(R.string.action_download),
                        dialogClickListener)
                    .setNegativeButton(mActivity.resources.getString(R.string.action_cancel),
                        dialogClickListener).show()
                setDialogSize(mActivity, dialog)
                logger!!.log(TAG, "Downloading: $fileName")
            } else {
                downloadHandler!!.onDownloadStartNoStream(mActivity as AppCompatActivity, userPreferences!!, url, userAgent, contentDisposition, mimetype)
            }
        }
    }

    companion object {
        private const val TAG = "StyxDownloader"
    }

    init {
        context.injector.inject(this)
        mActivity = context
    }
}
