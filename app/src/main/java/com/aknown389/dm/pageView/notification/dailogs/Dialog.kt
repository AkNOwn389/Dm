package com.aknown389.dm.pageView.notification.dailogs

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.aknown389.dm.R
import com.aknown389.dm.pageView.notification.models.NotificationDataClass
import com.aknown389.dm.pageView.notification.utilities.NotificationAdapter
import com.aknown389.dm.pageView.notification.remote.DeleteNotification
import com.aknown389.dm.pageView.notification.remote.ReportSeen
import com.aknown389.dm.pageView.notification.utilities.NotificationsViewHolder
import com.google.android.material.bottomsheet.BottomSheetDialog
import javax.inject.Inject

class Dialog @Inject constructor(
    val notification: ArrayList<NotificationDataClass>,
    val holder: NotificationsViewHolder,
    val constraintLayout: ConstraintLayout,
    val context: Context,
    val parent:ViewGroup,
    val adapter: NotificationAdapter,
) {

    private lateinit var data: NotificationDataClass
    init {
        data = holder.viewHolderData!!
        showDialog()
    }
    private fun showDialog(){
        val dialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_notification, parent,  false)
        val delete: TextView? = view.findViewById(R.id.delete)
        val seen: TextView? = view.findViewById(R.id.seen)
        val report: TextView? = view.findViewById(R.id.report)
        dialog.setContentView(view)
        dialog.show()
        delete?.setOnClickListener {
            DeleteNotification(data, holder = holder, adapter = adapter, context = context, notifications = notification)
            dialog.dismiss()
        }
        seen?.setOnClickListener {
            ReportSeen(data.id, constraintLayout = constraintLayout, context = context)
        }
        report?.setOnClickListener {

        }
    }
}