package com.aknown389.dm.models.profileGalleryModels

data class MyGalleryResponseModel(
    val `data`: List<ImageDataModel>,
    val hasMorePage: Boolean,
    val lenght: Int,
    val message: String,
    val status: Boolean,
    val status_code: Int
)