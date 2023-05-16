package com.aknown389.dm.api.retrofitapi

import com.aknown389.dm.models.chatmodels.ChatListenerResponse
import com.aknown389.dm.models.chatmodels.ChatUserItemModels
import com.aknown389.dm.models.chatmodels.SendMessageResponse
import com.aknown389.dm.models.chatmodels.SendMessageBody
import com.aknown389.dm.models.chatmodels.UserChatListenerResponse
import com.aknown389.dm.models.postmodel.NormalResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApi {
    @GET("/chat/messagepage/page={page}")
    suspend fun getMessagePageView(
        @Header("Authorization") token: String,
        @Path("page") page: Int,
    ):Response<ChatUserItemModels>
    @GET("/chat/getmessage/{user}/page={page}")
    suspend fun getUserMessage(
        @Header("Authorization") token: String,
        @Path("page") page: Int,
        @Path("user") pk:String
    ):Response<ChatUserItemModels>

    @POST("/chat/sendmessage")
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Body body: SendMessageBody
    ):Response<SendMessageResponse>

    @GET("/chat/seen/{id}")
    suspend fun seen(
        @Header("Authorization") token: String,
        @Path("id") id:Int
    ):Response<NormalResponseModel>

    @GET("/chat/notify/{user}")
    suspend fun userChatListener(
        @Header("Authorization") token: String,
        @Path("user") user:String
    ):Response<UserChatListenerResponse>
    @GET("/chat/notify")
    suspend fun chatListener(
        @Header("Authorization") token: String
    ):Response<ChatListenerResponse>

    @GET("/chat/deleteMessage/id={id}")
    suspend fun chatDelete(
        @Header("Authorization") token: String,
        @Path("id") id:Int
    ):Response<NormalResponseModel>
}