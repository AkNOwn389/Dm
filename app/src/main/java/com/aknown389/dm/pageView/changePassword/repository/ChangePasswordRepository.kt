package com.aknown389.dm.pageView.changePassword.repository

import android.content.Context
import com.aknown389.dm.models.postmodel.NormalResponseModel
import com.aknown389.dm.pageView.changePassword.dataClass.ChangePasswordBody
import com.aknown389.dm.pageView.changePassword.remote.ChangePasswordInstance
import retrofit2.Response

class ChangePasswordRepository {




    suspend fun changePassword(context:Context, body: ChangePasswordBody):Response<NormalResponseModel>{
        return ChangePasswordInstance(context).api.changePassword(body)
    }
}