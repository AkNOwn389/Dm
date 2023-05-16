package com.aknown389.dm.models.loginRegModels

data class RegisterResModelV2(
    val status_code: Int,
    val info: Info,
    val message: String,
    val status: Boolean,
    val token: Token
)