package com.aknown389.dm.models.chatmodels

import com.aknown389.dm.models.chatmodels.Data

data class ChatUserItemModels(
    val `data`: List<Data>,
    val hasMorePage: Boolean,
    val message: String,
    val status: Boolean,
    val status_code: Int
)