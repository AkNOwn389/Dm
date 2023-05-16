package com.aknown389.dm.pageView.notification.utilities

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aknown389.dm.R
import com.aknown389.dm.pageView.notification.models.NotificationDataClass
import com.aknown389.dm.pageView.notification.diffUtil.NotificationDiffUtil
import com.aknown389.dm.pageView.notification.items.CommentNotification
import com.aknown389.dm.pageView.notification.items.FollowingOrFollowItem
import com.aknown389.dm.pageView.notification.items.LikePostNotification
import com.aknown389.dm.utils.DataManager

class NotificationAdapter():RecyclerView.Adapter<ViewHolder>() {
    private lateinit var context:Context
    private lateinit var token:String
    private lateinit var manager:DataManager
    private lateinit var parent: ViewGroup
    val notificationList:ArrayList<NotificationDataClass> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        this.context = parent.context
        this.manager = DataManager(parent.context)
        this.parent = parent
        this.token = manager.getAccessToken().toString()
        return when(viewType){
            1 -> NotificationsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_notification_for_following_or_follow, parent, false))
            2 -> NotificationsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_notification_for_posts_likes, parent, false))
            3 -> NotificationsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_notification_for_posts_comments, parent, false))
            else -> NotificationsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_notification_for_following_or_follow, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun getItemViewType(position: Int): Int {
        return notificationList[position].notificationType
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = notificationList[position]
        (holder as NotificationsViewHolder).viewHolderData = data
        when(data.notificationType){
            1 -> {
                FollowingOrFollowItem(
                    holder = holder,
                    adapter = this,
                    context = context,
                    parent = parent,
                    notifications = notificationList
                )
            }
            2 -> {
                LikePostNotification(
                    holder = holder,
                    adapter = this,
                    context = context,
                    parent = parent,
                    notifications = notificationList
                )
            }
            3 -> {
                CommentNotification(
                    holder = holder,
                    adapter = this,
                    context = context,
                    parent = parent,
                    notifications = notificationList
                )
            }
        }
    }

    fun setData(new:ArrayList<NotificationDataClass>){
        val diffResult = DiffUtil.calculateDiff(NotificationDiffUtil(new = new, old = notificationList))
        notificationList.addAll(new)
        diffResult.dispatchUpdatesTo(this)
    }
}