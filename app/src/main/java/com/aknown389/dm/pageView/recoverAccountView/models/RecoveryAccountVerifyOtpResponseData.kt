package com.aknown389.dm.pageView.recoverAccountView.models

data class RecoveryAccountVerifyOtpResponseData(
    val max_password_change:Int,
    val recoveryKey:String,
    val email:String,
    val user:String,
    val userId:String,
)
