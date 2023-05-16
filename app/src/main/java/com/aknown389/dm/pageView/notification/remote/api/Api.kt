package com.aknown389.dm.pageView.notification.remote.api

import com.aknown389.dm.pageView.notification.models.NotificationModel
import com.aknown389.dm.models.postViewModels.PostViewResponseModel
import com.aknown389.dm.models.postmodel.NormalResponseModel
import com.aknown389.dm.pageView.notification.models.NotificationBadge
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {
    @GET("/notification/my/page={page}")
    suspend fun notifications(
        @Path("page") page:Int
    ): Response<NotificationModel>

    suspend fun notify(
    ): Response<NotificationModel>

    @GET("/notification/notificationBadge")
    suspend fun notificationBadge(
    ): Response<NotificationBadge>

    @GET("/notification/chatBadge")
    suspend fun chatBadge(
    ): Response<NotificationBadge>

    @GET("/notification/seen/id={id}")
    suspend fun bahaviorSeenNotification(
        @Path("id") id:Int
    ): Response<NormalResponseModel>
    @GET("/post/getpost/id={id}")
    suspend fun getPostData(
        @Path("id") id:String
    ): Response<PostViewResponseModel>
    @GET("/notification/delete/id={id}")
    suspend fun deleteNotification(
        @Path("id") id:Int
    ): Response<NormalResponseModel>
}