package com.aknown389.dm.pageView.commentView.remote

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aknown389.dm.api.retroInstance.UsersInstance
import com.aknown389.dm.pageView.commentView.models.Data
import com.aknown389.dm.pageView.commentView.utility.Adapter
import com.aknown389.dm.pageView.commentView.utility.CommentViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FollowUser(
    private val token:String,
    private val context: Context,
    private val parent: ViewGroup,
    private val adapterContext: Adapter,
    private val myitem: Data,
    private val holder: CommentViewHolder



) {
    init {
        (context as? AppCompatActivity)?.lifecycleScope?.launch {
            val response = try {
                UsersInstance.api.followUser(token, myitem.user!!)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful){
                withContext(Dispatchers.Main){
                    if (response.body()!!.message == "following"){
                        //holder.commentFollowbtn?.text = (context as? AppCompatActivity)?.getString(R.string.followed)
                    }else{
                        //holder.commentFollowbtn?.text = (context as? AppCompatActivity)?.getString(R.string.follow)
                    }
                }
            }
        }
    }
}