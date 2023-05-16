package com.aknown389.dm.models.chatmodels

data class ChatListenerResponse(
    val status:Boolean?=null,
    val status_code:Int?=null,
    val message:String?=null,
    val length:Int?=null,
    val type:Int? = null,
    val data:List<NullableMessage>?=null
)
