package com.aknown389.dm.models.videoFeedModels

import com.google.gson.annotations.SerializedName

data class VideoFeedResponseModel(
    @SerializedName("status") val status: Boolean,
    @SerializedName("status_code") val statusCode:Int,
    @SerializedName("hasMorePage") val hasMorePage:Boolean,
    @SerializedName("data") val data:List<VideoDataModels>
)