package com.aknown389.dm.pageView.notification.models

data class NotificationBadge(
    val notification: Int,
    val chat:Int,
    val message: String,
    val status: Boolean,
    val status_code: Int
)