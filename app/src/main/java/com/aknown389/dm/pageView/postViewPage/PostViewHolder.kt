package com.aknown389.dm.pageView.postViewPage

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aknown389.dm.R
import com.aknown389.dm.reactionTesting.ReactImageButton
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.makeramen.roundedimageview.RoundedImageView

class PostViewHolder(val parent: View) : ViewHolder(parent) {
    @JvmField
    var image: ImageView? = null

    @JvmField
    var imagecard: CardView? = null

    @JvmField
    var caption: TextView? = null

    @JvmField
    var likeCommentBody: ConstraintLayout? = null

    @JvmField
    var likePost: ReactImageButton? = null

    @JvmField
    var commenBtn: ImageButton? = null

    @JvmField
    var shareBtn: ImageButton? = null

    @JvmField
    var noOfLike: TextView? = null

    @JvmField
    var noOfComment: TextView? = null

    @JvmField
    var bottomNoOfComments: TextView? = null

    @JvmField
    var viewAllComment: TextView? = null

    @JvmField
    //comments
    var commentUsername: TextView? = null

    @JvmField
    var commentFollowbtn: TextView? = null

    @JvmField
    var commentBody: TextView? = null

    @JvmField
    var commentUserImage: ImageView? = null

    @JvmField
    var commentTime: TextView? = null

    @JvmField
    var commentCardView: CardView? = null

    @JvmField
    //new
    var commentLikeBtn: TextView? = null

    @JvmField
    var commentLikeLenght: TextView? = null

    @JvmField
    //video
    var player: StyledPlayerView? = null

    @JvmField
    var thumbnailUtils: ImageView? = null

    @JvmField
    var progressBar1: ProgressBar? = null

    @JvmField
    var progressBar2: ProgressBar? = null
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


    init {
        icLove = itemView.findViewById(R.id.ic_love)
        icAngry = itemView.findViewById(R.id.ic_angry)
        icHappy = itemView.findViewById(R.id.ic_happy)
        icWow = itemView.findViewById(R.id.ic_wow)
        icSad = itemView.findViewById(R.id.ic_sad)
        icLike = itemView.findViewById(R.id.ic_like)
        image = itemView.findViewById(R.id.postImage)
        imagecard = itemView.findViewById(R.id.postImageCardView)
        caption = itemView.findViewById(R.id.caption)
        likeCommentBody = itemView.findViewById(R.id.likedisplaybgContrains)
        likePost = itemView.findViewById(R.id.LikeButton)
        commenBtn = itemView.findViewById(R.id.CommentBtn)
        shareBtn = itemView.findViewById(R.id.ShareBtn)
        noOfLike = itemView.findViewById(R.id.NoOfLikes)
        noOfComment = itemView.findViewById(R.id.noOfComments)
        bottomNoOfComments = itemView.findViewById(R.id.NoOfComments)
        viewAllComment = itemView.findViewById(R.id.postCardHomeViewAllComment)
        commentUsername = itemView.findViewById(R.id.commentUsername)
        commentBody = itemView.findViewById(R.id.commentTextView)
        commentUserImage = itemView.findViewById(R.id.commenUserImage)
        commentTime = itemView.findViewById(R.id.commentdate)
        commentCardView = itemView.findViewById(R.id.cardview)
        commentLikeLenght = itemView.findViewById(R.id.commentLikeLen)
        player = itemView.findViewById(R.id.player_view)
        thumbnailUtils = itemView.findViewById(R.id.thumbnail)
        progressBar1 = itemView.findViewById(R.id.progressBar1)
        progressBar2 = itemView.findViewById(R.id.progressBar2)
    }
}