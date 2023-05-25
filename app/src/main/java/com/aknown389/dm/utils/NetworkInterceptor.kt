package com.aknown389.dm.utils

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class NetworkInterceptor(private val context: Context):Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val manager = DataManager(context = context)
        val request = chain.request()
            .newBuilder()
            .addHeader("User-Agent", "DM/${Constants.APP_VERSION} (com.example; build:${Constants.APP_BUILD} Android R ${Constants.API_VERSION}) ${Constants.PHONE_NAME}")
            .addHeader("Connection", "Keep-Alive")
            .addHeader("Authorization", manager.getAccessToken().toString())
            .addHeader("Accept-Encoding", "gzip")
            .build()
        return chain.proceed(request)
    }
}