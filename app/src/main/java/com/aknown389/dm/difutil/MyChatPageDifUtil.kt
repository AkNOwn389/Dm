package com.aknown389.dm.difutil

import androidx.recyclerview.widget.DiffUtil
import com.aknown389.dm.models.chatmodels.NullableMessage

class MyChatPageDifUtil(
    private val oldItem: List<NullableMessage>,
    private val newItem: List<NullableMessage>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldItem.size
    }

    override fun getNewListSize(): Int {
        return newItem.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItem[oldItemPosition].id == newItem[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldItem[oldItemPosition].me != newItem[newItemPosition].me -> {false}
            oldItem[oldItemPosition].seen != newItem[newItemPosition].seen -> {false}
            oldItem[oldItemPosition].date_time != newItem[newItemPosition].date_time -> {false}
            oldItem[oldItemPosition].sender != newItem[newItemPosition].sender -> {false}
            oldItem[oldItemPosition].receiver != newItem[newItemPosition].receiver -> {false}
            oldItem[oldItemPosition].message_body != newItem[newItemPosition].message_body -> {false}
            oldItem[oldItemPosition].username != newItem[newItemPosition].username -> {false}
            oldItem[oldItemPosition].id != newItem[newItemPosition].id-> {false}
            else -> true
        }
    }
}