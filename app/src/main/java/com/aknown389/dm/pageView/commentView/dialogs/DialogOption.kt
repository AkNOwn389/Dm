package com.aknown389.dm.pageView.commentView.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.aknown389.dm.R
import com.aknown389.dm.api.retroInstance.CommentInstance
import com.aknown389.dm.databinding.DialogFragmentCommentViewBinding
import com.bumptech.glide.Glide
import com.aknown389.dm.pageView.commentView.models.Data
import com.aknown389.dm.pageView.commentView.models.LikeCommentBody
import com.aknown389.dm.pageView.commentView.remote.LikeComment
import com.aknown389.dm.pageView.commentView.utility.Adapter
import com.aknown389.dm.pageView.commentView.utility.CommentViewHolder
import com.aknown389.dm.reactionTesting.ReactionsConstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class DialogOption @Inject constructor(
    private val holder:CommentViewHolder,
    private val context:Context,
    private val adapter: Adapter,
    private val comments:ArrayList<Data>,
    private val parent:ViewGroup,
    private val myitem: Data,
    private val postType:String,
) {
    init {
        openDialog()
    }

    private lateinit var dialog: BottomSheetDialog
    private lateinit var binding: DialogFragmentCommentViewBinding
    @OptIn(DelicateCoroutinesApi::class)
    private fun openDialog() {
        if (myitem.me == true) {
            dialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
            binding = DialogFragmentCommentViewBinding.inflate(LayoutInflater.from(context), parent, false)
            Glide.with(context).load(R.drawable.like).into(binding.like)
            Glide.with(context).load(R.drawable.love).into(binding.love)
            Glide.with(context).load(R.drawable.haha).into(binding.haha)
            Glide.with(context).load(R.drawable.wow).into(binding.wow)
            Glide.with(context).load(R.drawable.sad).into(binding.sad)
            Glide.with(context).load(R.drawable.angry).into(binding.angry)
            dialog.setContentView(binding.root)
            dialog.show()
            binding.commentviewdialogdelete.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    val response = try {
                        CommentInstance(context.applicationContext).api.deletecomment(
                            id = myitem.id.toString(),
                            postId = myitem.postId!!
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return@launch
                    }
                    if (response.isSuccessful) {
                        if (response.body()!!.status) {
                            Toast.makeText(context, "comment deleted", Toast.LENGTH_SHORT).show()
                            adapter.newCommentDeleted(myitem)
                        }
                    }
                }
                dialog.dismiss()
            }
            setListener()
        }else{
            dialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
            binding = DialogFragmentCommentViewBinding.inflate(LayoutInflater.from(context), parent, false)
            Glide.with(context).load(R.drawable.like).into(binding.like)
            Glide.with(context).load(R.drawable.love).into(binding.love)
            Glide.with(context).load(R.drawable.haha).into(binding.haha)
            Glide.with(context).load(R.drawable.wow).into(binding.wow)
            Glide.with(context).load(R.drawable.sad).into(binding.sad)
            Glide.with(context).load(R.drawable.angry).into(binding.angry)
            dialog.setContentView(binding.root)
            dialog.show()
            binding.commentviewdialogdelete.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    val response = try {
                        CommentInstance(context.applicationContext).api.deletecomment(
                            id = myitem.id.toString(),
                            postId = myitem.postId!!
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return@launch
                    }
                    if (response.isSuccessful) {
                        if (response.body()!!.status) {
                            Toast.makeText(context, "comment deleted", Toast.LENGTH_SHORT).show()
                            adapter.newCommentDeleted(myitem)
                        }
                    }
                }
                dialog.dismiss()
            }
            setListener()
        }
    }

    private fun likeComment(body: LikeCommentBody){
        LikeComment(context = context, parent = parent, adapterContext = adapter, myitem = myitem, holder = holder, body = body, dialog)
    }
    private fun createBody(reactionType:String):LikeCommentBody{
        return LikeCommentBody(
            myitem.postId.toString(),
            commentId = myitem.id!!,
            postType = postType,
            reactionType = reactionType,
            type = "react"
        )
    }

    private fun setListener() {
        if (holder.myData?.isLike == true){
            binding.unreact.visibility = View.VISIBLE
        }else{
            binding.unreact.visibility = View.GONE
        }
        binding.unreact.setOnClickListener {
            val body = LikeCommentBody(
                myitem.postId.toString(),
                commentId = myitem.id!!,
                postType = postType,
                reactionType = ReactionsConstance.DEFAULT,
                type = "unReact"
            )
            likeComment(body)
        }
        binding.like.setOnClickListener {
            val body = createBody(ReactionsConstance.LIKE)
            likeComment(body)
        }
        binding.love.setOnClickListener {
            val body = createBody(ReactionsConstance.LOVE)
            likeComment(body)
        }
        binding.haha.setOnClickListener {
            val body = createBody(ReactionsConstance.SMILE)
            likeComment(body)
        }
        binding.wow.setOnClickListener {
            val body = createBody(ReactionsConstance.WOW)
            likeComment(body)
        }
        binding.sad.setOnClickListener {
            val body = createBody(ReactionsConstance.SAD)
            likeComment(body)
        }
        binding.angry.setOnClickListener {
            val body = createBody(ReactionsConstance.ANGRY)
            likeComment(body)
        }
    }
}