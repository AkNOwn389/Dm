package com.aknown389.dm.pageView.notification.models

import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.global.VideoUrl

data class PostDataModel(
    val NoOfcomment: Int? = null,
    val NoOflike: Int? = null,
    val created_at: String? = null,
    val creator: String? = null,
    val creator_full_name: String? = null,
    val creator_avatar: String? = null,
    val description: String? = null,
    val id: String? = null,
    val image_url: List<ImageUrl>? = null,
    val is_like: Boolean? = null,
    val media_type: Int? = null,
    val title: String? = null,
    val videos_url: List<VideoUrl>? = null,
    val your_avatar: String? = null
)