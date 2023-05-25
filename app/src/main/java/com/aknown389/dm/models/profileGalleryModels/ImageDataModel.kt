package com.aknown389.dm.models.profileGalleryModels

import com.aknown389.dm.models.global.PostReactions
import com.google.gson.annotations.SerializedName

data class ImageDataModel(
    @SerializedName("id")                       val id:String?=null,
    @SerializedName("url")                      val original: String?=null,
    @SerializedName("NoOflike")                 var noOfLike: Int?=null,
    @SerializedName("NoOfcomment")              var noOfComment:Int?=null,
    @SerializedName("is_like")                  var isLike:Boolean?=null,
    @SerializedName("width")                    val width: Int? = null,
    @SerializedName("height")                   val height: Int? = null,
    @SerializedName("thumbnail")                val imgW500:String? = null,
    @SerializedName("url_w1000")                val imgW1000:String? = null,
    @SerializedName("url_w250")                 val imgW250:String? = null,
    @SerializedName("reactions")                val reactions: PostReactions? = null,
    @SerializedName("reactionType")             var reactionType: String? = null
)