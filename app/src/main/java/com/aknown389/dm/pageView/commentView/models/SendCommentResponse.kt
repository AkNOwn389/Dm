package com.aknown389.dm.pageView.commentView.models


data class SendCommentResponse(
    val `data`: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)