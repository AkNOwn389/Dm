package com.aknown389.dm.pageView.recoverAccountView.models

data class AccountRecoveryChangePasswordBody(
    val newPassword:String,
    val newPasswordConfirm:String,
    val recoveryKey:String,
    val user:String,
    val userId:String,
    val email:String,
)
