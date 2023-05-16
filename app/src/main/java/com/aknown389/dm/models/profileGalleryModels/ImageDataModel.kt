package com.aknown389.dm.models.profileGalleryModels

import com.google.gson.annotations.SerializedName

data class ImageDataModel(
    @SerializedName("NoOflike")         val noOflike:Int? = null,
    @SerializedName("NoOfcomment")      val noOfcomment:Int? = null,
    @SerializedName("id")               val id:String?=null,
    @SerializedName("url")              val image: String?=null,
    @SerializedName("is_like")          val isLike:Boolean?=null,
    @SerializedName("width")            val width: Int? = null,
    @SerializedName("height")           val heigth: Int? = null,
    @SerializedName("thumbnail")        val imgW500:String? = null,
    @SerializedName("url_w1000")        val imgW1000:String? = null,
    @SerializedName("url_w250")         val imgW250:String? = null,
)