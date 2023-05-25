package com.aknown389.dm.pageView.switchAccount.remote.instance

import com.aknown389.dm.pageView.switchAccount.remote.api.Api
import com.aknown389.dm.utils.Constants
import com.aknown389.dm.utils.NetInterceptor
import com.aknown389.dm.utils.NetworkInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object SwitchAccountInstance2 {
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
    val api: Api by lazy {
        retrofit.create(Api::class.java)
    }
}