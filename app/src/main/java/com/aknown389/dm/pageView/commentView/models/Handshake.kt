package com.aknown389.dm.pageView.commentView.models

import com.aknown389.dm.pageView.commentView.models.Info


data class Handshake(
    val info: Info,
    val message: String,
    val status: Boolean,
    val status_code: Int,
    val type: String
)