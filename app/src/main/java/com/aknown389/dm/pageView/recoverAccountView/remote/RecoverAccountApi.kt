package com.aknown389.dm.pageView.recoverAccountView.remote

import com.aknown389.dm.models.postmodel.NormalResponseModel
import com.aknown389.dm.pageView.recoverAccountView.models.AccountRecoveryChangePasswordBody
import com.aknown389.dm.pageView.recoverAccountView.models.RecoveryAccountVerifyOtp
import com.aknown389.dm.pageView.recoverAccountView.models.RecoveryAccountVerifyOtpResponse
import com.aknown389.dm.pageView.recoverAccountView.models.RequestAccountRecovery
import com.aknown389.dm.pageView.recoverAccountView.models.RequestAccountRecoveryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface RecoverAccountApi {

    @POST("/user/recovery-requests-otp")
    suspend fun requestAccountRecovery(
        @Body body: RequestAccountRecovery
    ):Response<RequestAccountRecoveryResponse>

    @POST("/user/recoverAccount")
    suspend fun recoveryAccountVerifyCode(
        @Body body: RecoveryAccountVerifyOtp
    ):Response<RecoveryAccountVerifyOtpResponse>

    @PUT("/user/recoverAccount")
    suspend fun recoveryAccountChangePassword(
        @Body body: AccountRecoveryChangePasswordBody
    ):Response<NormalResponseModel>

}