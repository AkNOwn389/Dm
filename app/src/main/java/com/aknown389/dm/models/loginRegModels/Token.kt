package com.aknown389.dm.models.loginRegModels

import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("refresh_token") val refreshToken: String? = null,
    @SerializedName("accesstoken")   val accessToken: String? = null,
    @SerializedName("tokenType")     val tokenType: String? = null
)