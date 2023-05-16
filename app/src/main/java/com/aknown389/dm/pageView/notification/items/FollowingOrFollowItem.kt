package com.aknown389.dm.pageView.notification.items

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.aknown389.dm.R
import com.aknown389.dm.activities.UserViewActivity
import com.aknown389.dm.pageView.notification.models.NotificationDataClass
import com.aknown389.dm.pageView.notification.utilities.NotificationAdapter
import com.aknown389.dm.pageView.notification.dailogs.Dialog
import com.aknown389.dm.pageView.notification.remote.ReportSeen
import com.aknown389.dm.pageView.notification.utilities.NotificationsViewHolder
import javax.inject.Inject

class FollowingOrFollowItem @Inject constructor(
    val holder: NotificationsViewHolder,
    val adapter: NotificationAdapter,
    val context: Context,
    val parent:ViewGroup,
    val notifications:ArrayList<NotificationDataClass>
) {
    private var data: NotificationDataClass = holder.viewHolderData!!

    init {
        if (!data.seen){
            holder.item1Body?.setBackgroundResource(R.drawable.chat_item_ripple_2)
        }else{
            holder.item1Body?.setBackgroundResource(R.drawable.chat_item_ripple)
        }
        holder.item1?.text = data.title
        holder.item1Body?.setOnClickListener {
            if (!data.seen){
                ReportSeen(id = data.id, constraintLayout = holder.item1Body!!, context = context)
            }
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java).putExtra("username", data.subject_id)
                it.startActivity(intent)
            }
        }
        Glide.with(context)
            .load(data.subjectImage)
            .placeholder(R.drawable.progress_animation)
            .error(R.mipmap.greybg)
            .into(holder.subjectImage!!)
        holder.item1Body?.setOnLongClickListener {
            Dialog(adapter = adapter, constraintLayout = holder.item1Body!!, context = context, parent = parent, holder = holder, notification = notifications)
            true
        }
    }
}