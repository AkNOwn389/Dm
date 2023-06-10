package com.aknown389.dm.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import com.aknown389.dm.R
import javax.inject.Inject

@SuppressLint("InflateParams")
class DialogLoginLoading @Inject constructor(context: Context) {
    private var dialog: AlertDialog? = null
    private var builder: AlertDialog.Builder
    private var inflater: LayoutInflater
    private var dialogView: View

    init {
        builder = AlertDialog.Builder(context)
        inflater = LayoutInflater.from(context)
        dialogView = inflater.inflate(R.layout.dialog_login_loading, null, false)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        if (dialog?.window != null){
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(0))
        }
    }

    fun start(){
        if (!dialog?.isShowing!!){
            dialog?.show()
        }
    }
    fun dismiss(){
        if (dialog?.isShowing==true){
            dialog?.dismiss()
        }
    }
}