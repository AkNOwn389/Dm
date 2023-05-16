package com.aknown389.dm.pageView.notification.remote.repository

import android.content.Context
import com.aknown389.dm.pageView.notification.models.NotificationModel
import com.aknown389.dm.pageView.notification.remote.instance.Instance
import retrofit2.Response

class Repository {
    suspend fun getNotification(context: Context, page:Int): Response<NotificationModel> {
        return Instance(context).api.notifications(page)
    }
}