package com.aknown389.dm.models.global

import com.google.gson.annotations.SerializedName

data class VideoUrl(
    @SerializedName("id")           val id:String?=null,
    @SerializedName("original")     val videos: String?=null,
    @SerializedName("url_w1000")    val w1000: String?=null,
    @SerializedName("url_w500")     val w500: String?=null,
    @SerializedName("url_w250")     val w250: String?=null,
    @SerializedName("thumbnail")    val thumbnail:String? = null,
    @SerializedName("playback_url") val playbackUrl:String? = null,
    @SerializedName("width")        val width:Int? = null,
    @SerializedName("height")       val height:Int? = null,
    var playbackPosition:Int = 0
)
