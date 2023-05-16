package com.aknown389.dm.utils.remote

import com.aknown389.dm.utils.dataClass.GetHostDataClass
import retrofit2.Call
import retrofit2.http.GET

interface Api {
    @GET("https://raw.githubusercontent.com/AkNOwn389/dmHostName/main/host.json")
    fun getHomeName(): Call<GetHostDataClass>
}