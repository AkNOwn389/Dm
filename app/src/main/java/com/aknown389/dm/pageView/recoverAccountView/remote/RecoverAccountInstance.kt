package com.aknown389.dm.pageView.recoverAccountView.remote

import android.content.Context
import com.aknown389.dm.pageView.newsView.remote.api.NewsApi
import com.aknown389.dm.utils.Constants
import com.aknown389.dm.utils.NetInterceptor
import com.aknown389.dm.utils.NetworkInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

object RecoverAccountInstance {
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
    val api: RecoverAccountApi by lazy {
        retrofit.create(RecoverAccountApi::class.java)
    }
}