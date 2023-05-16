package com.aknown389.dm.pageView.commentView.remote.api

import com.aknown389.dm.models.postmodel.CommentBody
import com.aknown389.dm.pageView.commentView.models.LikeCommentBody
import com.aknown389.dm.pageView.commentView.models.LikeCommentResponse
import com.aknown389.dm.pageView.commentView.models.SendCommentResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface Api {
    @POST("/post/likeComment")
    suspend fun likeComment(
        @Body body: LikeCommentBody
    ): Response<LikeCommentResponse>

    @Multipart
    @PUT("/comments/sendComment/commentType={commentType}")
    suspend fun sendCommentFile(
        @Path("commentType") type:String,
        @Part("comment") comment: RequestBody,
        @Part("postId") postId: RequestBody,
        @Part file: MultipartBody.Part
    ):Response<SendCommentResponse>


    @POST("/comments/sendComment/commentType={commentType}")
    suspend fun sendCommentText(
        @Path("commentType") type:String = "text",
        @Body body: CommentBody
    ):Response<SendCommentResponse>
}