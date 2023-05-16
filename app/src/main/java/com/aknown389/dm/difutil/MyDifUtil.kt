package com.aknown389.dm.difutil

import androidx.recyclerview.widget.DiffUtil
import com.aknown389.dm.models.homepostmodels.PostDataModel

class MyDifUtil(
    private val oldItem: List<PostDataModel>,
    private val newItem: List<PostDataModel>): DiffUtil.Callback() {
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
            oldItem[oldItemPosition].creator != newItem[newItemPosition].creator -> {false}
            oldItem[oldItemPosition].media_type != newItem[newItemPosition].media_type -> {false}
            else -> true
        }
    }
}