package com.aknown389.dm.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.aknown389.dm.R
import javax.inject.Inject

@SuppressLint("InflateParams")
class DownloadingAlertDialog @Inject constructor(context: Context) {
    private var dialog: AlertDialog? = null
    private var builder: AlertDialog.Builder
    private var inflater: LayoutInflater
    private var dialogView: View

    init {
        builder = AlertDialog.Builder(context)
        inflater = LayoutInflater.from(context)
        dialogView = inflater.inflate(R.layout.dialog_dowload_loading, null, false)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
    }

    fun start(){
        dialog?.show()
    }
    fun dismiss(){
        dialog?.dismiss()
    }
}