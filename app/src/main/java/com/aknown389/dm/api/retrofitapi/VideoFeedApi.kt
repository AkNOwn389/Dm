package com.aknown389.dm.api.retrofitapi

import com.aknown389.dm.models.videoFeedModels.VideoFeedResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface VideoFeedApi {
    @GET("/feed/videofeed/page={page}")
    suspend fun videoFeed(
        @Header("Authorization") token:String,
        @Path("page") page:Int
    ):Response<VideoFeedResponseModel>
}