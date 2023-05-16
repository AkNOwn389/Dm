package com.aknown389.dm.pageView.commentView.models

import android.net.Uri
import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.global.PostReactions
import com.aknown389.dm.models.global.VideoUrl
import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("id")               var id:String? = null,
    @SerializedName("avatar")           var avatar: String? = null,
    @SerializedName("comments")         var comments: String? = null,
    @SerializedName("created")          var created: String? = null,
    @SerializedName("post_id")          var postId: String? = null,
    @SerializedName("image")            var image:ArrayList<ImageUrl?>? = null,
    @SerializedName("video")            var video:ArrayList<VideoUrl?>? = null,
    @SerializedName("user")             var user: String? = null,
    @SerializedName("comment_type")     var type: Int? = null,
    @SerializedName("me")               val me:Boolean? = null,
    @SerializedName("Followed")         var followed:Boolean? = null,
    @SerializedName("Follower")         var follower:Boolean? = null,
    @SerializedName("user_full_name")   var userFullName:String? = null,
    @SerializedName("NoOflike")         var noOfLike:Int? = null,
    @SerializedName("is_like")          var isLike:Boolean? = false,
    @SerializedName("reactions")        var reactions: PostReactions? = null,
    @SerializedName("reactionType")     var reactionType:String? = null,
    var imageToUpload:Uri? = null,
    var videoToUpload:Uri? = null,
    var sendKoBa:Boolean? = null,
)