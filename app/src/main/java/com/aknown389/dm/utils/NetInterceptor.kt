package com.aknown389.dm.utils

import com.aknown389.dm.utils.Constants.Companion.API_VERSION
import com.aknown389.dm.utils.Constants.Companion.APP_BUILD
import com.aknown389.dm.utils.Constants.Companion.APP_VERSION
import com.aknown389.dm.utils.Constants.Companion.PHONE_NAME
import okhttp3.Interceptor
import okhttp3.Response

class NetInterceptor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("User-Agent", "DM/${APP_VERSION} (com.example; build:${APP_BUILD} Android R ${API_VERSION}) $PHONE_NAME")
            .addHeader("Connection", "Keep-Alive")
            .addHeader("Accept-Encoding", "gzip")
            .build()
        return chain.proceed(request)
    }
}