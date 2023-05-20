package com.aknown389.dm.pageView.photoView.remote

import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aknown389.dm.api.retroInstance.RetrofitInstance
import com.aknown389.dm.databinding.ActivityPhotoViewBinding
import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.postmodel.LikesPostBodyModel
import com.aknown389.dm.models.postmodel.LikesPostResponseModel
import com.aknown389.dm.pageView.newsView.NewsGlobalSetter
import com.aknown389.dm.pageView.photoView.models.Parcel
import com.aknown389.dm.pageView.photoView.utilities.Setter
import com.aknown389.dm.pageView.photoView.utilities.Setter.afterReactionReasponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class LikeImagePost @Inject constructor(
    private val holder: ActivityPhotoViewBinding?,
    private val currentItem: ImageUrl,
    private val postType: String,
    private val token: String,
    private val reactionType: String,
    private val context: Context?,
    private val reactType: String
) {
    init {
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
                        //reportLike(currentItem.id)
                        holder?.NoOfLikes?.visibility = View.VISIBLE
                        holder?.NoOfLikes?.text = response.body()!!.postLikes.toString()
                        if (holder != null) {
                            afterReactionReasponse(holder, res, currentItem)
                        }
                    } else if (res.message == "post unReacted"){
                        //reportUnLike(currentItem.id)
                        if (holder != null) {
                            Setter.afterUnReactResponseOn(holder, res, currentItem)
                        }
                        holder?.NoOfLikes?.text = response.body()!!.postLikes.toString()
                        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                            Configuration.UI_MODE_NIGHT_NO -> {
                                // Night mode is not active on device
                                holder?.likePost?.returnToDefaultReaction()
                                holder?.NoOfLikes?.text = res.postLikes.toString()
                            }
                            Configuration.UI_MODE_NIGHT_YES -> {
                                // Night mode is active on device
                                holder?.likePost?.returnToDefaultReaction()
                                holder?.NoOfLikes?.text = res.postLikes.toString()
                            }
                        }
                    }
                    if (response.body()!!.postLikes == 0){
                        holder?.NoOfLikes?.visibility = View.GONE
                    }

                }
            }

            override fun onFailure(call: Call<LikesPostResponseModel?>, t: Throwable) {
                Toast.makeText(context, "No internet", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun reportLike(id:String){
        (context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.IO){
           // val result = dataBase.newDao().reportLike(id)
            //if (!result){
                Toast.makeText(context, "Error updating in db", Toast.LENGTH_SHORT).show()
           // }
        }
    }
    private fun reportUnLike(id:String){
        (context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.IO){
            //val result = dataBase.newDao().reportUnLike(id)
            //if (!result){
                Toast.makeText(context, "Error updating in db", Toast.LENGTH_SHORT).show()
           // }
        }
    }
}