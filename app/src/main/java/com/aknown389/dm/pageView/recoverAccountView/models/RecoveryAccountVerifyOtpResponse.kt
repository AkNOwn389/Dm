package com.aknown389.dm.pageView.recoverAccountView.models

data class RecoveryAccountVerifyOtpResponse(
    val status:Boolean,
    val status_code:Int,
    val message:String,
    val remaining_attempt:Int? = null,
    val data:RecoveryAccountVerifyOtpResponseData
)
