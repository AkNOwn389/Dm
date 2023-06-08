package com.aknown389.dm.pageView.videoGallery.remote

import com.aknown389.dm.pageView.videoGallery.dataClass.VideoGalleryDataClassResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface VideoGalleryApi {
    @GET("/post/myvideos/page={page}")
    suspend fun myVideoGallery(
        @Path("page") page:Int,
    ):Response<VideoGalleryDataClassResponse>
}