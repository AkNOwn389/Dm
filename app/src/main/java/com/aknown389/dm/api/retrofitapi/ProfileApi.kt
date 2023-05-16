package com.aknown389.dm.api.retrofitapi

import com.aknown389.dm.pageView.mainSearch.dataClass.SearchResponse
import com.aknown389.dm.models.searchActivityModels.searchItemsModels
import com.aknown389.dm.models.userviewModels.UserViewModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ProfileApi {
    @GET("/profile/user/{user}")
    suspend fun profileview(
        @Header("Authorization") token: String,
        @Path("user") user:String
    ): Response<UserViewModel>
    @GET("/profile/search/{user}/page={page}")
    suspend fun searchuser(
        @Header("Authorization") token:String,
        @Path("user") user: String,
        @Path("page") page:Int
    ):Response<searchItemsModels>
    @GET("/profile/mainsearch/type={type}/search={user}/page={page}")
    suspend fun mainSearch(
        @Header("Authorization") token:String,
        @Path("user") user: String,
        @Path("type") type:String,
        @Path("page") page:Int
    ):Response<SearchResponse>
}