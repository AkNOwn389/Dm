package com.aknown389.dm.models.profileModel

import com.aknown389.dm.db.local.UserProfileData

data class MeModel(
    val status_code: Int,
    val status: Boolean,
    val `data`: UserProfileData,
    val message: String
)