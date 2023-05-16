package com.aknown389.dm.models.showFriendModels

data class UserCardViewResponseModel(
    val `data`: List<showFriendData>,
    val status: Boolean,
    val message: String,
    val status_code: Int
)