package com.aknown389.dm.pageView.commentView.models

import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.global.PostReactions
import com.aknown389.dm.models.global.VideoUrl

data class NewCommentChangeReaction(
    val NoOflike: Int,
    val avatar: String,
    val comment_type: Int,
    val comments: String,
    val created: String,
    val id: String,
    val image: List<ImageUrl>,
    val post_id: String,
    val reactions: PostReactions,
    val type: String,
    val user: String,
    val video: List<VideoUrl>
)