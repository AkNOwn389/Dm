package com.aknown389.dm.models.postViewModels

import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.global.PostReactions
import com.aknown389.dm.models.global.VideoUrl

data class Data(
    val NoOfcomment: Int,
    val NoOflike: Int,
    val created: String,
    val created_at: String,
    val creator: String,
    val creator_avatar: String,
    val creator_full_name: String,
    val description: String,
    val id: String,
    val image_url: List<ImageUrl>,
    val is_like: Boolean,
    val media_type: Int,
    val perma_link: String,
    val privacy: String,
    val reactions: PostReactions,
    val status: Int,
    val title: String,
    val videos_url: List<VideoUrl>,
    val your_avatar: String
)