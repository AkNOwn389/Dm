package com.aknown389.dm.pageView.recoverAccountView.models

import com.google.gson.annotations.SerializedName

data class RequestAccountRecoveryResponseData(
    @SerializedName("requestId")            val requestId:String?,
    @SerializedName("email")                val email:String?,
)
