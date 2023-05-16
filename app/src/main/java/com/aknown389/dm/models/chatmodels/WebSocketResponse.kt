package com.aknown389.dm.models.chatmodels

data class WebSocketResponse(
    var id: Int,
    val sender_full_name: String,
    val receiver_full_name: String,
    val message_type:Int? = null,
    val username: String,
    val user_full_name:String,
    val user_avatar: String,
    val date_time: String,
    val message_body: String,
    val receiver: String,
    val seen: Boolean,
    val me:Boolean,
    val sender: String,
)
