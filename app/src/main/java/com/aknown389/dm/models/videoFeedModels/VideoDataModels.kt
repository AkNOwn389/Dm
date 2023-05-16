package com.aknown389.dm.models.videoFeedModels
import com.aknown389.dm.models.global.PostReactions
import com.google.gson.annotations.SerializedName

data class VideoDataModels(
    @SerializedName("NoOfComment")      val noOfComment: Int? = null,
    @SerializedName("NoOflike") var noOfLike: Int? = null,
    @SerializedName("created_at")       val createdAt: String? = null,
    @SerializedName("creator")          val creator: String? = null,
    @SerializedName("creator_full_name")val creatorFullName: String? = null,
    @SerializedName("description")      val description: String? = null,
    @SerializedName("id")               val id: String? = null,
    @SerializedName("media_type")       val mediaType: Int? = null,
    @SerializedName("title")            val title: String? = null,
    @SerializedName("video_url")        val videosUrl: String? = null,
    @SerializedName("thumbnail")        val thumbnail: String? = null,
    @SerializedName("your_avatar")      val yourAvatar: String? = null,
    @SerializedName("creator_avatar")   val creatorAvatar: String? = null,
    @SerializedName("is_like") var isLike:Boolean? = null,
    @SerializedName("me")               val me:Boolean? = null,
    @SerializedName("privacy")          var privacy:Char? = null,
    @SerializedName("width")            val width:Int? = null,
    @SerializedName("height")           val height:Int? = null,
    @SerializedName("url_w500")         val url_w500:String? = null,
    @SerializedName("url_w250")         val url_w250:String? = null,
    @SerializedName("playback_url")     val playback_url:String? = null,
    val reactions: PostReactions? = null,
    var reactionType:String? = null,
    var playbackPosition:Long = 0
)
