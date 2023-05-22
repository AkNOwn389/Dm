package com.aknown389.dm.db.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aknown389.dm.models.loginRegModels.Info
import com.aknown389.dm.models.loginRegModels.Token


@Entity(tableName = "userAccount")
data class UserAccountDataClass(
    @PrimaryKey
    val id:String,
    val info:Info,
    var lastLogin:String? = null,
    val refreshToken: String,
    val accessToken: String,
    val tokenType: String
)
