package com.aknown389.dm.pageView.mainSearch.utility

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.aknown389.dm.api.retroInstance.PostInstance
import com.aknown389.dm.models.mainSearchActivityModels.Data
import com.aknown389.dm.models.postmodel.CommentBody
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Comment(
    private val token:String,
    private val data: Data,
    private val holder: MainSearchViewHolder,
    private val context: Context,
    private val adapter: Adapter
){

    init {
        comment()
    }
    @SuppressLint("SetTextI18n")
    @OptIn(DelicateCoroutinesApi::class)
    private fun comment(){
        val mycomment = holder.commentInput?.text.toString()
        if (mycomment.isEmpty()){
            return
        }
        val body = CommentBody(postId = data.id!!, comment = mycomment)
        (context as? AppCompatActivity)?.lifecycleScope?.launch {
            val response = try {
                PostInstance.api.comment(token,  body)
            }catch (e: java.lang.Exception){
                e.stackTrace
                return@launch
            }
            if (response.isSuccessful && response.body() != null){
                val resBody = response.body()!!
                if (resBody.status){
                    withContext(Dispatchers.Main){
                        data.NoOfcomment?.plus(1)
                        holder.noOfComment?.isVisible  = true
                        holder.viewAllComment?.isVisible  = true
                        holder.noOfComment?.text = "${data.NoOfcomment?.plus(1)} comments"
                        holder.viewAllComment?.text = "View All ${data.NoOfcomment?.plus(1)} comments"
                        holder.commentInput?.setText("")
                    }
                }
            }
        }
    }
}