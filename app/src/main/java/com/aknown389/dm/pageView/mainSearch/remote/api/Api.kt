package com.aknown389.dm.pageView.mainSearch.remote.api

import com.aknown389.dm.pageView.mainSearch.dataClass.SearchResponse
import com.aknown389.dm.models.postmodel.NormalResponseModel
import com.aknown389.dm.models.searchActivityModels.searchItemsModels
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {
    @GET("/profile/search/{user}/page={page}")
    suspend fun searchuser(
        @Path("user") user: String,
        @Path("page") page:Int
    ): Response<searchItemsModels>
    @GET("/profile/mainsearch/type={type}/search={user}/page={page}")
    suspend fun mainSearch(
        @Path("user") user: String,
        @Path("type") type:String,
        @Path("page") page:Int
    ): Response<SearchResponse>

    @GET("/profile/save-recent/{user}")
    suspend fun saveRecent(
        @Path("user") user: String,
    ): Response<NormalResponseModel>

    @GET("profile/search/recent")
    suspend fun loadSearchRecent(
    ): Response<SearchResponse>
}