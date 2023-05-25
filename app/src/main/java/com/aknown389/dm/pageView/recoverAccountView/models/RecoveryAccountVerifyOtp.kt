package com.aknown389.dm.pageView.recoverAccountView.models

data class RecoveryAccountVerifyOtp(
    val email:String,
    val code:String,
    val requestId:String,
)
