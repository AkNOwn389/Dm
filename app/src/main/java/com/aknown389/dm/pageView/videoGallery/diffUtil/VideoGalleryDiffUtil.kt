package com.aknown389.dm.pageView.videoGallery.diffUtil

import androidx.recyclerview.widget.DiffUtil
import com.aknown389.dm.models.global.VideoUrl

class VideoGalleryDiffUtil(val old:ArrayList<VideoUrl>, val new:ArrayList<VideoUrl>):DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].id == new[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val luma = old[oldItemPosition]
        val bago = new[newItemPosition]
        return when{
            luma.height != bago.height -> false
            luma.width != bago.width -> false
            luma.original != bago.original -> false
            luma.playbackPosition != bago.playbackPosition -> false
            luma.playbackUrl != bago.playbackUrl -> false
            luma.thumbnail != bago.thumbnail -> false
            luma.w1000 != bago.w1000 -> false
            luma.w500 != bago.w500 -> false
            luma.w250 != bago.w250 -> false
            else -> true
        }
    }
}