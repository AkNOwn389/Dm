package com.aknown389.dm.pageView.videoFeed

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.aknown389.dm.R
import com.aknown389.dm.reactionTesting.ReactImageButton
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.makeramen.roundedimageview.RoundedImageView

class VideoFeedViewHolder(val parent: View) : RecyclerView.ViewHolder(parent) {

    @JvmField
    var userImage: ImageView? = null
    @JvmField
    var name: TextView? = null
    @JvmField
    var time: TextView? = null
    @JvmField
    var menuBtn: ImageButton? = null
    @JvmField
    var privacy: TextView? = null
    @JvmField
    var caption: TextView? = null
    @JvmField
    var likePost: ReactImageButton? = null
    @JvmField
    var commentBtn: ImageButton? = null
    @JvmField
    var shareBtn: ImageButton? = null
    @JvmField
    var noOfLike: TextView? = null
    @JvmField
    var noOfComment: TextView? = null
    @JvmField
    var mediaContainer: FrameLayout? = null
    @JvmField
    var thumbnail: ImageView? = null
    @JvmField
    var progressBar: ProgressBar? = null
    @JvmField
    var volumeControl: ImageView? = null
    @JvmField
    var playerView: StyledPlayerView? = null
    @JvmField
    var requestManager:RequestManager? = null
    @JvmField
    var exoPlay: ImageView? = null
    @JvmField
    var exoSettings: ImageView? = null
    @JvmField
    var exoFullScreen: ImageView? = null
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
    fun onBind(requestManager: RequestManager){
        parent.tag = this
        this.requestManager = requestManager
    }

    init {
        icLove = itemView.findViewById(R.id.ic_love)
        icAngry = itemView.findViewById(R.id.ic_angry)
        icHappy = itemView.findViewById(R.id.ic_happy)
        icWow = itemView.findViewById(R.id.ic_wow)
        icSad = itemView.findViewById(R.id.ic_sad)
        icLike = itemView.findViewById(R.id.ic_like)
        exoFullScreen = itemView.findViewById(R.id.fullscreen_header)
        exoPlay = itemView.findViewById(R.id.exo_play)
        exoSettings = itemView.findViewById(R.id.exo_settings)
        userImage = itemView.findViewById(R.id.CreatorAvatar)
        name = itemView.findViewById(R.id.Name)
        time = itemView.findViewById(R.id.postCardHomeTimeCreated)
        menuBtn= itemView.findViewById(R.id.newsMenu)
        privacy = itemView.findViewById(R.id.privacy)
        caption = itemView.findViewById(R.id.description)
        likePost = itemView.findViewById(R.id.LikeButton)
        commentBtn = itemView.findViewById(R.id.CommentBtn)
        shareBtn = itemView.findViewById(R.id.ShareBtn)
        noOfLike = itemView.findViewById(R.id.NoOfLikes)
        noOfComment = itemView.findViewById(R.id.NoOfComments)
        mediaContainer = itemView.findViewById(R.id.media_container)
        thumbnail = itemView.findViewById(R.id.thumbnail)
        progressBar = itemView.findViewById(R.id.progressBar)
        volumeControl = itemView.findViewById(R.id.volume_control)
        playerView = itemView.findViewById(R.id.player_view)
    }
}