package com.aknown389.dm.models.chatmodels

data class ChatPageItemData(
    val `data`: List<NullableMessage>,
    val hasMorePage: Boolean,
    val status: Boolean,
    val status_code: Int,
    val type: Int
)