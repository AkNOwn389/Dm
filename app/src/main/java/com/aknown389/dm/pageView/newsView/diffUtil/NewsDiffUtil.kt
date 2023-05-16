package com.aknown389.dm.pageView.newsView.diffUtil

import androidx.recyclerview.widget.DiffUtil
import com.aknown389.dm.db.local.NewsDataEntities

class NewsDiffUtil(val new:List<NewsDataEntities>, val old:List<NewsDataEntities>):DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return new[newItemPosition].id == old[oldItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when{
            new[newItemPosition].is_like != old[oldItemPosition].is_like -> false
            new[newItemPosition].noOfComment != old[oldItemPosition].noOfComment -> false
            new[newItemPosition].noOfLike != old[oldItemPosition].noOfLike -> false
            new[newItemPosition].description != old[oldItemPosition].description -> false
            new[newItemPosition].reactionType != old[oldItemPosition].reactionType -> false
            new[newItemPosition].content != old[oldItemPosition].content -> false
            else -> true
        }
    }
}