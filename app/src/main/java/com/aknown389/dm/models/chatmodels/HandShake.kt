package com.aknown389.dm.models.chatmodels

import com.google.gson.annotations.SerializedName

data class HandShake(
    @SerializedName("chatmate")     val chatmate: ChatUserInfo,
    @SerializedName("data")         val data: ChatUserInfo,
    @SerializedName("dataType")     val dataType: String,
    @SerializedName("message")      val message: String,
    @SerializedName("status")       val status: Boolean,
    @SerializedName("status_code")  val status_code: Int
)