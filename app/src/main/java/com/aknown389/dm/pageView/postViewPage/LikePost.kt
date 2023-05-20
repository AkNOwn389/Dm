package com.aknown389.dm.pageView.postViewPage

import android.content.Context
import android.content.res.Configuration
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.aknown389.dm.activities.PostViewActivity
import com.aknown389.dm.pageView.postViewPage.PostViewGlobalSetter.afterReactionResponse
import com.aknown389.dm.api.retroInstance.PostInstance
import com.aknown389.dm.models.postmodel.LikesPostBodyModel
import com.aknown389.dm.pageView.postViewPage.models.ToDisplayDataModel
import com.aknown389.dm.pageView.postViewPage.PostViewGlobalSetter.afterUnReactResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LikePost(
    private val holder: PostViewHolder,
    private val data: ToDisplayDataModel,
    private val token: String,
    private val context:Context,
    private val postType:String,
    private val reactionType:String,
    private val type:String
) {
    init {
        (context as? PostViewActivity)?.lifecycleScope?.launch(Dispatchers.Main) {
            val body = LikesPostBodyModel(data.ImageOrVideoId.toString(), postType, reactionType, type = type)
            val response = try {
                PostInstance.api.likepost(token, body)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful){
                val res = response.body()!!
                withContext(coroutineContext){
                    if (res.message == "post reacted"){
                        holder.noOfLike?.visibility = View.VISIBLE
                        holder.noOfLike?.text = response.body()!!.postLikes.toString()
                        afterReactionResponse(holder, res, data)
                    }
                    if (res.message == "post unReacted"){
                        afterUnReactResponse(holder, res, data)
                        holder.noOfLike?.text = response.body()!!.postLikes.toString()
                        when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                            Configuration.UI_MODE_NIGHT_NO -> {
                                // Night mode is not active on device
                                holder.likePost?.returnToDefaultReaction()
                                holder.noOfLike?.text = res.postLikes.toString()
                            }
                            Configuration.UI_MODE_NIGHT_YES -> {
                                // Night mode is active on device
                                holder.likePost?.returnToDefaultReaction()
                                holder.noOfLike?.text = res.postLikes.toString()
                            }
                        }
                    }
                    if (response.body()!!.postLikes == 0){
                        holder.noOfLike?.visibility = View.GONE
                    }
                }
            }
        }
    }
}