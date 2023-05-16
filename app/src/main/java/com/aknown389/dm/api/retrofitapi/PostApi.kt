package com.aknown389.dm.api.retrofitapi

import com.aknown389.dm.models.homepostmodels.FeedResponseModelV2
import com.aknown389.dm.models.postmodel.LikesPostBodyModel
import com.aknown389.dm.models.postmodel.LikesPostResponseModel
import com.aknown389.dm.models.profileGalleryModels.MyGalleryResponseModel
import com.aknown389.dm.models.postmodel.UploadPostResponseModel
import com.aknown389.dm.models.postmodel.UploadTextPostBody
import com.aknown389.dm.models.postmodel.CommentBody
import com.aknown389.dm.models.postmodel.NormalResponseModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface PostApi {
    @GET("/post/mygallery/page={page}")
    suspend fun mygallery(
        @Path("page") page: Int,
        @Header("Authorization") token: String
    ): Response<MyGalleryResponseModel>
    @POST("/post/uploadtext")
    suspend fun uploadTextPost(
        @Header("Authorization") key: String,
        @Body body: UploadTextPostBody
    ): Response<UploadPostResponseModel>

    @Multipart
    @PUT("/post/upload")
    suspend fun uploadImage(
        @Header("Authorization") key: String,
        @Part image: MultipartBody.Part,
        @Part("caption") caption: RequestBody,
    ): Response<UploadPostResponseModel>

    @Multipart
    @PUT("/post/upload")
    suspend fun uploadMultipleImages(
        @Header("Authorization") key: String,
        @Part("privacy") privacy:RequestBody,
        @Part("caption") caption: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Response<UploadPostResponseModel>

    @POST("/post/comment")
    suspend fun comment(
        @Header("Authorization") token: String,
        @Body body: CommentBody
    ):Response<UploadPostResponseModel>

    @GET("/post/postlist/page={page}")
    suspend fun getPostList(
        @Header("Authorization")token: String,
        @Path("page") page:Int
    ):Response<FeedResponseModelV2>

    @GET("/post/postview/{user}/page={page}")
    suspend fun postview(
        @Header("Authorization")token: String,
        @Path("user") user:String,
        @Path("page") page:Int
    ):Response<FeedResponseModelV2>
    @POST("/post/likepost")
    suspend fun likepost(
        @Header("Authorization") token: String,
        @Body likebody: LikesPostBodyModel
    ): Response<LikesPostResponseModel>

    @GET("post/deletePosts/postId={postId}")
    suspend fun deletePost(
        @Header("Authorization") token: String,
        @Path("postId") postId:String
    ):Response<NormalResponseModel>

    @GET("post/changePrivacy/{id}/privacy={privacy}")
    suspend fun changePrivacy(
        @Header("Authorization") token: String,
        @Path("id") postId:String,
        @Path("privacy") privacy:String
    ):Response<NormalResponseModel>
}