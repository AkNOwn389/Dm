package com.aknown389.dm.pageView.mainSearch.utility

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.aknown389.dm.R
import com.aknown389.dm.api.retroInstance.UsersInstance
import com.aknown389.dm.models.mainSearchActivityModels.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FollowUser(
    private val token:String,
    private val data: Data,
    private val holder: MainSearchViewHolder,
    private val context: Context,
    private val adapter: Adapter

) {
    init {
        (context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.Main) {
            if (isActive){
                val response = try {
                    UsersInstance.api.followUser(token, data.username.toString())
                }catch (e:Exception){
                    e.printStackTrace()
                    return@launch
                }
                if (response.isSuccessful && response.body()!!.status){
                    val body = response.body()!!
                    when(body.message){
                        "unfollowed" -> {
                            holder.button1?.text = context.getString(R.string.follow)
                            holder.button2?.text = context.getString(R.string.deny)
                            holder.button2?.isVisible = true}

                        "following" -> {holder.button1?.text = context.getString(R.string.followed)
                            holder.button2?.isVisible = false}
                    }
                }else{return@launch}
            }else{return@launch}
        }

    }
}