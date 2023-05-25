package com.aknown389.dm.db.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aknown389.dm.models.global.PostReactions

@Entity(tableName = "profileGallery")
data class ImageDataModelEntity(
    @PrimaryKey
    val id:String,
    val noOfLike:Int,
    val noOfComment:Int,
    val image: String,
    val isLike:Boolean? = null,
    val width: Int? = null,
    val height: Int? = null,
    val imgW500:String? = null,
    val imgW1000:String? = null,
    val imgW250:String? = null,
    val reactions: PostReactions? = null,
    var reactionType: String? = null
)