package com.aknown389.dm.api.retroInstance

import com.aknown389.dm.api.retrofitapi.AppApi
import com.aknown389.dm.utils.Constants.Companion.BASE_URL
import com.aknown389.dm.utils.NetInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitInstance {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(NetInterceptor())
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

//for Call
    val retrofitBuilder = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()
        .create(AppApi::class.java)

//for Response

    private val retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: AppApi by lazy {
        retrofit.create(AppApi::class.java)
    }
}