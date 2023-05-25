package com.aknown389.dm.pageView.switchAccount.remote.api

import com.aknown389.dm.pageView.switchAccount.models.IsAuthenticatedResponse
import com.aknown389.dm.pageView.switchAccount.models.RefreshTokenBody
import com.aknown389.dm.pageView.switchAccount.models.RefreshTokenResponse
import com.aknown389.dm.pageView.switchAccount.models.ResponseDetails
import com.aknown389.dm.pageView.switchAccount.models.UserDetailBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface Api {
    @POST("/user/getUserDetail")
    suspend fun getUserDetail(
        @Body body:UserDetailBody
    ):Response<ResponseDetails>

    @GET("/user/isAuthenticated")
    suspend fun isAuthenticatedForOtherUser(
        @Header("Authorization") token:String
    ): Response<IsAuthenticatedResponse>

    @GET("/user/isAuthenticated")
    suspend fun isAuthenticated():Response<IsAuthenticatedResponse>

    @POST("/api/token/refresh/")
    suspend fun refreshToken(
        @Header("Authorization") token:String,
        @Body body: RefreshTokenBody
    ):Response<RefreshTokenResponse>
}