package com.aknown389.dm.models.chatmodels

import com.aknown389.dm.models.chatmodels.Data

data class UserChatListenerResponse(
    val status:Boolean,
    val message:String,
    val status_code:Int,
    val hasMorePage:Boolean,
    val data:List<Data>
)
