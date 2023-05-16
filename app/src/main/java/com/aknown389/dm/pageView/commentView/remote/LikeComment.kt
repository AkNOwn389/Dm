package com.aknown389.dm.pageView.commentView.remote

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.aknown389.dm.pageView.commentView.models.Data
import com.aknown389.dm.pageView.commentView.models.LikeCommentBody
import com.aknown389.dm.pageView.commentView.remote.instance.Instance
import com.aknown389.dm.pageView.commentView.utility.Adapter
import com.aknown389.dm.pageView.commentView.utility.CommentViewHolder
import com.aknown389.dm.reactionTesting.ReactionsConstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
class LikeComment @Inject constructor(
    private val context: Context,
    private val parent: ViewGroup,
    private val adapterContext: Adapter,
    private val myitem: Data,
    private val holder: CommentViewHolder,
    private val body: LikeCommentBody,
    private val dialogOption: BottomSheetDialog

) {

    init {
        GlobalScope.launch(Dispatchers.Main){
            val animation = AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_slide_in_top)
            val animationOut= AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_slide_out_top)
            if (holder.myData?.isLike!!){
                val reactions = holder.myData?.reactions
                if (body.reactionType != ReactionsConstance.DEFAULT){
                    when(holder.myData?.reactionType){
                        ReactionsConstance.LIKE -> {
                            if (reactions?.Like == 1){
                                holder.icLike?.startAnimation(animationOut)
                                holder.icLike?.visibility = View.GONE
                            }else{
                                if (holder.icLike?.visibility == View.GONE){
                                    holder.icLike?.visibility = View.VISIBLE
                                    holder.icLike?.startAnimation(animation)
                                }
                            }}
                        ReactionsConstance.LOVE -> {
                            if (reactions?.Love == 1){
                                holder.icLove?.startAnimation(animationOut)
                                holder.icLove?.visibility = View.GONE
                            }else{
                                if (holder.icLove?.visibility == View.GONE){
                                    holder.icLove?.visibility = View.VISIBLE
                                    holder.icLove?.startAnimation(animation)
                                }

                            }}
                        ReactionsConstance.SMILE -> {
                            if (reactions?.Happy == 1){
                                holder.icHappy?.startAnimation(animationOut)
                                holder.icHappy?.visibility = View.GONE
                            }else{
                                if (holder.icHappy?.visibility == View.GONE){
                                    holder.icHappy?.visibility = View.VISIBLE
                                    holder.icHappy?.startAnimation(animation)
                                }
                            }}
                        ReactionsConstance.WOW -> {
                            if (reactions?.Wow == 1){
                                holder.icWow?.startAnimation(animationOut)
                                holder.icWow?.visibility = View.GONE
                            }else{
                                if (holder.icWow?.visibility == View.GONE){
                                    holder.icWow?.visibility = View.VISIBLE
                                    holder.icWow?.startAnimation(animation)
                                }
                            }}
                        ReactionsConstance.SAD -> {
                            if (reactions?.Sad == 1){
                                holder.icSad?.startAnimation(animationOut)
                                holder.icSad?.visibility = View.GONE
                            }else{
                                if (holder.icSad?.visibility == View.GONE){
                                    holder.icSad?.visibility = View.VISIBLE
                                    holder.icSad?.startAnimation(animation)
                                }
                            }}
                        ReactionsConstance.ANGRY -> {
                            if (reactions?.Angry == 1){
                                holder.icAngry
                                    ?.startAnimation(animationOut)
                                holder.icAngry?.visibility = View.GONE
                            }else{
                                if (holder.icAngry?.visibility == View.GONE){
                                    holder.icAngry?.visibility = View.VISIBLE
                                    holder.icAngry?.startAnimation(animation)
                                }
                            }}
                    }
                }
            }
            when(body.reactionType){
                ReactionsConstance.LIKE -> {
                    if (holder.icLike?.visibility == View.GONE){
                        holder.icLike?.visibility = View.VISIBLE
                        holder.icLike?.startAnimation(animation)
                    }
                }
                ReactionsConstance.LOVE -> {
                    if (holder.icLove?.visibility == View.GONE){
                        holder.icLove?.visibility = View.VISIBLE
                        holder.icLove?.startAnimation(animation)
                    }
                }
                ReactionsConstance.SMILE -> {
                    if (holder.icHappy?.visibility == View.GONE){
                        holder.icHappy?.visibility = View.VISIBLE
                        holder.icHappy?.startAnimation(animation)
                    }
                }
                ReactionsConstance.WOW -> {
                    if (holder.icWow?.visibility == View.GONE){
                        holder.icWow?.visibility = View.VISIBLE
                        holder.icWow?.startAnimation(animation)
                    }
                }
                ReactionsConstance.SAD -> {
                    if (holder.icSad?.visibility == View.GONE){
                        holder.icSad?.visibility = View.VISIBLE
                        holder.icSad?.startAnimation(animation)
                    }
                }
                ReactionsConstance.ANGRY -> {
                    if (holder.icAngry?.visibility == View.GONE){
                        holder.icAngry?.visibility = View.VISIBLE
                        holder.icAngry?.startAnimation(animation)
                    }
                }
            }
            val response = try {
                Instance(context.applicationContext).api.likeComment(body = body)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful && response.body()!!.status){
                if (response.body()!!.message == "comment_reacted"){
                    holder.myData?.reactionType = body.reactionType
                    dialogOption.dismiss()
                    holder.myData!!.isLike = true
                }else if (response.body()!!.message == "comment_unreacted"){
                   holder.myData!!.isLike = false
                    dialogOption.dismiss()
                }
            }else{
                dialogOption.dismiss()
            }
        }
    }
}