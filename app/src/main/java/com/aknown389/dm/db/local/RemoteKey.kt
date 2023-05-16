package com.aknown389.dm.db.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "newsRemoteKey")
data class RemoteKey(
    @PrimaryKey
    val dataId:String,
    val prevKey:Int,
    val nextKey:Int
)
