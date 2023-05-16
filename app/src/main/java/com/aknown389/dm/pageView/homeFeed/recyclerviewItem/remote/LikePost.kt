package com.aknown389.dm.pageView.homeFeed.recyclerviewItem.remote

import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aknown389.dm.pageView.homeFeed.utility.GlobalSetter.afterReactionResponse
import com.aknown389.dm.pageView.homeFeed.utility.GlobalSetter.afterUnReactResponse
import com.aknown389.dm.api.retroInstance.RetrofitInstance
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.models.postmodel.LikesPostBodyModel
import com.aknown389.dm.models.postmodel.LikesPostResponseModel
import com.aknown389.dm.models.homepostmodels.PostDataModel
import com.aknown389.dm.pageView.homeFeed.utility.HomeFeedRecyclerViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LikePost(
    private val holder: HomeFeedRecyclerViewHolder,
    private val currentItem: PostDataModel,
    private val postType: String,
    private val token: String,
    private val reactionType: String,
    private val context: Context,
    private val reactType: String,
) {
    private var dataBase: AppDataBase = AppDataBase.getDatabase(context)

    init {
        val body = LikesPostBodyModel(currentItem.id, postType, reactionType, reactType)
        val request = RetrofitInstance.retrofitBuilder.likepostC(token = token, body)
        request.enqueue(object : Callback<LikesPostResponseModel?> {
            override fun onResponse(
                call: Call<LikesPostResponseModel?>,
                response: Response<LikesPostResponseModel?>
            ) {
                if (response.isSuccessful){
                        val res = response.body()!!
                        if (res.message == "post reacted"){
                            holder.noOflikes?.visibility = View.VISIBLE
                            holder.noOflikes?.text = response.body()!!.postLikes.toString()
                            afterReactionResponse(holder, res, currentItem)
                            reportLike(currentItem.id)
                        }
                        if (res.message == "post unReacted"){
                            reportUnLike(currentItem.id)
                            afterUnReactResponse(holder, res, currentItem)
                            holder.noOflikes?.text = response.body()!!.postLikes.toString()
                            when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                                Configuration.UI_MODE_NIGHT_NO -> {
                                    // Night mode is not active on device
                                    holder.likePost?.returnToDefaultReaction()
                                    holder.noOflikes?.text = res.postLikes.toString()
                                }
                                Configuration.UI_MODE_NIGHT_YES -> {
                                    // Night mode is active on device
                                    holder.likePost?.returnToDefaultReaction()
                                    holder.noOflikes?.text = res.postLikes.toString()
                                }
                            }
                        }
                        if (response.body()!!.postLikes == 0){
                            holder.noOflikes?.visibility = View.GONE
                        }

                }
            }

            override fun onFailure(call: Call<LikesPostResponseModel?>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun reportLike(id:String){
        (context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.IO) {
            val result = dataBase.homeFeedDao().reportLike(id)
            if (!result){
                Toast.makeText(context, "Error updating from db", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun reportUnLike(id:String){
        (context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.IO) {
            val result = dataBase.homeFeedDao().reportUnLike(id)
            if (!result){
                Toast.makeText(context, "Error updating from db", Toast.LENGTH_SHORT).show()
            }
        }
    }
}