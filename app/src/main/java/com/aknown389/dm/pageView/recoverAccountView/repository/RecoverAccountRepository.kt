package com.aknown389.dm.pageView.recoverAccountView.repository

import android.content.Context
import com.aknown389.dm.models.postmodel.NormalResponseModel
import com.aknown389.dm.pageView.recoverAccountView.models.AccountRecoveryChangePasswordBody
import com.aknown389.dm.pageView.recoverAccountView.models.RecoveryAccountVerifyOtp
import com.aknown389.dm.pageView.recoverAccountView.models.RecoveryAccountVerifyOtpResponse
import com.aknown389.dm.pageView.recoverAccountView.models.RequestAccountRecovery
import com.aknown389.dm.pageView.recoverAccountView.models.RequestAccountRecoveryResponse
import com.aknown389.dm.pageView.recoverAccountView.remote.RecoverAccountInstance
import retrofit2.Response

class RecoverAccountRepository {

    suspend fun requestAccountRecovery(body:RequestAccountRecovery): Response<RequestAccountRecoveryResponse>{
        return RecoverAccountInstance.api.requestAccountRecovery(body)
    }

    suspend fun requestAccountVerifyOtp(body: RecoveryAccountVerifyOtp): Response<RecoveryAccountVerifyOtpResponse>{
        return RecoverAccountInstance.api.recoveryAccountVerifyCode(body)
    }

    suspend fun recoveryAccountChangePassword(body: AccountRecoveryChangePasswordBody): Response<NormalResponseModel>{
        return RecoverAccountInstance.api.recoveryAccountChangePassword(body)
    }
}