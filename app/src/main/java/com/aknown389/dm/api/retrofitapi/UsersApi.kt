package com.aknown389.dm.api.retrofitapi

import com.aknown389.dm.models.userviewModels.FollowUserResponseModel
import com.aknown389.dm.models.postmodel.NormalResponseModel
import com.aknown389.dm.models.userviewModels.UserDataResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface UsersApi {
    @GET("/user/follow/{user}")
    suspend fun followUser(
        @Header("Authorization") key: String,
        @Path("user") user: String
    ): Response<FollowUserResponseModel>

    @GET("/user/usersuggested/page={page}")
    suspend fun getSuggestedFriend(
        @Header("Authorization") token: String,
        @Path("page") page: Int
    ):Response<UserDataResponseModel>

    @GET("/user/followers/page={page}")
    suspend fun getFollower(
        @Header("Authorization") token: String,
        @Path("page") page: Int
    ):Response<UserDataResponseModel>

    @GET("/user/following/page={page}")
    suspend fun getFollowing(
        @Header("Authorization") token: String,
        @Path("page") page: Int
    ):Response<UserDataResponseModel>
    @GET("/user/cancelRequest/user={user}")
    suspend fun canselRequest(
        @Header("Authorization") token: String,
        @Path("user") user: String
    ):Response<NormalResponseModel>

    @GET("/user/denied/{user}")
    suspend fun deniedRequest(
        @Header("Authorization") token: String,
        @Path("user") user: String
    ):Response<NormalResponseModel>
}