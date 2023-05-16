package com.aknown389.dm.pageView.commentView.items

import android.content.Context
import com.aknown389.dm.R
import com.aknown389.dm.pageView.commentView.models.Data
import com.aknown389.dm.pageView.commentView.utility.CommentViewHolder
import javax.inject.Inject

class DeletedComment @Inject constructor(
    private val holder: CommentViewHolder,
    private var currentItem: Data,
    private val context: Context,
) {
    init {
        loadUI()
    }

    private fun loadUI() {
        if (currentItem.me == true){
            holder.commentUsername?.text = context.getString(R.string.me)
        }else{
            holder.commentUsername?.text = currentItem.userFullName
        }
    }
}