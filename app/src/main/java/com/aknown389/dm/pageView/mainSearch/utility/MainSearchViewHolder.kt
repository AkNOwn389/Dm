package com.aknown389.dm.pageView.mainSearch.utility

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aknown389.dm.R
import com.aknown389.dm.reactionTesting.ReactImageButton
import com.makeramen.roundedimageview.RoundedImageView

class MainSearchViewHolder(view:View):ViewHolder(view) {
    @JvmField    var name: TextView? = null
    @JvmField    var image: ImageView? = null
    @JvmField    var location: TextView? = null
    @JvmField    var button1: Button? = null
    @JvmField    var button2: Button? = null
    @JvmField    var status: TextView? = null
    @JvmField    var body: ConstraintLayout? = null
    @JvmField    var postsAvatar: ImageView? = null
    @JvmField    var postsName: TextView? = null
    @JvmField    var time: TextView? = null
    @JvmField    var isProfile: TextView? = null
    @JvmField    var menuBtn: ImageButton? = null
    @JvmField    var caption: TextView? = null
    @JvmField    var postImage: ImageView? = null
    @JvmField    var noOfLike: TextView? = null
    @JvmField    var noOfComment: TextView? = null
    @JvmField    var likePost: ReactImageButton? = null
    @JvmField    var commentBtn: ImageButton? = null
    @JvmField    var shareBtn: ImageButton? = null
    @JvmField    var myImage: ImageView? = null
    @JvmField    var commentInput: EditText? = null
    @JvmField    var commentSendBtn: ImageButton? = null
    @JvmField    var viewAllComment: TextView? =null
    @JvmField    var imagelen: TextView? = null
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
        image = itemView.findViewById(R.id.ImageView)
        name = itemView.findViewById(R.id.NameTag)
        location = itemView.findViewById(R.id.location)
        button1 = itemView.findViewById(R.id.button)
        button2= itemView.findViewById(R.id.button2)
        status = itemView.findViewById(R.id.activeStatus)
        body = itemView.findViewById(R.id.item1Body)
        postsAvatar = itemView.findViewById(R.id.CreatorAvatar)
        postsName = itemView.findViewById(R.id.Name)
        time = itemView.findViewById(R.id.postCardHomeTimeCreated)
        isProfile= itemView.findViewById(R.id.isUpdateProfile)
        menuBtn = itemView.findViewById(R.id.newsMenu)
        caption = itemView.findViewById(R.id.description)
        postImage = itemView.findViewById(R.id.NewsImage)
        noOfLike = itemView.findViewById(R.id.NoOfLikes)
        noOfComment = itemView.findViewById(R.id.NoOfComments)
        likePost = itemView.findViewById(R.id.LikeButton)
        commentBtn = itemView.findViewById(R.id.CommentBtn)
        shareBtn = itemView.findViewById(R.id.ShareBtn)
        myImage = itemView.findViewById(R.id.myAvatarOnhomeFeed)
        commentInput = itemView.findViewById(R.id.editTextPostCardHomeAddComment)
        commentSendBtn = itemView.findViewById(R.id.postCardHomeSendComment)
        viewAllComment = itemView.findViewById(R.id.postCardHomeViewAllComment)
        imagelen = itemView.findViewById(R.id.imagelenght)

    }
}