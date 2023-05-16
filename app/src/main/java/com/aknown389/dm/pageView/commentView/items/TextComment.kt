package com.aknown389.dm.pageView.commentView.items

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.aknown389.dm.R
import com.aknown389.dm.activities.UserViewActivity
import com.aknown389.dm.api.retroInstance.CommentInstance
import com.aknown389.dm.models.postmodel.CommentBody
import com.aknown389.dm.pageView.commentView.dialogs.DialogOption
import com.bumptech.glide.Glide
import com.aknown389.dm.pageView.commentView.models.Data
import com.aknown389.dm.pageView.commentView.utility.Adapter
import com.aknown389.dm.pageView.commentView.utility.CommentViewHolder
import com.aknown389.dm.pageView.commentView.utility.Setter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class TextComment @Inject constructor(
    private val holder: CommentViewHolder,
    private var currentItem: Data,
    private val context: Context,
    private val comments: ArrayList<Data>,
    private val parent: ViewGroup,
    private val adapterContext: Adapter,
    private val postType: String?
) {



    init {
        if (currentItem.sendKoBa == true){
            sendItem(currentItem, holder)
            loadSendingUi(currentItem, holder)
        }else{
            holder.myData = currentItem
            loadCommentUI(holder)
            setListener(holder)
        }
    }

    private fun loadSendingUi(currentItem: Data, holder: CommentViewHolder) {
        holder.commentBody?.text = currentItem.comments
        holder.commentUsername?.text = context.getString(R.string.me)
        holder.commentLikeLenght?.visibility = View.GONE
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sendItem(currentItem: Data, holder: CommentViewHolder) {
        if (currentItem.postId == null || currentItem.comments == null){
            return
        }
        GlobalScope.launch(Dispatchers.Main) {
            holder.sendingProgress?.visibility = View.VISIBLE
            delay(500)
            val body = CommentBody(postId = currentItem.postId!!, comment = currentItem.comments!!)
            val response = try {
                CommentInstance(context).api.sendCommentText("text", body)
            }catch (e:Exception){
                return@launch
            }
            if (response.isSuccessful && response.body()!!.status){
                val pos = comments.indexOf(currentItem)
                val resBody = response.body()!!.data
                this@TextComment.currentItem = resBody
                comments[pos] = resBody
                Log.d(TAG, currentItem.toString())
                Log.d(TAG, resBody.toString())
                holder.sending?.setImageResource(R.drawable.check_alt_flat_icon)
                holder.sendingProgress?.visibility = View.GONE
                holder.sending?.visibility = View.VISIBLE
                init2(this@TextComment.currentItem, holder)
            }else{
                holder.sending?.setImageResource(R.drawable.error)
                holder.sendingProgress?.visibility = View.GONE
                holder.sending?.visibility = View.VISIBLE
            }
        }
    }

    private fun init2(currentItem: Data, holder: CommentViewHolder){
        holder.myData = currentItem
        loadCommentUI(holder)
        setListener(holder)
    }


    private fun loadCommentUI(holder: CommentViewHolder) {

        try {
            Setter.iconSetterBaseOnLike(holder, currentItem, context)
            holder.commentBody?.text = holder.myData?.comments
            if (holder.myData?.me == true) {
                holder.commentUsername?.text = context.getString(R.string.me)
            } else {
                holder.commentUsername?.text = holder.myData?.userFullName
                Glide.with(context)
                    .load(holder.myData?.avatar)
                    .placeholder(R.drawable.progress_animation)
                    .error(R.mipmap.greybg)
                    .into(holder.commentUserImage!!)
            }
            holder.commentTime?.text = holder.myData?.created
        }catch (e:Exception){
            Log.d(TAG, e.stackTrace.toString())
        }
    }
    private fun setListener(holder: CommentViewHolder) {
        holder.commentUsername?.setOnClickListener {
            if (holder.myData?.me == true){
                return@setOnClickListener
            }
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java)
                intent.putExtra("username", holder.myData?.user)
                it.startActivity(intent)
            }
        }
        holder.commentUserImage?.setOnClickListener {
            if (holder.myData?.me == true){
                return@setOnClickListener
            }
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java)
                intent.putExtra("username", holder.myData?.user)
                it.startActivity(intent)
            }
        }
        holder.commentBody?.setOnLongClickListener {
            if (postType != null) {
                DialogOption(holder = holder, context = context, adapter = adapterContext, comments = comments, parent = parent, myitem = holder.myData!!, postType = postType)
            }
            true
        }
    }
    companion object{
        val TAG = "textcomment"
    }


}