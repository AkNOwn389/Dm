package com.aknown389.dm.api.retroInstance

import android.annotation.SuppressLint
import android.content.Context
import com.aknown389.dm.api.retrofitapi.CommentApi
import com.aknown389.dm.utils.Constants
import com.aknown389.dm.utils.NetworkInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
class CommentInstance @Inject constructor(private val context: Context) {
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
    val api: CommentApi by lazy {
        retrofit.create(CommentApi::class.java)
    }
}