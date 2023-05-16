package com.aknown389.dm.models.chatmodels

data class ConnectionStablish(
    val chatmate: ChatUserInfo,
    val `data`: ChatUserInfo,
    val message: String,
    val status: Boolean,
    val status_code: Int
)