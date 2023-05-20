package com.aknown389.dm.models.loginRegModels

data class RegisterModel(
    val code: String,
    val cridential: String,
    val username: String,
    val email: String,
    val name: String,
    val password: String,
    val password2: String
)
