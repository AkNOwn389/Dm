package com.aknown389.dm.db.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.global.VideoUrl
import com.aknown389.dm.models.global.PostReactions

@Entity(tableName = "homeFeeds")
data class HomeFeedDataEntity(
    @PrimaryKey
    val id: String,
    val noOfComment: Int? = null,
    val noOfLike: Int?= null,
    val createdAt: String?= null,
    val creator: String?= null,
    val creatorFullName: String?= null,
    val description: String?= null,
    val imageUrl: List<ImageUrl?>?= null,
    var mediaType: Int?= null,
    val title: String?= null,
    val videosUrl: List<VideoUrl?>? = null,
    val yourAvatar: String?= null,
    val creatorAvatar: String?= null,
    val isLike:Boolean?= null,
    val me:Boolean?= null,
    var privacy:Char?= null,
    var videos: String?= null,
    val playbackUrl:String?= null,
    val urlW1000:String?= null,
    val urlW250:String?= null,
    val width:Int?= null,
    val height:Int?= null,
    val reactions: PostReactions,
    var videoPlayerPlaybackPosition:Long = 0
)
