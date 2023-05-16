package com.aknown389.dm.difutil

import androidx.recyclerview.widget.DiffUtil
import com.aknown389.dm.models.homepostmodels.PostDataModel

class HomeFeedDiffUtil:DiffUtil.ItemCallback<PostDataModel>() {
    override fun areItemsTheSame(oldItem: PostDataModel, newItem: PostDataModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PostDataModel, newItem: PostDataModel): Boolean {
        return when{
            oldItem.is_like != newItem.is_like -> false
            oldItem.NoOflike != newItem.NoOflike -> false
            oldItem.NoOfcomment != newItem.NoOfcomment -> false
            oldItem.creator_avatar != newItem.creator_avatar -> false
            oldItem.creator_full_name != newItem.creator_full_name -> false
            oldItem.description != newItem.description -> false
            oldItem.privacy != newItem.privacy -> false
            oldItem.your_avatar != newItem.your_avatar -> false
            else -> true
        }
    }
}