package com.aknown389.dm.api.retroInstance

import com.aknown389.dm.api.retrofitapi.PostApi
import com.aknown389.dm.utils.Constants
import com.aknown389.dm.utils.NetInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object PostInstance {
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(NetInterceptor())
        .connectTimeout(2, TimeUnit.HOURS)
        .readTimeout(2, TimeUnit.HOURS)
        .writeTimeout(2, TimeUnit.HOURS)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Constants.BASE_URL)
            .validateEagerly(true)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api: PostApi by lazy {
        retrofit.create(PostApi::class.java)
    }

    val retrofitBuilder: PostApi = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Constants.BASE_URL)
        .client(okHttpClient)
        .build()
        .create(PostApi::class.java)

}
