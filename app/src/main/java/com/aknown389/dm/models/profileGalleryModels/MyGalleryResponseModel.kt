package com.aknown389.dm.models.profileGalleryModels

import com.aknown389.dm.models.global.ImageUrl
import com.google.gson.annotations.SerializedName

data class MyGalleryResponseModel(
    @SerializedName("data")                 val `data`: List<ImageUrl>,
    @SerializedName("hasMorePage")          val hasMorePage: Boolean,
    @SerializedName("lenght")               val length: Int,
    @SerializedName("message")              val message: String,
    @SerializedName("status")               val status: Boolean,
    @SerializedName("status_code")          val status_code: Int
)