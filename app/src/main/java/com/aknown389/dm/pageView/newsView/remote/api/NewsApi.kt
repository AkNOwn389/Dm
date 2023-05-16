package com.aknown389.dm.pageView.newsView.remote.api

import com.aknown389.dm.models.news.NewsArticlesModels
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface NewsApi {
    @GET("/news/news/page={page}")
    suspend fun getNews(
        @Path("page") page:Int
    ):Response<NewsArticlesModels>
}