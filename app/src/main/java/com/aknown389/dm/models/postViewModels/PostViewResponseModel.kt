package com.aknown389.dm.models.postViewModels

data class PostViewResponseModel(
    val `data`: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)