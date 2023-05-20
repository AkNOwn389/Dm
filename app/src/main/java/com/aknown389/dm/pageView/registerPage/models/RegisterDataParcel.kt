package com.aknown389.dm.pageView.registerPage.models

data class RegisterDataParcel(
    val username:String,
    val name:String,
    val email:String,
    val password:String,
    val comfirmPassword:String,
    var registerId:String,
)
