package com.aknown389.dm.difutil

import androidx.recyclerview.widget.DiffUtil
import com.aknown389.dm.models.chatmodels.Data

class ChatAdapterDiffUtil(private val messageItem:List<Data>, private val newMessageItem: List<Data>):DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return messageItem.size
    }

    override fun getNewListSize(): Int {
        return newMessageItem.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return messageItem[oldItemPosition].id == newMessageItem[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return messageItem[oldItemPosition].id == newMessageItem[newItemPosition].id
    }
}