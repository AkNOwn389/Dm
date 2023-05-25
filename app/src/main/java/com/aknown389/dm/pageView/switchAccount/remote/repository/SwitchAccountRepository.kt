package com.aknown389.dm.pageView.switchAccount.remote.repository

import android.content.Context
import com.aknown389.dm.pageView.switchAccount.models.IsAuthenticatedResponse
import com.aknown389.dm.pageView.switchAccount.models.RefreshTokenBody
import com.aknown389.dm.pageView.switchAccount.models.RefreshTokenResponse
import com.aknown389.dm.pageView.switchAccount.models.ResponseDetails
import com.aknown389.dm.pageView.switchAccount.models.UserDetailBody
import com.aknown389.dm.pageView.switchAccount.remote.instance.SwitchAccountInstance
import com.aknown389.dm.pageView.switchAccount.remote.instance.SwitchAccountInstance2
import retrofit2.Response

class SwitchAccountRepository {
    suspend fun getUserSwitchAccountDetail(body: UserDetailBody, context: Context):Response<ResponseDetails>{
        return SwitchAccountInstance(context).api.getUserDetail(body)
    }

    suspend fun isAuthenticatedForOtherUser(token:String):Response<IsAuthenticatedResponse>{
        return SwitchAccountInstance2.api.isAuthenticatedForOtherUser(token)
    }

    suspend fun isAuthenticated(context: Context):Response<IsAuthenticatedResponse>{
        return SwitchAccountInstance(context).api.isAuthenticated()
    }

    suspend fun refreshToken(token: String, body: RefreshTokenBody):Response<RefreshTokenResponse>{
        return SwitchAccountInstance2.api.refreshToken(token, body)
    }
}