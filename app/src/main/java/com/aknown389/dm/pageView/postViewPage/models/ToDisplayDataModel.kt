package com.aknown389.dm.pageView.postViewPage.models

import com.aknown389.dm.models.global.PostReactions

data class ToDisplayDataModel(
    //required
    val parentPostId: String? = null,
    val ImageOrVideoId: String? = null,
    //for images

    val imageUrlOriginal:String? = null,
    val imageUrl1000: String? = null,
    val imageUrl500:String? = null,
    val imageUrl250:String? = null,
    val media_type: Int? = null,
    //for video

    val thumbnail:String? = null,
    val videoUrl250: String? = null,
    val videoUrl500:String? = null,
    val videoUrl1000: String? = null,
    val videoUrlOriginal:String? = null,
    val playbackUrl:String? = null,

    //for comment
    val comments: String? = null,
    val created: String? = null,

    val commentId:String? = null,
    val user: String? = null,
    val type: Int? = null,
    val me:Boolean? = null,
    val Followed:Boolean? = null,
    val Follower:Boolean? = null,

    //from body
    val creator_avatar: String? = null,
    val user_full_name:String? = null,
    val creator: String? = null,
    val creator_full_name: String? = null,
    val caption:String? = null,
    val title:String? = null,
    val privacy:Char? = null,

    //Global
    val avatar: String? = null,
    var isLike:Boolean? = null,
    val NoOfcomment: Int? = null,
    var NoOflike: Int? = null,
    val reactions: PostReactions? = null,
    var reactionType:String? = null,
    var heigth:Int? = null,
    var width:Int? = null,
)
