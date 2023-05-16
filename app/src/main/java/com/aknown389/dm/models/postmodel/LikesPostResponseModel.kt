package com.aknown389.dm.models.postmodel

import com.google.gson.annotations.SerializedName

data class LikesPostResponseModel(
    @SerializedName("message")      val message: String,
    @SerializedName("post_likes")   val postLikes: Int,
    @SerializedName("reaction")     val reaction:String,
    @SerializedName("status")       val status: Boolean
)