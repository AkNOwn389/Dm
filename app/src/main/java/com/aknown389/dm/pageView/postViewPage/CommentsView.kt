package com.aknown389.dm.pageView.postViewPage

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.aknown389.dm.R
import com.aknown389.dm.activities.UserViewActivity
import com.aknown389.dm.pageView.postViewPage.models.ToDisplayDataModel
import com.aknown389.dm.api.retroInstance.CommentInstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CommentsView @Inject constructor(
    private val adapterContext:PostViewAdapter,
    private val context: Context,
    private val parent: ViewGroup,
    private val item: ToDisplayDataModel,
    private val holder: PostViewHolder,
    private val postDataList:ArrayList<ToDisplayDataModel>,
    private val token: String,
) {
    init {
        setUIComment()
        setListenerComment()
    }
    private fun setListenerComment() {
        holder.commentCardView?.setOnClickListener{
            if (holder.commentTime?.isVisible == false){
                holder.commentTime!!.visibility = View.VISIBLE
            }else{
                holder.commentTime?.visibility = View.GONE
            }
        }
        holder.commentUsername?.setOnClickListener {
            if (item.me == true){
                return@setOnClickListener
            }
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java)
                intent.putExtra("username", item.user)
                it.startActivity(intent)
            }
        }
        holder.commentUserImage?.setOnClickListener {
            if (item.me == true){
                return@setOnClickListener
            }
            (context as? AppCompatActivity)?.let {
                val intent = Intent(it, UserViewActivity::class.java)
                intent.putExtra("username", item.user)
                it.startActivity(intent)
            }
        }
        holder.commentLikeBtn?.setOnClickListener {
            //likeComment()
        }
        holder.commentCardView?.setOnLongClickListener {
            if (item.me == true) {
                val dialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_fragment_comment_view, parent, false)
                val deleteBtn: TextView? = view.findViewById(R.id.commentviewdialogdelete)
                dialog.setContentView(view)
                dialog.show()
                deleteBtn?.setOnClickListener {
                    (context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.Main) {
                        val response = try {
                            CommentInstance(context.applicationContext).api.deletecomment(
                                id = item.commentId.toString(),
                                postId = item.parentPostId!!
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                            return@launch
                        }
                        if (response.isSuccessful) {
                            if (response.body()!!.status) {
                                val num = postDataList.indexOf(item)
                                Log.d("Delete Num", num.toString())
                                postDataList.remove(item)
                                adapterContext.notifyItemRemoved(num)
                            }
                        }
                    }
                    dialog.dismiss()
                }
            }
            true
        }
    }

    private fun setUIComment() {
        holder.commentBody?.text = item.comments
        holder.commentUsername?.text = item.user_full_name
        holder.commentTime?.text = item.created
        if (holder.commentUserImage != null){
            Glide.with(context)
                .load(item.avatar)
                .placeholder(R.drawable.progress_animation)
                .error(R.mipmap.greybg)
                .into(holder.commentUserImage!!)
        }
        holder.commentFollowbtn?.setOnClickListener {
            item.user?.let { it1 -> FollowUser(
                it1,
                holder=holder,
                context = context,
                token = token,
                data = item) }
        }
        if (item.isLike==true){
            holder.commentLikeBtn?.text = context.getString(R.string.liked)
        }
        if (item.me == true){
            holder.commentFollowbtn?.isVisible = false
        }
        if (!item.me!!){
            holder.commentFollowbtn?.isVisible = item.Followed!!
        }
        if (item.Followed == true){
            holder.commentFollowbtn?.visibility = View.GONE
        }
        if (item.Followed == false){
            holder.commentFollowbtn?.visibility = View.VISIBLE
        }
    }
}