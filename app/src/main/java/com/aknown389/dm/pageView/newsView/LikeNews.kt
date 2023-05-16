package com.aknown389.dm.pageView.newsView

import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.widget.Toast
import com.aknown389.dm.api.retroInstance.RetrofitInstance
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.db.local.NewsDataEntities
import com.aknown389.dm.models.postmodel.LikesPostBodyModel
import com.aknown389.dm.models.postmodel.LikesPostResponseModel
import com.aknown389.dm.pageView.newsView.NewsGlobalSetter.afterReactionReasponse
import com.aknown389.dm.pageView.newsView.NewsGlobalSetter.afterUnReactResponse
import com.aknown389.dm.pageView.newsView.NewsViewHolder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LikeNews(
    private val context: Context,
    private val holder: NewsViewHolder,
    private val currentItem: NewsDataEntities,
    private val token: String,
    private val postType:String,
    private val reactionType:String,
    private val reactType:String,
) {

    private lateinit var dataBase: AppDataBase
    init {
        dataBase = AppDataBase.getDatabase(context)
        val body = LikesPostBodyModel(currentItem.id!!, postType = postType, reactionType, type = reactType)
        val request = RetrofitInstance.retrofitBuilder.likepostC(token = token, body)
        request.enqueue(object : Callback<LikesPostResponseModel?> {
            override fun onResponse(
                call: Call<LikesPostResponseModel?>,
                response: Response<LikesPostResponseModel?>
            ) {
                if (response.isSuccessful){
                    val res = response.body()!!
                    if (res.message == "post reacted"){
                        reportLike(currentItem.id)
                        holder.noOflikes?.visibility = View.VISIBLE
                        holder.noOflikes?.text = response.body()!!.postLikes.toString()
                        afterReactionReasponse(holder, res, currentItem)
                    } else if (res.message == "post unReacted"){
                        reportUnLike(currentItem.id)
                        afterUnReactResponse(holder, res, currentItem)
                        holder.noOflikes?.text = response.body()!!.postLikes.toString()
                        when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                            Configuration.UI_MODE_NIGHT_NO -> {
                                // Night mode is not active on device
                                holder.likeBtn?.returnToDefaultReaction()
                                holder.noOflikes?.text = res.postLikes.toString()
                            }
                            Configuration.UI_MODE_NIGHT_YES -> {
                                // Night mode is active on device
                                holder.likeBtn?.returnToDefaultReaction()
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
                Toast.makeText(context, "No internet", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun reportLike(id:String){
        val result = dataBase.newDao().reportLike(id)
        if (!result){
            Toast.makeText(context, "Error updating in db", Toast.LENGTH_SHORT).show()
        }
    }
    private fun reportUnLike(id:String){
        val result = dataBase.newDao().reportUnLike(id)
        if (!result){
            Toast.makeText(context, "Error updating in db", Toast.LENGTH_SHORT).show()
        }
    }

}