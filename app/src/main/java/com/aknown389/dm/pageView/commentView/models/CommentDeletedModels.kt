package com.aknown389.dm.pageView.commentView.models

import com.google.gson.annotations.SerializedName

data class CommentDeletedModels(
    @SerializedName("commentId")    val commentId: String,
    @SerializedName("data_type")    val dataType: Int,
    @SerializedName("type")         val type: String,
    @SerializedName("user")         val user: String
)