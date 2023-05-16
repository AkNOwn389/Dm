package com.aknown389.dm.pageView.postViewPage.remote.instance

import android.content.Context
import com.aknown389.dm.pageView.postViewPage.remote.api.Api
import com.aknown389.dm.utils.Constants
import com.aknown389.dm.utils.NetworkInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class Instance @Inject constructor(context: Context) {
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(NetworkInterceptor(context))
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
    val api: Api by lazy {
        retrofit.create(Api::class.java)
    }
}