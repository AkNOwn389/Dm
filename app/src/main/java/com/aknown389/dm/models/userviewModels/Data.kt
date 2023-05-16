package com.aknown389.dm.models.userviewModels

data class Data(
    val bgimg: String,
    val bio: String,
    val email: String,
    val first_name: String,
    val followers: Int,
    val following: Int,
    val gender: String,
    val hobby: List<Any>,
    val id: Int,
    val interested: String,
    val isFollower: Boolean,
    val isFollowing: Boolean,
    val last_name: String,
    val location: String,
    val name: String,
    val post_lenght: Int,
    val profileimg: String,
    val school: String,
    val user: String,
    val username: String,
    val works: String
)