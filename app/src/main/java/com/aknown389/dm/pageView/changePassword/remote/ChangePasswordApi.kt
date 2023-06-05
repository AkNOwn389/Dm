package com.aknown389.dm.pageView.changePassword.remote

import com.aknown389.dm.models.postmodel.NormalResponseModel
import com.aknown389.dm.pageView.changePassword.dataClass.ChangePasswordBody
import com.aknown389.dm.pageView.recoverAccountView.models.AccountRecoveryChangePasswordBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ChangePasswordApi {




    @POST("/user/change-password")
    suspend fun changePassword(
        @Body body: ChangePasswordBody
    ):Response<NormalResponseModel>
}