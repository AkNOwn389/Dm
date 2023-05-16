package com.aknown389.dm.models.mainSearchActivityModels

import com.google.gson.annotations.SerializedName

data class DataX(
    @SerializedName("date_search") val date: String? = null,
    val isFollowed: Boolean? = null,
    val isFollower: Boolean? = null,
    val location: String? = null,
    val name: String? = null,
    val user: String? = null,
    val userAvatar: String? = null,
    val searchType: Int? = null,

)