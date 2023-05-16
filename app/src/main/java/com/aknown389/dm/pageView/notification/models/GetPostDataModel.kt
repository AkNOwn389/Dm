package com.aknown389.dm.pageView.notification.models

data class GetPostDataModel(
    val `data`: PostDataModel,
    val message: String,
    val status: Boolean,
    val status_code: Int
)