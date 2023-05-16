package com.aknown389.dm.pageView.homeFeed.remote.api

import com.aknown389.dm.models.homepostmodels.FeedResponseModelV2
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {
    @GET("/feed/newsfeed/page={page}")
    suspend fun newsfeed(
        @Path("page") page: Int
    ): Response<FeedResponseModelV2>
}