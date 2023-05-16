package com.aknown389.dm.models.loginRegModels

data class RegRequestCodeResponse(
    val `data`: RegData,
    val message: String,
    val status: Boolean,
    val status_code: Int
)