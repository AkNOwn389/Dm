package com.aknown389.dm.pageView.notification.models

data class GetPostModel(
    val `data`: GetPostData,
    val message: String,
    val status: Boolean,
    val status_code: Int
)