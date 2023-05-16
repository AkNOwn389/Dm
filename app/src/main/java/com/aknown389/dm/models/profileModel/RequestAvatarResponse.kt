package com.aknown389.dm.models.profileModel

data class RequestAvatarResponse(
    val avatar: String,
    val name: String,
    val username: String,
    val status_code: Int,
    val message: String,
    val status: Boolean
)