package com.aknown389.dm.pageView.commentView.models

data class LikeCommentBody(
    val postId:String,
    val commentId:String,
    val postType:String,
    val reactionType:String,
    val type:String
)