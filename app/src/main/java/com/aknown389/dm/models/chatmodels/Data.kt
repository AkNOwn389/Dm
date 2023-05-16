package com.aknown389.dm.models.chatmodels

import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.global.VideoUrl

data class Data(
    var id: Int,
    val sender_full_name: String,
    val receiver_full_name: String,
    val message_type:Int,
    val username: String,
    val user_full_name:String,
    val user_avatar: String,
    val date_time: String,
    val message_body: String,
    val receiver: String,
    val seen: Boolean,
    val video:List<VideoUrl>,
    val image:List<ImageUrl>,
    val me:Boolean,
    val sender: String,
)