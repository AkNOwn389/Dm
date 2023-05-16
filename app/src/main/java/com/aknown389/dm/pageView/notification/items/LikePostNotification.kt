package com.aknown389.dm.pageView.notification.items

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.aknown389.dm.R
import com.aknown389.dm.activities.PostViewActivity
import com.aknown389.dm.pageView.notification.models.NotificationDataClass
import com.aknown389.dm.pageView.notification.utilities.NotificationAdapter
import com.aknown389.dm.pageView.notification.dailogs.Dialog
import com.aknown389.dm.pageView.notification.remote.ReportSeen
import com.aknown389.dm.pageView.notification.utilities.NotificationsViewHolder
import javax.inject.Inject

class LikePostNotification @Inject constructor(
    val holder: NotificationsViewHolder,
    val adapter: NotificationAdapter,
    val context: Context,
    val parent: ViewGroup,
    val notifications:ArrayList<NotificationDataClass>
) {
    private var data: NotificationDataClass = holder.viewHolderData!!

    init {
        if (!data.seen){
        holder.item2Body?.setBackgroundResource(R.drawable.chat_item_ripple_2)
    }else{
        holder.item2Body?.setBackgroundResource(R.drawable.chat_item_ripple)
    }
    holder.item2?.text = data.title
    holder.item2Body?.setOnClickListener {
        if (!data.seen){
            ReportSeen(data.id, constraintLayout =  holder.item2Body!!, context = context)
        }
        (context as? AppCompatActivity)?.let {
            val intent = Intent(it, PostViewActivity::class.java)
            intent.putExtra("postId", data.subject_id)
            intent.putExtra("userAvatar", data.subjectImage)
            intent.putExtra("username", data.user)
            intent.putExtra("user_full_name", data.subjectFullName)
            intent.putExtra("noOfComment", data.NoOfComment)
            intent.putExtra("like", data.NoOfLike)
            it.startActivity(intent)
        }
    }
    holder.item2Body?.setOnLongClickListener {
        Dialog(adapter = adapter, constraintLayout = holder.item1Body!!, context = context, parent = parent, holder = holder, notification = notifications)
        true
    }
    }
}