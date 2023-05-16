package com.aknown389.dm.pageView.mainSearch.diffUtil

import androidx.recyclerview.widget.DiffUtil
import com.aknown389.dm.pageView.mainSearch.dataClass.MainSearchItemData

class MainSearchDiffUtil(val old:ArrayList<MainSearchItemData>, val new:ArrayList<MainSearchItemData>):DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].id == new[newItemPosition].id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when{
            old[oldItemPosition].is_like != new[newItemPosition].is_like -> false
            old[oldItemPosition].searchType != new[newItemPosition].searchType -> false
            old[oldItemPosition].caption != new[newItemPosition].caption -> false
            old[oldItemPosition].reactionType != new[newItemPosition].reactionType -> false
            old[oldItemPosition].isFollowed != new[newItemPosition].isFollowed -> false
            old[oldItemPosition].your_avatar != new[newItemPosition].your_avatar -> false
            old[oldItemPosition].NoOfcomment != new[newItemPosition].NoOfcomment -> false
            old[oldItemPosition].NoOflike != new[newItemPosition].NoOflike -> false
            else -> true
        }
    }
}