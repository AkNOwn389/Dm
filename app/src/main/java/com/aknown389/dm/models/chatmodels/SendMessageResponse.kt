package com.aknown389.dm.models.chatmodels

import com.aknown389.dm.models.chatmodels.Data

data class SendMessageResponse(
    val status:Boolean,
    val status_code: Int,
    val message:String,
    val data: Data
)
