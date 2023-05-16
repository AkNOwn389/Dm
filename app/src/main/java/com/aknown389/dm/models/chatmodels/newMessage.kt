package com.aknown389.dm.models.chatmodels

import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.global.VideoUrl
import com.google.gson.annotations.SerializedName

data class newMessage(
    @SerializedName("date_time")    val date_time: String,
    @SerializedName("file")         val file: List<Any>,
    @SerializedName("id")           val id: Int,
    @SerializedName("image")        val image: List<ImageUrl>,
    @SerializedName("message_body") val messageBody: String,
    @SerializedName("message_type") val messageType: Int,
    @SerializedName("receiver")     val receiver: String,
    @SerializedName("seen")         val seen: Boolean,
    @SerializedName("sender")       val sender: String,
    @SerializedName("type")         val type: String,
    @SerializedName("video")        val video: List<VideoUrl>
)