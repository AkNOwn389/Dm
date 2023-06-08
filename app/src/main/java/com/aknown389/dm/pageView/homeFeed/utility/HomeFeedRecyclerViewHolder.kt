package com.aknown389.dm.pageView.homeFeed.utility

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aknown389.dm.R
import com.aknown389.dm.models.homepostmodels.PostDataModel
import com.bumptech.glide.RequestManager
import com.aknown389.dm.reactionTesting.ReactImageButton
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.makeramen.roundedimageview.RoundedImageView

class HomeFeedRecyclerViewHolder(val parent: View): ViewHolder(parent) {
    @JvmField var requestManager: RequestManager? = null
    @JvmField var mediaContainer: FrameLayout? = null
    @JvmField var thumbnail: ImageView? = null
    @JvmField var progressBar: ProgressBar? = null
    @JvmField var volumeControl: ImageView? = null
    @JvmField var postImage: ImageView? = null
    @JvmField var caption: TextView? = null
    @JvmField var creatorAvatar: ImageView? = null
    @JvmField var creatorName: TextView? = null
    @JvmField var createDate: TextView? = null
    @JvmField var myAvatar: ImageView? = null
    @JvmField var noOflikes: TextView? = null
    @JvmField var noOfComments: TextView? = null
    @JvmField var viewallcomment: TextView? = null
    @JvmField var likePost: ReactImageButton? = null
    @JvmField var commentBtn: ImageButton? = null
    @JvmField var shareBtn: ImageButton? = null
    @JvmField var sendCommenBtn: ImageButton? = null
    @JvmField var edtComment: EditText? = null
    @JvmField var isProfileUpdate: TextView? = null
    @JvmField var createPostBar: ConstraintLayout? = null
    @JvmField var createpostbarimage:ImageView? = null
    @JvmField var imagelenght:TextView? = null
    @JvmField var menuBtn:ImageButton? = null
    @JvmField var privacy:TextView? = null
    @JvmField var playerView: StyledPlayerView? = null
    @JvmField var exoPlay: ImageView? = null
    @JvmField var exoSettings: ImageView? = null
    @JvmField var exoFullScreen: ImageView? = null
    @JvmField var videoLen: TextView? = null
    @JvmField var cardView:CardView? = null
    @JvmField var icLike: RoundedImageView? = null
    @JvmField var icLove: RoundedImageView? = null
    @JvmField var icHappy: RoundedImageView? = null
    @JvmField var icWow: RoundedImageView? = null
    @JvmField var icSad: RoundedImageView? = null
    @JvmField var icAngry: RoundedImageView? = null
    //profile
    @JvmField var profileImage:ImageView? = null
    @JvmField var profileBackgroundImage:ImageView? = null
    @JvmField var profileBio:TextView? = null
    @JvmField var profilePostLength:TextView? = null
    @JvmField var profileFollowerLength:TextView? = null
    @JvmField var profileFollowingLength:TextView? = null
    @JvmField var profileName:TextView? = null
    @JvmField var profileUserName:TextView? = null
    @JvmField var profileSeeAllFollowers:TextView? = null
    @JvmField var profileSeeFollowed:TextView? = null
    @JvmField var profileVideos:TextView? = null
    @JvmField var profilePhotos:TextView? = null
    @JvmField var profileAboutSelf:TextView? = null
    fun onBind(requestManager: RequestManager?) {
        this.requestManager = requestManager
        parent.tag = this
    }

    init {
        profileImage = itemView.findViewById(R.id.ImageProfile)
        profileBackgroundImage = itemView.findViewById(R.id.profileBackgroundCover)
        profileBio = itemView.findViewById(R.id.profileBio)
        profilePostLength = itemView.findViewById(R.id.profilepostcount)
        profileFollowerLength = itemView.findViewById(R.id.profilefollowers)
        profileFollowingLength = itemView.findViewById(R.id.profilefollowing)
        profileName = itemView.findViewById(R.id.txtProfileName)
        profileUserName = itemView.findViewById(R.id.userName)
        profileSeeAllFollowers = itemView.findViewById(R.id.seeFollowers)
        profileSeeFollowed = itemView.findViewById(R.id.seeFollowing)
        profileVideos = itemView.findViewById(R.id.videos)
        profilePhotos = itemView.findViewById(R.id.photos)
        profileAboutSelf = itemView.findViewById(R.id.about)

        icLove = itemView.findViewById(R.id.ic_love)
        icAngry = itemView.findViewById(R.id.ic_angry)
        icHappy = itemView.findViewById(R.id.ic_happy)
        icWow = itemView.findViewById(R.id.ic_wow)
        icSad = itemView.findViewById(R.id.ic_sad)
        icLike = itemView.findViewById(R.id.ic_like)
        cardView = itemView.findViewById(R.id.newsCardViewImage)
        videoLen = itemView.findViewById(R.id.videoLenght)
        exoFullScreen = itemView.findViewById(R.id.fullscreen_header)
        exoPlay = itemView.findViewById(R.id.exo_play)
        exoSettings = itemView.findViewById(R.id.exo_settings)
        playerView = itemView.findViewById(R.id.player_view)
        mediaContainer = itemView.findViewById(R.id.media_container)
        thumbnail = itemView.findViewById(R.id.thumbnail)
        progressBar = itemView.findViewById(R.id.progressBar)
        volumeControl = itemView.findViewById(R.id.volume_control)
        postImage = itemView.findViewById(R.id.NewsImage)
        caption = itemView.findViewById(R.id.description)
        creatorAvatar = itemView.findViewById(R.id.CreatorAvatar)
        creatorName = itemView.findViewById(R.id.Name)
        createDate = itemView.findViewById(R.id.postCardHomeTimeCreated)
        myAvatar = itemView.findViewById(R.id.myAvatarOnhomeFeed)
        noOflikes = itemView.findViewById(R.id.NoOfLikes)
        noOfComments = itemView.findViewById(R.id.NoOfComments)
        viewallcomment = itemView.findViewById(R.id.postCardHomeViewAllComment)
        commentBtn = itemView.findViewById(R.id.CommentBtn)
        shareBtn = itemView.findViewById(R.id.ShareBtn)
        sendCommenBtn = itemView.findViewById(R.id.postCardHomeSendComment)
        edtComment = itemView.findViewById(R.id.editTextPostCardHomeAddComment)
        isProfileUpdate = itemView.findViewById(R.id.isUpdateProfile)
        createPostBar = itemView.findViewById(R.id.homefeedcreatpostbar)
        createpostbarimage = itemView.findViewById(R.id.craetepostbarimage)
        imagelenght = itemView.findViewById(R.id.imagelenght)
        menuBtn = itemView.findViewById(R.id.newsMenu)
        privacy = itemView.findViewById(R.id.privacy)
        likePost = itemView.findViewById(R.id.LikeButton)

    }
}