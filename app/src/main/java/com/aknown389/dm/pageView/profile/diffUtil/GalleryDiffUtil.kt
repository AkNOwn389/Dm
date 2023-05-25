package com.aknown389.dm.pageView.profile.diffUtil

import androidx.recyclerview.widget.DiffUtil
import com.aknown389.dm.models.global.ImageUrl

class GalleryDiffUtil(val new:List<ImageUrl>, val old:List<ImageUrl>):DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].id == new[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when{
            new[newItemPosition].isLike != old[oldItemPosition].isLike -> false
            new[newItemPosition].noOfComment != old[oldItemPosition].noOfComment -> false
            new[newItemPosition].imgW1000 != old[oldItemPosition].imgW1000 -> false
            new[newItemPosition].imgW500 != old[oldItemPosition].imgW500 -> false
            new[newItemPosition].imgW250 != old[oldItemPosition].imgW250 -> false
            new[newItemPosition].noOfLike != old[oldItemPosition].noOfLike -> false
            new[newItemPosition].original != old[oldItemPosition].original -> false
            new[newItemPosition].reactionType != old[oldItemPosition].reactionType -> false
            new[newItemPosition].reactions != old[oldItemPosition].reactions -> false
            else -> true
        }
    }
}