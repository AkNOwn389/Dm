package com.aknown389.dm.models.news


import com.aknown389.dm.models.global.PostReactions
import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("author")           val author: String? = null,
    @SerializedName("avatar")           val avatar: String? = null,
    @SerializedName("content")          val content: String? = null,
    @SerializedName("description")      val description: String? = null,
    @SerializedName("id")               val id: String? = null,
    @SerializedName("name")             val name: String? = null,
    @SerializedName("news_id")          val news_id: String? = null,
    @SerializedName("noOfComment")      val noOfComment: Int? = null,
    @SerializedName("noOfLike")         var noOfLike: Int? = null,
    @SerializedName("noOfShare")        val noOfShare: Int? = null,
    @SerializedName("publishedAt")      val publishedAt: String? = null,
    @SerializedName("title")            val title: String? = null,
    @SerializedName("url")              val url: String? = null,
    @SerializedName("urlToImage")       val urlToImage: String? = null,
    @SerializedName("news_type")        val newsType: Int? = null,
    @SerializedName("is_like")          var is_like:Boolean? = null,
    @SerializedName("reactions")        val reactions: PostReactions? = null,
    @SerializedName("reactionType")     var reactionType:String? = null,
)