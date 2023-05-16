package com.aknown389.dm.pageView.commentView.models


data class PostCommentResponse(
    val `data`: List<Data>,
    val hasMorePage: Boolean,
    val message: String,
    val status: Boolean,
    val status_code: Int
)