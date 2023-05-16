package com.aknown389.dm.pageView.notification.utilities

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aknown389.dm.R
import com.aknown389.dm.pageView.notification.models.NotificationDataClass

class NotificationsViewHolder(view:View):ViewHolder(view) {
    var viewHolderData: NotificationDataClass? = null
    @JvmField
    var subjectImage: ImageView? = null
    @JvmField
    var item1: TextView? = null
    @JvmField
    var item2: TextView? = null
    @JvmField
    var item3: TextView? = null
    @JvmField
    var item1Body:ConstraintLayout? = null
    @JvmField
    var item2Body:ConstraintLayout? = null
    @JvmField
    var item3Body:ConstraintLayout? = null
    init {
        subjectImage = itemView.findViewById(R.id.notifsubjectuserImage)
        item1 = itemView.findViewById(R.id.notifTitle)
        item2 = itemView.findViewById(R.id.notifLikeTitle)
        item3 = itemView.findViewById(R.id.notifCommentedTitle)
        item1Body = itemView.findViewById(R.id.notificationitem1body)
        item2Body = itemView.findViewById(R.id.notificationitem2body)
        item3Body = itemView.findViewById(R.id.notificationitem3body)
    }
}