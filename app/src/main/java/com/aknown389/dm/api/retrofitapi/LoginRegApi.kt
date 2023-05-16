package com.aknown389.dm.api.retrofitapi

import com.aknown389.dm.models.loginRegModels.RegRequestCodeResponse
import com.aknown389.dm.models.loginRegModels.LoginModel
import com.aknown389.dm.models.loginRegModels.LoginResponseModelV2
import com.aknown389.dm.models.loginRegModels.RegReqCodeModel
import com.aknown389.dm.models.loginRegModels.RegisterModel
import com.aknown389.dm.models.loginRegModels.RegisterResModelV2
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginRegApi {

    @POST("/user/login")
    suspend fun loginR(
        @Body loginBody: LoginModel
    ): Response<LoginResponseModelV2>

    @POST("/user/signup")
    suspend fun register(
        @Body registerBody: RegisterModel
    ): Response<RegisterResModelV2>

    @POST("/user/signup/getCode")
    suspend fun getRegCode(
        @Body body: RegReqCodeModel
    ): Response<RegRequestCodeResponse>
}