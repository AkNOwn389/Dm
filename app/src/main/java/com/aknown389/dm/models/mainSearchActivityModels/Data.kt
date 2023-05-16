package com.aknown389.dm.models.mainSearchActivityModels

import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.global.PostReactions
import com.aknown389.dm.models.global.VideoUrl
import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("id")                               val id: String? = null,
    @SerializedName("bgimg")                            val bgimg: String?=null,
    @SerializedName("bio")                              val bio: String? = null,
    @SerializedName("email")                            val email: String? = null,
    @SerializedName("first_name")                       val first_name: String? = null,
    @SerializedName("gender")                           val gender: String? = null,
    @SerializedName("hobby")                            val hobby: List<Any>? = null,
    @SerializedName("interested")                       val interested: String? = null,
    @SerializedName("last_name")                        val last_name: String? = null,
    @SerializedName("location")                         val location: String? = null,
    @SerializedName("name")                             val name: String? = null,
    @SerializedName("profileimg")                       val profileimg: String? = null,
    @SerializedName("school")                           val school: String? = null,
    @SerializedName("searchType")                       val searchType: Int? = null,
    @SerializedName("user")                             val user: String? = null,
    @SerializedName("username")                         val username: String? = null,
    @SerializedName("works")                            val works: String? = null,
    @SerializedName("isFollowed")                       val isFollowed:Boolean? = null,
    @SerializedName("isFollower")                       val isFollower:Boolean? = null,
    //for recent
    @SerializedName("date_search")                      val date: String? = null,
    //for posts
    @SerializedName("NoOfcomment")                      val NoOfcomment: Int? = null,
    @SerializedName("NoOflike")                         var NoOflike: Int? = null,
    @SerializedName("created_at")                       val created_at: String? = null,
    @SerializedName("creator")                          val creator: String? = null,
    @SerializedName("creator_full_name")                val creator_full_name: String? = null,
    @SerializedName("description")                      val description: String? = null,
    @SerializedName("image_url")                        val image_url: List<ImageUrl>? = null,
    @SerializedName("media_type")                       val media_type: Int? = null,
    @SerializedName("title")                            val title: String? = null,
    @SerializedName("videos_url")                       val videos_url: List<VideoUrl>? = null,
    @SerializedName("your_avatar")                      val your_avatar: String? = null,
    @SerializedName("creator_avatar")                   val creator_avatar: String? = null,
    @SerializedName("is_like")                          var is_like:Boolean? = null,
    @SerializedName("status")                           val status:Int? = null,
    @SerializedName("privacy")                          val privacy:Char? = null,
    @SerializedName("caption")                          val caption:String? = null,
    @SerializedName("reactions")                        val reactions: PostReactions? = null,
    @SerializedName("reactionType")                     var reactionType:String? = null,
)