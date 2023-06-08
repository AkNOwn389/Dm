package com.aknown389.dm.pageView.videoGallery.dataClass

import com.aknown389.dm.models.global.VideoUrl
import com.google.gson.annotations.SerializedName

data class VideoGalleryDataClassResponse(
    @SerializedName("data")         val `data`: List<VideoUrl>,
    @SerializedName("hasMorePage")  val hasMorePage: Boolean,
    @SerializedName("lenght")       val length: Int,
    @SerializedName("message")      val message: String,
    @SerializedName("status")       val status: Boolean,
    @SerializedName("status_code")  val statusCode: Int
)