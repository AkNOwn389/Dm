package com.aknown389.dm.pageView.homeFeed.recyclerviewItem.remote

import androidx.core.view.isVisible
import com.aknown389.dm.api.retroInstance.PostInstance
import com.aknown389.dm.models.homepostmodels.PostDataModel
import com.aknown389.dm.models.postmodel.CommentBody
import com.aknown389.dm.pageView.homeFeed.utility.HomeFeedRecyclerViewHolder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Comment(
    val holder: HomeFeedRecyclerViewHolder,
    val  currentItem: PostDataModel,
    val token:String
) {
    init {
        comment(holder, currentItem)
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun comment(holder: HomeFeedRecyclerViewHolder, currentItem: PostDataModel) {
        val mycomment = holder.edtComment?.text.toString()
        if (mycomment.isEmpty()) {
            return
        }
        val body = CommentBody(postId = currentItem.id!!, comment = mycomment)
        GlobalScope.launch {
            val response = try {
                PostInstance.api.comment(token, body)
            } catch (e: Exception) {
                e.stackTrace
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                val resBody = response.body()!!
                if (resBody.status) {
                    withContext(Dispatchers.Main) {
                        currentItem.NoOfcomment?.plus(1)
                        holder.noOfComments?.isVisible = true
                        holder.viewallcomment?.isVisible = true
                        holder.noOfComments?.text = "${currentItem.NoOfcomment?.plus(1)} comments"
                        holder.viewallcomment?.text =
                            "View All ${currentItem.NoOfcomment?.plus(1)} comments"
                        holder.edtComment?.setText("")
                    }
                }
            }
        }
    }
}