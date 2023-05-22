package com.aknown389.dm.pageView.switchAccount.models

import com.google.gson.annotations.SerializedName

data class ResponseDetails(
    val status:Boolean,
    @SerializedName("status_code")
    val statusCode:Int,
    val message:String,
    val id:String?,
    val image:String?,
    val name:String?,
    val username:String?,
    val lastLogin:String?,
    val lastLoginDisplay:String?,
)
