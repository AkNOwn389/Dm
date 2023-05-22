package com.aknown389.dm.pageView.switchAccount.remote.repository

import android.content.Context
import com.aknown389.dm.pageView.switchAccount.models.ResponseDetails
import com.aknown389.dm.pageView.switchAccount.models.UserDetailBody
import com.aknown389.dm.pageView.switchAccount.remote.instance.SwitchAccountInstance
import retrofit2.Response

class SwitchAccountRepository {
    suspend fun getUserSwitchAccountDetail(body: UserDetailBody, context: Context):Response<ResponseDetails>{
        return SwitchAccountInstance(context).api.getUserDetail(body)
    }

}