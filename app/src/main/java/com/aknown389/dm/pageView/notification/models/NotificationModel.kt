package com.aknown389.dm.pageView.notification.models

data class NotificationModel(
    val `data`: List<NotificationDataClass>,
    val hasMorePage: Boolean,
    val message: String,
    val status: Boolean,
    val status_code: Int
)