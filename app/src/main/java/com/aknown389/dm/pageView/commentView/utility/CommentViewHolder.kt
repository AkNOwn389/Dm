package com.aknown389.dm.pageView.commentView.utility

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aknown389.dm.R
import com.aknown389.dm.models.global.PostReactions
import com.aknown389.dm.pageView.commentView.models.Data
import com.makeramen.roundedimageview.RoundedImageView

class CommentViewHolder(view:View):ViewHolder(view) {
    var myData: Data? = null
    @JvmField
    var commentUsername:TextView? = null
    @JvmField
    var commentBody:TextView? = null
    @JvmField
    var commentUserImage:ImageView? = null
    @JvmField
    var commentTime:TextView? = null
    @JvmField
    var commentCardView:CardView? = null
    //new
    @JvmField
    var commentLikeLenght:TextView? = null
    @JvmField
    var imageContainer:ImageView? = null
    @JvmField
    var progress:ProgressBar? = null
    @JvmField
    var sending:RoundedImageView? = null
    @JvmField
    var sendingProgress:ProgressBar? = null
    @JvmField
    var icLike: RoundedImageView? = null
    @JvmField
    var icLove: RoundedImageView? = null
    @JvmField
    var icHappy: RoundedImageView? = null
    @JvmField
    var icWow: RoundedImageView? = null
    @JvmField
    var icSad: RoundedImageView? = null
    @JvmField
    var icAngry: RoundedImageView? = null
    @SuppressLint("SetTextI18n")
    fun newReactions(reactions: PostReactions, ){
        val sumOfAll = reactions.Wow!! + reactions.Sad!! + reactions.Angry!! + reactions.Happy!! + reactions.Like!! + reactions.Love!!
        myData?.noOfLike = sumOfAll
        this.commentLikeLenght?.text = sumOfAll.toString()
        if (this.myData != null){
            if (this.myData?.reactions != reactions){
                myData?.reactions = reactions
                Setter.onChangeReaction(this, myData!!, itemView.context)
            }
        }else{
            this.myData?.reactions = reactions
            myData?.let { Setter.onChangeReaction(this, it, itemView.context) }
        }
    }
    init {
        icLove = itemView.findViewById(R.id.ic_love)
        icAngry = itemView.findViewById(R.id.ic_angry)
        icHappy = itemView.findViewById(R.id.ic_happy)
        icWow = itemView.findViewById(R.id.ic_wow)
        icSad = itemView.findViewById(R.id.ic_sad)
        icLike = itemView.findViewById(R.id.ic_like)
        commentUsername = itemView.findViewById(R.id.commentUsername)
        commentBody = itemView.findViewById(R.id.commentTextView)
        commentUserImage = itemView.findViewById(R.id.commenUserImage)
        commentTime = itemView.findViewById(R.id.commentdate)
        commentCardView =  itemView.findViewById(R.id.cardview)
        commentLikeLenght = itemView.findViewById(R.id.commentLikeLen)
        imageContainer = itemView.findViewById(R.id.appCompatImageView)
        progress = itemView.findViewById(R.id.progress)
        sending = itemView.findViewById(R.id.sending)
        sendingProgress = itemView.findViewById(R.id.progressSending)
    }
}