package com.aknown389.dm.pageView.switchAccount.models

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(
    val status:Boolean? = true,
    @SerializedName("accesstoken")          val accesstoken:String,
    @SerializedName("refresh_token")        val refresh_token:String,
)
