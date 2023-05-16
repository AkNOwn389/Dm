package com.aknown389.dm.models.homepostmodels

import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.global.PostReactions
import com.aknown389.dm.models.global.VideoUrl
import com.google.gson.annotations.SerializedName


data class PostDataModel(
    @SerializedName("NoOfComment")              val NoOfcomment: Int? = null,
    @SerializedName("NoOflike")                 var NoOflike: Int? = null,
    @SerializedName("created_at")               val created_at: String? = null,
    @SerializedName("creator")                  val creator: String? = null,
    @SerializedName("creator_full_name")        val creator_full_name: String? = null,
    @SerializedName("description")              val description: String? = null,
    @SerializedName("id")                       val id: String,
    @SerializedName("image_url")                val image_url: List<ImageUrl?>? = null,
    @SerializedName("media_type")               var media_type: Int? = null,
    @SerializedName("title")                    val title: String? = null,
    @SerializedName("videos_url")               val videos_url: List<VideoUrl?>? = null,
    @SerializedName("your_avatar")              val your_avatar: String? = null,
    @SerializedName("creator_avatar")           val creator_avatar: String? = null,
    @SerializedName("is_like")      var is_like:Boolean? = null,
    @SerializedName("me")                       val me:Boolean? = null,
    @SerializedName("privacy")                  var privacy:Char? = null,
    @SerializedName("videos")                   var videos: String? = null,
    @SerializedName("playback_url")             val playback_url:String? = null,
    @SerializedName("url_w1000")                val url_w1000:String? = null,
    @SerializedName("url_w250")                 val url_w250:String? = null,
    @SerializedName("width")                    val width:Int? = null,
    @SerializedName("height")                   val height:Int? = null,
    @SerializedName("reactions")                val reactions: PostReactions? = null,
    @SerializedName("reactionType") var reactionType:String? = null,
    var videoPlayerPlaybackPosition:Long = 0
    )