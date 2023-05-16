package com.aknown389.dm.models.global

data class LoginBahavior(
    val message: String,
    val status: Boolean,
    val status_code: Int,
    val id: Int,
    val username: String
)