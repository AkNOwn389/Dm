package com.aknown389.dm.pageView.newsView

import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aknown389.dm.R
import com.aknown389.dm.reactionTesting.ReactImageButton
import com.makeramen.roundedimageview.RoundedImageView

class NewsViewHolder(view:View):ViewHolder(view) {
    @JvmField
    var NewsImage: ImageView? = null
    @JvmField
    var caption: TextView? = null
    @JvmField
    var creatorAvatar: ImageView? = null
    @JvmField
    var creatorName: TextView? = null
    @JvmField
    var createDate: TextView? = null
    @JvmField
    var noOflikes: TextView? = null
    @JvmField
    var noOfComments: TextView? = null
    @JvmField
    var viewallcomment: TextView? = null
    @JvmField
    var likeBtn: ReactImageButton? = null
    @JvmField
    var commentBtn: ImageButton? = null
    @JvmField
    var shareBtn: ImageButton? = null
    @JvmField
    var menuBtn: ImageButton? = null
    @JvmField
    var privacy: TextView? = null
    @JvmField
    var cardView: CardView? = null
    @JvmField
    var title:TextView? = null
    @JvmField
    var content:TextView? = null
    @JvmField
    var author:TextView? = null
    @JvmField
    var inputSearch:EditText? = null
    @JvmField
    var buttonSearch:ImageButton? = null
    @JvmField
    var frame:FrameLayout? = null
    @JvmField
    var contraintlayout:ConstraintLayout? = null
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
        contraintlayout = itemView.findViewById(R.id.constraintLayout)
        frame = itemView.findViewById(R.id.frameLayout)
        cardView = itemView.findViewById(R.id.newsCardViewImage)
        NewsImage = itemView.findViewById(R.id.NewsImage)
        caption = itemView.findViewById(R.id.description)
        creatorAvatar = itemView.findViewById(R.id.CreatorAvatar)
        creatorName = itemView.findViewById(R.id.Name)
        createDate = itemView.findViewById(R.id.TimeCreated)
        noOflikes = itemView.findViewById(R.id.NoOfLikes)
        noOfComments = itemView.findViewById(R.id.NoOfComments)
        viewallcomment = itemView.findViewById(R.id.ViewAllComment)
        likeBtn = itemView.findViewById(R.id.LikeButton)
        commentBtn = itemView.findViewById(R.id.CommentBtn)
        shareBtn = itemView.findViewById(R.id.ShareBtn)
        menuBtn = itemView.findViewById(R.id.newsMenu)
        privacy = itemView.findViewById(R.id.privacy)
        title = itemView.findViewById(R.id.title)
        content = itemView.findViewById(R.id.content)
        author = itemView.findViewById(R.id.author)

        inputSearch = itemView.findViewById(R.id.inputSearch)
        buttonSearch = itemView.findViewById(R.id.buttonSearch)
    }
}