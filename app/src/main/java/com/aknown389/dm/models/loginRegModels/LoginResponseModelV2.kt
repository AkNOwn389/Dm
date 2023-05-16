package com.aknown389.dm.models.loginRegModels

data class LoginResponseModelV2(
    val status_code: Int,
    val info: Info,
    val message: String,
    val status: Boolean,
    val token: Token
)