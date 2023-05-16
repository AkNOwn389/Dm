package com.aknown389.dm.api.retroInstance

import com.aknown389.dm.api.retrofitapi.VideoFeedApi
import com.aknown389.dm.utils.Constants
import com.aknown389.dm.utils.NetInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object VideoFeedInstance {
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(NetInterceptor())
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api: VideoFeedApi by lazy {
        retrofit.create(VideoFeedApi::class.java)
    }
}