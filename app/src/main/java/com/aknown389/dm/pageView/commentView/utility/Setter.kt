package com.aknown389.dm.pageView.commentView.utility

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import com.aknown389.dm.pageView.commentView.models.Data
import com.aknown389.dm.pageView.commentView.models.LikeCommentResponse
import com.aknown389.dm.utils.ReactionTypes

object Setter {
    fun afterReactionResponse(
        holder: CommentViewHolder,
        currentItem: LikeCommentResponse,
        dataModel: Data,
        context:Context
    ) {
        dataModel.noOfLike = currentItem.commentLike
        if (dataModel.isLike == true) {
            when (dataModel.reactionType) {
                ReactionTypes.LIKE -> dataModel.reactions?.Like =
                    dataModel.reactions?.Like?.minus(1)

                ReactionTypes.LOVE -> dataModel.reactions?.Love =
                    dataModel.reactions?.Love?.minus(1)

                ReactionTypes.HAPPY -> dataModel.reactions?.Happy =
                    dataModel.reactions?.Happy?.minus(1)

                ReactionTypes.AMAZE -> dataModel.reactions?.Wow = dataModel.reactions?.Wow?.minus(1)
                ReactionTypes.SAD -> dataModel.reactions?.Sad = dataModel.reactions?.Sad?.minus(1)
                ReactionTypes.ANGRY -> dataModel.reactions?.Angry =
                    dataModel.reactions?.Angry?.minus(1)
            }
            when (currentItem.reaction) {
                ReactionTypes.LIKE -> dataModel.reactions?.Like = dataModel.reactions?.Like?.plus(1)
                ReactionTypes.LOVE -> dataModel.reactions?.Love = dataModel.reactions?.Love?.plus(1)
                ReactionTypes.HAPPY -> dataModel.reactions?.Happy =
                    dataModel.reactions?.Happy?.plus(1)

                ReactionTypes.AMAZE -> dataModel.reactions?.Wow = dataModel.reactions?.Wow?.plus(1)
                ReactionTypes.SAD -> dataModel.reactions?.Sad = dataModel.reactions?.Sad?.plus(1)
                ReactionTypes.ANGRY -> dataModel.reactions?.Angry =
                    dataModel.reactions?.Angry?.plus(1)
            }
        } else {
            when (currentItem.reaction) {
                ReactionTypes.LIKE -> dataModel.reactions?.Like = dataModel.reactions?.Like?.plus(1)
                ReactionTypes.LOVE -> dataModel.reactions?.Love = dataModel.reactions?.Love?.plus(1)
                ReactionTypes.HAPPY -> dataModel.reactions?.Happy =
                    dataModel.reactions?.Happy?.plus(1)

                ReactionTypes.AMAZE -> dataModel.reactions?.Wow = dataModel.reactions?.Wow?.plus(1)
                ReactionTypes.SAD -> dataModel.reactions?.Sad = dataModel.reactions?.Sad?.plus(1)
                ReactionTypes.ANGRY -> dataModel.reactions?.Angry =
                    dataModel.reactions?.Angry?.plus(1)
            }
        }


        dataModel.reactionType = currentItem.reaction
        dataModel.isLike = true
        iconSetterBaseOnLike(holder = holder, currentItem = dataModel, context = context)
    }

    fun afterUnReactResponse(
        holder: CommentViewHolder,
        currentItem: LikeCommentResponse,
        dataModel: Data,
        context:Context
    ) {
        dataModel.noOfLike = currentItem.commentLike
        when (dataModel.reactionType) {
            ReactionTypes.LIKE -> dataModel.reactions?.Like = dataModel.reactions?.Like?.minus(1)
            ReactionTypes.LOVE -> dataModel.reactions?.Love = dataModel.reactions?.Love?.minus(1)
            ReactionTypes.HAPPY -> dataModel.reactions?.Happy = dataModel.reactions?.Happy?.minus(1)
            ReactionTypes.AMAZE -> dataModel.reactions?.Wow = dataModel.reactions?.Wow?.minus(1)
            ReactionTypes.SAD -> dataModel.reactions?.Sad = dataModel.reactions?.Sad?.minus(1)
            ReactionTypes.ANGRY -> dataModel.reactions?.Angry = dataModel.reactions?.Angry?.minus(1)
        }
        dataModel.reactionType = null
        dataModel.isLike = false
        iconSetterBaseOnLike(holder = holder, currentItem = dataModel, context = context)
    }

    fun onChangeReaction(holder: CommentViewHolder, currentItem: Data, context: Context){
        val animation = AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_slide_in_top)
        if (currentItem.reactions?.Love == 0) {
            holder.icLove?.visibility = View.GONE
        } else {
            if (holder.icLove?.visibility == View.GONE){
                holder.icLove?.visibility = View.VISIBLE
                holder.icLove?.startAnimation(animation)
            }
        }
        if (currentItem.reactions?.Like == 0) {
            holder.icLike?.visibility = View.GONE
        } else {
            if (holder.icLike?.visibility == View.GONE){
                holder.icLike?.visibility = View.VISIBLE
                holder.icLike?.startAnimation(animation)
            }
        }
        if (currentItem.reactions?.Happy == 0) {
            holder.icHappy?.visibility = View.GONE
        } else {
            if (holder.icHappy?.visibility == View.GONE){
                holder.icHappy?.visibility = View.VISIBLE
                holder.icHappy?.startAnimation(animation)
            }
        }
        if (currentItem.reactions?.Wow == 0) {
            holder.icWow?.visibility = View.GONE
        } else {
            if (holder.icWow?.visibility == View.GONE){
                holder.icWow?.visibility = View.VISIBLE
                holder.icWow?.startAnimation(animation)
            }
        }
        if (currentItem.reactions?.Sad == 0) {
            holder.icSad?.visibility = View.GONE
        } else {
            if (holder.icSad?.visibility == View.GONE){
                holder.icSad?.visibility = View.VISIBLE
                holder.icSad?.startAnimation(animation)
            }
        }
        if (currentItem.reactions?.Angry == 0) {
            holder.icAngry?.visibility = View.GONE
        } else {
            if (holder.icAngry?.visibility == View.GONE){
                holder.icAngry?.visibility = View.VISIBLE
                holder.icAngry?.startAnimation(animation)
            }
        }
        if (currentItem.reactions?.Like!!.equals(0)
            && currentItem.reactions?.Happy!!.equals(0)
            && currentItem.reactions?.Love!!.equals(0)
            && currentItem.reactions?.Wow!!.equals(0)
            && currentItem.reactions?.Sad!!.equals(0)
            && currentItem.reactions?.Angry!!.equals(0)
        ) {
            holder.commentLikeLenght?.visibility = View.GONE
        } else {
            holder.commentLikeLenght?.visibility = View.VISIBLE
            val num = currentItem.reactions
            val noOfAllLike = num?.Like!! + num.Love!! + num.Happy!! + num.Wow!! + num.Sad!! + num.Angry!!
            val noToDisplay = if (noOfAllLike > 1000){
                ((noOfAllLike / 1000).toDouble().toString() + "k")
            } else {
                noOfAllLike.toString()
            }
            currentItem.noOfLike = noOfAllLike
            holder.commentLikeLenght?.text = noToDisplay
        }
    }

    @SuppressLint("SetTextI18n")
    fun iconSetterBaseOnLike(holder: CommentViewHolder, currentItem: Data, context: Context) {
        val animation = AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_popup_enter)
        if (currentItem.reactions?.Love == 0) {
            holder.icLove?.visibility = View.GONE
        } else {
            if (holder.icLove?.visibility == View.GONE){
                holder.icLove?.visibility = View.VISIBLE
                holder.icLove?.startAnimation(animation)
            }
        }
        if (currentItem.reactions?.Like == 0) {
            holder.icLike?.visibility = View.GONE
        } else {
            if (holder.icLike?.visibility == View.GONE){
                holder.icLike?.visibility = View.VISIBLE
                holder.icLike?.startAnimation(animation)
            }
        }
        if (currentItem.reactions?.Happy == 0) {
            holder.icHappy?.visibility = View.GONE
        } else {
            if (holder.icHappy?.visibility == View.GONE){
                holder.icHappy?.visibility = View.VISIBLE
                holder.icHappy?.startAnimation(animation)
            }
        }
        if (currentItem.reactions?.Wow == 0) {
            holder.icWow?.visibility = View.GONE
        } else {
            if (holder.icWow?.visibility == View.GONE){
                holder.icWow?.visibility = View.VISIBLE
                holder.icWow?.startAnimation(animation)
            }
        }
        if (currentItem.reactions?.Sad == 0) {
            holder.icSad?.visibility = View.GONE
        } else {
            if (holder.icSad?.visibility == View.GONE){
                holder.icSad?.visibility = View.VISIBLE
                holder.icSad?.startAnimation(animation)
            }
        }
        if (currentItem.reactions?.Angry == 0) {
            holder.icAngry?.visibility = View.GONE
        } else {
            if (holder.icAngry?.visibility == View.GONE){
                holder.icAngry?.visibility = View.VISIBLE
                holder.icAngry?.startAnimation(animation)
            }
        }
        if (currentItem.reactions?.Like == 0
            && currentItem.reactions?.Happy == 0
            && currentItem.reactions?.Love == 0
            && currentItem.reactions?.Wow == 0
            && currentItem.reactions?.Sad == 0
            && currentItem.reactions?.Angry == 0
        ) {
            if (holder.commentLikeLenght?.visibility == View.VISIBLE){
                holder.commentLikeLenght?.visibility = View.GONE
            }
        } else {
            if (holder.commentLikeLenght?.visibility == View.GONE){
                holder.commentLikeLenght?.visibility = View.VISIBLE
            }
            val num = currentItem.reactions
            val noOfAllLike = num?.Like!! + num.Love!! + num.Happy!! + num.Wow!! + num.Sad!! + num.Angry!!
            val noToDisplay = if (noOfAllLike > 1000){
                ((noOfAllLike / 1000).toDouble().toString() + "k")
            } else {
                noOfAllLike.toString()
            }
            currentItem.noOfLike = noOfAllLike
            holder.commentLikeLenght?.text = noToDisplay
        }
    }
}