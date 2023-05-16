package com.aknown389.dm.models.userviewModels

import com.aknown389.dm.models.profileModel.UserDisplayData

data class UserDataResponseModel(
    val `data`: List<UserDisplayData>,
    val status: Boolean,
    val status_code: Int,
    val message:String,
    val hasMorePage: Boolean
)