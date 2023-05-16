package com.aknown389.dm.api.retrofitapi

import com.aknown389.dm.pageView.notification.models.NotificationModel
import com.aknown389.dm.models.postViewModels.PostViewResponseModel
import com.aknown389.dm.models.postmodel.NormalResponseModel
import com.aknown389.dm.pageView.notification.models.NotificationBadge
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface NotificationApi {
    @GET("/notification/my/page={page}")
    suspend fun notifications(
        @Header("Authorization") token:String,
        @Path("page") page:Int
    ):Response<NotificationModel>

    suspend fun notify(
        @Header("Authorization") token: String
    ):Response<NotificationModel>

    @GET("/notification/notificationBadge")
    suspend fun notificationBadge(
        @Header("Authorization") token:String
    ):Response<NotificationBadge>

    @GET("/notification/chatBadge")
    suspend fun chatBadge(
        @Header("Authorization") token:String
    ):Response<NotificationBadge>

    @GET("/notification/seen/id={id}")
    suspend fun bahaviorSeenNotification(
        @Header("Authorization") token:String,
        @Path("id") id:Int
    ):Response<NormalResponseModel>
    @GET("/post/getpost/id={id}")
    suspend fun getPostData(
        @Header("Authorization") token:String,
        @Path("id") id:String
    ):Response<PostViewResponseModel>
    @GET("/notification/delete/id={id}")
    suspend fun deleteNotification(
        @Header("Authorization") token:String,
        @Path("id") id:Int
    ):Response<NormalResponseModel>
}