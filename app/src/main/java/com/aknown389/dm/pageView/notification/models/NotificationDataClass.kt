package com.aknown389.dm.pageView.notification.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aknown389.dm.models.global.PostReactions
import com.google.gson.annotations.SerializedName


@Entity(tableName = "NotificationData")
data class NotificationDataClass(
    @SerializedName("id") @PrimaryKey                           val id: Int,
    @SerializedName("date")                                     val date: String,
    @SerializedName("description")                              val description: String,
    @SerializedName("notifType")                                val notificationType: Int,
    @SerializedName("seen")                                     val seen: Boolean,
    @SerializedName("subjectImage")                             val subjectImage: String,
    @SerializedName("subjectPostsId")                           val subject_id: String,
    @SerializedName("title")                                    val title: String,
    @SerializedName("userToNotify")                             val user: String,
    @SerializedName("subjectFullName")                          val subjectFullName: String? = null,
    @SerializedName("subjectUserName")                          val subjectUserName: String? = null,
    @SerializedName("NoOfcomment")                              val NoOfComment: Int? = null,
    @SerializedName("NoOflike")                                 val NoOfLike: Int? = null,
    @SerializedName("created_at")                               val created_at: String? = null,
    @SerializedName("reactions")                                val reactions: PostReactions? = null,
    @SerializedName("display_date")                             val display_date: String? = null,
)