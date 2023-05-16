package com.aknown389.dm.pageView.postViewPage.Models

import com.aknown389.dm.models.global.PostReactions

data class ToDisplayDataModel(
    val NoOfcomment: Int? = null,
    var NoOflike: Int? = null,
    val creator: String? = null,
    val creator_full_name: String? = null,
    val id: String? = null,
    val image_url: String? = null,
    val media_type: Int? = null,
    val videos_url: String? = null,
    val creator_avatar: String? = null,
    var is_like:Boolean? = null,
    val caption:String? = null,
    val title:String? = null,
    val privacy:Char? = null,
    //for comment
    val avatar: String? = null,
    val comments: String? = null,
    val created: String? = null,
    val post_id: String? = null,
    val commentId:String? = null,
    val user: String? = null,
    val type: Int? = null,
    val me:Boolean? = null,
    val Followed:Boolean? = null,
    val Follower:Boolean? = null,
    val user_full_name:String? = null,
    val reactions: PostReactions? = null,
    var reactionType:String? = null,
)
