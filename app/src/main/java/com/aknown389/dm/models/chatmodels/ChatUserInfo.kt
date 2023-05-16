package com.aknown389.dm.models.chatmodels

data class ChatUserInfo(
    val bgimg: String,
    val bio: String,
    val gender: String,
    val hobby: List<Any>,
    val id: Int,
    val interested: String,
    val location: String,
    val name: String,
    val profileimg: String,
    val school: String,
    val user: String,
    val username: String,
    val works: String
)