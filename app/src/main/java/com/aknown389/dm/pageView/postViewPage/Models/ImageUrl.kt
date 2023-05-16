package com.aknown389.dm.pageView.postViewPage.Models

import com.aknown389.dm.models.global.PostReactions

data class ImageUrl(
    val NoOfcomment: Int,
    val NoOflike: Int,
    val height: String,
    val id: String,
    val is_like: Boolean,
    val reactionType: String,
    val reactions: PostReactions,
    val thumbnail: String,
    val url: String,
    val url_w1000: String,
    val url_w250: String,
    val width: String
)