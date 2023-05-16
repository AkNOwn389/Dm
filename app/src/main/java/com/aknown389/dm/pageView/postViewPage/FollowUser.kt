package com.aknown389.dm.pageView.postViewPage

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aknown389.dm.R
import com.aknown389.dm.api.retroInstance.UsersInstance
import com.aknown389.dm.pageView.postViewPage.Models.ToDisplayDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FollowUser(
    user:String,
    private val holder: PostViewHolder,
    private val data: ToDisplayDataModel,
    private val token: String,
    private val context: Context
) {


    init {
        (context as? AppCompatActivity)?.lifecycleScope?.launch {
            val response = try {
                UsersInstance.api.followUser(token, user)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful){
                withContext(Dispatchers.Main){
                    if (response.body()!!.message == "following"){
                        holder.commentFollowbtn?.text = (context as? AppCompatActivity)?.getString(R.string.followed)
                    }else{
                        holder.commentFollowbtn?.text = (context as? AppCompatActivity)?.getString(R.string.follow)
                    }
                }
            }
        }
    }
}