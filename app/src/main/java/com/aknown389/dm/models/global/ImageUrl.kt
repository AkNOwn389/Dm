package com.aknown389.dm.models.global

import com.google.gson.annotations.SerializedName

data class ImageUrl(
    @SerializedName("id")                       val id:String?=null,
    @SerializedName("url")                      val image: String?=null,
    @SerializedName("NoOflike")                 val noOfLike: Int?=null,
    @SerializedName("NoOfcomment")              val noOfComment:Int?=null,
    @SerializedName("is_like")                  val isLike:Boolean?=null,
    @SerializedName("width")                    val width: Int? = null,
    @SerializedName("height")                   val heigth: Int? = null,
    @SerializedName("thumbnail")                val imgW500:String? = null,
    @SerializedName("url_w1000")                val imgW1000:String? = null,
    @SerializedName("url_w250")                 val imgW250:String? = null,
    @SerializedName("reactions")                val reactions: PostReactions? = null,
    @SerializedName("reactionType")             val reactionType: String? = null
)