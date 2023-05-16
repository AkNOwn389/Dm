package com.aknown389.dm.pageView.mainSearch.utility

import android.content.Context
import android.content.res.Configuration
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aknown389.dm.api.retroInstance.PostInstance
import com.aknown389.dm.pageView.mainSearch.dataClass.MainSearchItemData
import com.aknown389.dm.models.postmodel.LikesPostBodyModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LikePost(
    private val token:String,
    private val data: MainSearchItemData,
    private val holder: MainSearchViewHolder,
    private val context: Context,
    private val adapter: Adapter,
    private val reactionType:String,
    private val type:String
) {

    init {
        (context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.Main) {
            val body = LikesPostBodyModel(data.id.toString(), "posts", reactionType, type)
            val response = try {
                PostInstance.api.likepost(token, body)
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful){
                val res = response.body()!!
                if (res.message == "post reacted"){
                    holder.noOfLike?.visibility = View.VISIBLE
                    holder.noOfLike?.text = response.body()!!.postLikes.toString()
                    MainSearchGlobalSetter.afterReactionReasponse(holder, res, data)
                }
                if (res.message == "post unReacted"){
                    MainSearchGlobalSetter.afterUnReactResponse(holder, res, data)
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