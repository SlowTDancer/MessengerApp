package com.ikhut.messengerapp.presentation.components

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import com.ikhut.messengerapp.R

class LoadingOverlay(private val context: Context) {
    private var loadingDialog: Dialog? = null

    fun show() {
        dismiss()

        loadingDialog = Dialog(context, R.style.LoadingDialogStyle).apply {
            setContentView(R.layout.dialog_loading_overlay)

            setCancelable(false)
            setCanceledOnTouchOutside(false)

            window?.let { window ->
                window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
                )
                window.setGravity(Gravity.CENTER)
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }

            show()
        }
    }

    fun dismiss() {
        loadingDialog?.let { dialog ->
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
        loadingDialog = null
    }

    fun isShowing(): Boolean {
        return loadingDialog?.isShowing == true
    }
}