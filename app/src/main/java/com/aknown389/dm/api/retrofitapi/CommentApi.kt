package com.aknown389.dm.api.retrofitapi


import com.aknown389.dm.models.postmodel.CommentBody
import com.aknown389.dm.models.postmodel.NormalResponseModel
import com.aknown389.dm.pageView.commentView.models.PostCommentResponse
import com.aknown389.dm.pageView.commentView.models.SendCommentResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface CommentApi {
    @GET("/comments/comment/id={id}/page={page}")
    suspend fun getComment(
        @Path("id") postId:String,
        @Path("page") page:Int
    ):Response<PostCommentResponse>

    @POST("/comments/comment")
    suspend fun comment(
        @Body body: CommentBody
    ):Response<SendCommentResponse>

    @GET("/post/delete_comment/commentId={id}/postId={postId}")
    suspend fun deletecomment(
        @Path("id") id:String,
        @Path("postId") postId: String
    ):Response<NormalResponseModel>

    @Multipart
    @PUT("/comments/sendComment/commentType={commentType}")
    suspend fun sendCommentFile(
        @Path("commentType") type:String,
        @Part("comment") comment:RequestBody,
        @Part("postId") postId:RequestBody,
        @Part file:MultipartBody.Part
    ):Response<SendCommentResponse>


    @POST("/comments/sendComment/commentType={commentType}")
    suspend fun sendCommentText(
        @Path("commentType") type:String = "text",
        @Body body:CommentBody
    ):Response<SendCommentResponse>

}