package com.aknown389.dm.pageView.recoverAccountView.models

import com.google.gson.annotations.SerializedName

data class RequestAccountRecoveryResponse(
    @SerializedName("status")               val status:Boolean,
    @SerializedName("status_code")          val statusCode:Int,
    @SerializedName("message")              val message:String,
    @SerializedName("data")                 val data: RequestAccountRecoveryResponseData? = null,
)
