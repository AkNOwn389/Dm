package com.aknown389.dm.models.profileModel

data class UserDisplayData(
    val Follower: Boolean,
    val Following: Boolean,
    val gender: String,
    val hobby: List<Any>,
    val interested: String,
    val location: String,
    val name: String,
    val profileimg: String,
    val school: String,
    val user: String,
    val works: String
)