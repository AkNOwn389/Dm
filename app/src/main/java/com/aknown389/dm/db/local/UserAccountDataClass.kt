package com.aknown389.dm.db.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "userAccount")
data class UserAccountDataClass(
    @PrimaryKey
    val username:String,
    val refreshToken: String,
    val accessToken: String,
    val tokenType: String
)
