package com.aknown389.dm.pageView.notification.diffUtil

import androidx.recyclerview.widget.DiffUtil
import com.aknown389.dm.pageView.notification.models.NotificationDataClass

class NotificationDiffUtil(val new:ArrayList<NotificationDataClass>, val old:ArrayList<NotificationDataClass>):DiffUtil.Callback() {
    override fun getOldListSize() = old.size

    override fun getNewListSize() = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = new[newItemPosition].id == old[oldItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val item1 = new[newItemPosition]
        val item2 = old[oldItemPosition]
        return when{
            item1.seen != item2.seen -> false
            item1.notificationType != item2.notificationType -> false
            item1.NoOfLike != item2.NoOfLike -> false
            item1.NoOfComment != item2.NoOfComment -> false
            item1.description != item2.description -> false
            item1.subjectFullName != item2.subjectFullName -> false
            item1.subjectImage != item2.subjectImage -> false
            item1.user != item2.user -> false
            else -> true
        }
    }
}