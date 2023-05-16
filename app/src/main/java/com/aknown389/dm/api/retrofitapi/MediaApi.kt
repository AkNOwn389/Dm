package com.aknown389.dm.api.retrofitapi

import com.aknown389.dm.utils.Constants.Companion.BASE_URL
import com.aknown389.dm.utils.NetworkConnectionInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface MediaApi {

    companion object {
        operator fun invoke(networkConnectionInterceptor: NetworkConnectionInterceptor
        ) : AppApi {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(networkConnectionInterceptor)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AppApi::class.java)
        }
    }
}