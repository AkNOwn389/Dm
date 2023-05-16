package com.aknown389.dm.utils

import android.content.Context
import android.net.ConnectivityManager
import okhttp3.Interceptor
import okhttp3.Response

class NetworkConnectionInterceptor: Interceptor {
    private lateinit var context:Context

    override fun intercept(chain: Interceptor.Chain): Response {
        context = context
        if(!isInternetAvailable())
            throw NoInternetException("No Internet")
        return chain.proceed(chain.request())
    }

    private fun isInternetAvailable(): Boolean{

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityManager.activeNetworkInfo.also {
            return it != null && it.isConnected
        }
    }
}