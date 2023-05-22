package com.aknown389.dm.pageView.switchAccount.remote.api

import com.aknown389.dm.pageView.switchAccount.models.ResponseDetails
import com.aknown389.dm.pageView.switchAccount.models.UserDetailBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface Api {
    @POST("/user/getUserDetail")
    suspend fun getUserDetail(
        @Body body:UserDetailBody
    ):Response<ResponseDetails>
}