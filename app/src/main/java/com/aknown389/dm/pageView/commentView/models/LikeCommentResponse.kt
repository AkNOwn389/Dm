package com.aknown389.dm.pageView.commentView.models

data class LikeCommentResponse(
    val commentLike: Int,
    val message: String,
    val status: Boolean,
    val status_code: Int,
    val reaction:String
)