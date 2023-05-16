package com.aknown389.dm.pageView.homeFeed.diffUtil

import androidx.recyclerview.widget.DiffUtil
import com.aknown389.dm.models.homepostmodels.PostDataModel

class HomeFeedDiffUtil(val old:List<PostDataModel>, val new:List<PostDataModel>):DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return  new[newItemPosition].id == old[oldItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when{
            old[oldItemPosition].is_like != new[newItemPosition].is_like -> false
            old[oldItemPosition].NoOfcomment != new[newItemPosition].NoOfcomment -> false
            old[oldItemPosition].NoOflike != new[newItemPosition].NoOflike -> false
            old[oldItemPosition].creator_avatar != new[newItemPosition].creator_avatar -> false
            old[oldItemPosition].description != new[newItemPosition].description -> false
            old[oldItemPosition].privacy != new[newItemPosition].privacy -> false
            old[oldItemPosition].reactionType != new[newItemPosition].reactionType -> false
            old[oldItemPosition].reactions != new[newItemPosition].reactions -> false
            old[oldItemPosition].your_avatar != new[newItemPosition].your_avatar -> false
            else -> true
        }
    }
}