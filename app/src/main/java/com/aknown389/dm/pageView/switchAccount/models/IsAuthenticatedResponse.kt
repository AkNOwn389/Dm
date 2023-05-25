package com.aknown389.dm.pageView.switchAccount.models

data class IsAuthenticatedResponse(
    val status: Boolean,
    val status_code:Int,
    val message:String,
    val data:IsAuthenticatedData?,
)
