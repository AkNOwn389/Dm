package com.aknown389.dm.models.profileModel

data class MeModel(
    val status_code: Int,
    val status: Boolean,
    val `data`: UserProfileData,
    val message: String
)