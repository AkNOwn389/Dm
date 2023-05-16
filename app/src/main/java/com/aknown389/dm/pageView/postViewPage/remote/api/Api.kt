package com.aknown389.dm.pageView.postViewPage.remote.api

import com.aknown389.dm.pageView.postViewPage.Models.NetworkResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {
    @GET("/post/getpost/id={id}")
    suspend fun getPostData(
        @Path("id") id:String
    ): Response<NetworkResponse>
}