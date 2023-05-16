package com.aknown389.dm.models.postmodel

data class LikesPostBodyModel(
    val postId: String,
    val postType:String,
    val reactionType:String,
    val type:String = "react",
)
