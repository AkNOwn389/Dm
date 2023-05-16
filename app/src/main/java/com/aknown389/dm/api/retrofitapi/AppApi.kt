package com.aknown389.dm.api.retrofitapi

import com.aknown389.dm.models.loginRegModels.RegisterModel
import com.aknown389.dm.models.loginRegModels.RegisterResModelV2
import com.aknown389.dm.models.*
import com.aknown389.dm.models.loginRegModels.LoginModel
import com.aknown389.dm.models.loginRegModels.LoginResponseModelV2
import com.aknown389.dm.models.loginRegModels.LogoutResponseModel
import com.aknown389.dm.models.profileModel.RequestAvatarResponse
import com.aknown389.dm.models.global.LoginBahavior
import com.aknown389.dm.models.homepostmodels.FeedResponseModelV2
import com.aknown389.dm.models.logoutmodel.LogOutBodyModel
import com.aknown389.dm.models.postmodel.LikesPostBodyModel
import com.aknown389.dm.models.postmodel.LikesPostResponseModel
import com.aknown389.dm.models.postmodel.UploadPostResponseModel
import com.aknown389.dm.models.profileModel.MeModel
import com.aknown389.dm.models.profileModel.UpdateDetailsBodyModel
import com.aknown389.dm.models.showFriendModels.UserCardViewResponseModel
import com.aknown389.dm.models.userviewModels.FollowUserResponseModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface AppApi {
    // call method
    @POST("/user/signup")
    fun register(
        @Body registerBody: RegisterModel
    ): Call<RegisterResModelV2>

    @POST("/user/login")
    fun loginC(
        @Body loginBody: LoginModel
    ): Call<LoginResponseModelV2>

    @POST("/post/likepost")
    fun likepostC(
        @Header("Authorization") token: String,
        @Body likebody: LikesPostBodyModel
    ): Call<LikesPostResponseModel>


    @GET("/user/follow/{user}")
    fun followUser(
        @Header("Authorization") key: String,
        @Path("user") user: String
    ): Call<FollowUserResponseModel>


    //Response method
    // suspend mo ulyanin ka




    @GET("/profile/getavatar/{user}")
    suspend fun getAvatarR(
        @Path("user") user: String,
        @Header("Authorization") token: String
    ): Response<RequestAvatarResponse>


    @GET("/user/loginBahavior")
    suspend fun bahavior(
        @Header("Authorization"
        ) token: String): Response<LoginBahavior>

    @GET("/feed/newsfeed/page={page}")
    suspend fun newsfeed(
        @Path("page") page: Int,
        @Header("Authorization") token: String
    ): Response<FeedResponseModelV2>

    @GET("/profile/me")
    suspend fun me(
        @Header("Authorization") token: String
    ): Response<MeModel>

    @Multipart
    @PUT("/profile/profilePictureUpdate")
    suspend fun updateProfilePicture(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part,
        @Part("caption") caption: RequestBody
    ): Response<UploadPostResponseModel>

    @Multipart
    @PUT("/profile/profileCoverUpdate")
    suspend fun updateProfileCover(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part,
        @Part("caption") caption: RequestBody
    ): Response<UploadPostResponseModel>

    @POST("/profile/updatedetails")
    suspend fun updateDetails(
        @Header("Authorization") token: String,
        @Body body: UpdateDetailsBodyModel,
    ): Response<UploadPostResponseModel>

    @POST("/user/logout")
    suspend fun logout(
        @Header("Authorization") token: String,
        @Body body: LogOutBodyModel
    ): Response<LogoutResponseModel>

    @GET("/user/usersuggested/page={page}")
    suspend fun getSuggestedFriend(
        @Header("Authorization") token: String,
        @Path("page") page: Int
    ):Response<UserCardViewResponseModel>

    @GET("/user/following/page={page}")
    suspend fun getFollowing(
        @Header("Authorization") token: String,
        @Path("page") page: Int
    ):Response<UserCardViewResponseModel>

    @GET("/user/followers/page={page}")
    suspend fun getFollower(
        @Header("Authorization") token: String,
        @Path("page") page: Int
    ):Response<UserCardViewResponseModel>

    @GET("/user/friends/page={page}")
    suspend fun getFriends(
        @Header("Authorization") token: String,
        @Path("page") page: Int
    ):Response<UserCardViewResponseModel>
}