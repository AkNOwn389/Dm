package com.aknown389.dm.pageView.notification.models

import com.aknown389.dm.models.global.VideoUrl
import com.aknown389.dm.models.postmodel.ImageUrl

data class GetPostData(
    val NoOfcomment: Int,
    val NoOflike: Int,
    val created_at: String,
    val creator: String,
    val creator_avatar: String,
    val creator_full_name: String,
    val description: String,
    val id: String,
    val image_url: List<ImageUrl?>? = null,
    val is_like: Boolean,
    val media_type: Int,
    val perma_link: String? = null,
    val privacy: Char,
    val status: Int? = null,
    val title: String? = null,
    val videos_url: List<VideoUrl?>? = null,
    val your_avatar: String? = null,
)