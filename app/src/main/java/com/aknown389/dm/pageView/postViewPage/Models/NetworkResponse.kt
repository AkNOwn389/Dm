package com.aknown389.dm.pageView.postViewPage.Models

data class NetworkResponse(
    val `data`: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)