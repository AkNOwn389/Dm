package com.aknown389.dm.pageView.videoFeed

import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.widget.Toast
import com.aknown389.dm.api.retroInstance.RetrofitInstance
import com.aknown389.dm.models.postmodel.LikesPostBodyModel
import com.aknown389.dm.models.postmodel.LikesPostResponseModel
import com.aknown389.dm.models.videoFeedModels.VideoDataModels
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LikeVideo(
    private val holder: VideoFeedViewHolder,
    private val currentItem: VideoDataModels,
    private val token: String,
    private val context: Context,
    private val reactionType:String,
    private val reactType:String


) {
    init {
        val body = LikesPostBodyModel(currentItem.id!!, postType = "videos", reactionType = reactionType, type = reactType)
        val request = RetrofitInstance.retrofitBuilder.likepostC(token = token, body)
        request.enqueue(object : Callback<LikesPostResponseModel?> {
            override fun onResponse(
                call: Call<LikesPostResponseModel?>,
                response: Response<LikesPostResponseModel?>
            ) {
                if (response.isSuccessful){
                    val res = response.body()!!
                    if (res.message == "post reacted"){
                        holder.noOfLike?.visibility = View.VISIBLE
                        holder.noOfLike?.text = response.body()!!.postLikes.toString()
                        VideoFeedGlobalSetter.afterReactionReasponse(holder, res, currentItem)
                    }
                    if (res.message == "post unReacted"){
                        VideoFeedGlobalSetter.afterUnReactResponse(holder, res, currentItem)
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

            override fun onFailure(call: Call<LikesPostResponseModel?>, t: Throwable) {
                Toast.makeText(context, "Network failure", Toast.LENGTH_SHORT).show()
            }
        })
    }
}