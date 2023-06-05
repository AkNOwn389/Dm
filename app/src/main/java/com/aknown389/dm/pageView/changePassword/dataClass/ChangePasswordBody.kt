package com.aknown389.dm.pageView.changePassword.dataClass

data class ChangePasswordBody(
    val oldPassword:String,
    val newPassword:String,
    val confirmNewPassword:String,
    val device:String,
)
