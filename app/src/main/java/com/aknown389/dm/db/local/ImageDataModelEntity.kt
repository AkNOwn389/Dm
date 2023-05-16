package com.aknown389.dm.db.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profileGallery")
data class ImageDataModelEntity(
    @PrimaryKey
    val id:String,
    val noOflike:Int,
    val noOfcomment:Int,
    val image: String,
    val isLike:Boolean? = null,
    val width: Int? = null,
    val heigth: Int? = null,
    val imgW500:String? = null,
    val imgW1000:String? = null,
    val imgW250:String? = null,
)