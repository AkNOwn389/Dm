package com.aknown389.dm.db.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profileDetail")
data class UserProfileDetailsDataEntities(
    @PrimaryKey
    val id: Int,
    val backGroundImage: String? = null,
    val bio: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val followers: Int? = null,
    val following: Int? = null,
    val lastName: String? = null,
    val location: String? = null,
    val name: String? = null,
    val profileImage: String? = null,
    val username: String,
    val postLength: Int? = null
)