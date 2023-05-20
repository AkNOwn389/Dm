package com.aknown389.dm.pageView.photoView.models

import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.global.PostReactions

data class Parcel(
    val postId:String? = null,
    val userAvatar:String? = null,
    val username:String? = null,
    val userFullName:String? = null,
    val images:List<ImageUrl?>? = null,
    val myPosition:Int = 0,
)
