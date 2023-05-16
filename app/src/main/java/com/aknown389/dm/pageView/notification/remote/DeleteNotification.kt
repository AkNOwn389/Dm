package com.aknown389.dm.pageView.notification.remote

import android.content.Context
import android.widget.Toast
import com.aknown389.dm.pageView.notification.models.NotificationDataClass
import com.aknown389.dm.pageView.notification.utilities.NotificationAdapter
import com.aknown389.dm.pageView.notification.remote.instance.Instance
import com.aknown389.dm.pageView.notification.utilities.NotificationsViewHolder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeleteNotification @Inject constructor(
    val data: NotificationDataClass,
    val context: Context,
    val holder: NotificationsViewHolder,
    val notifications:ArrayList<NotificationDataClass>,
    val adapter: NotificationAdapter,
) {
    init {
        deleteNotification()
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun deleteNotification(){
        GlobalScope.launch(Dispatchers.Main) {
            if (isActive){
                val response = try {
                    Instance(context).api.deleteNotification(id = data.id)
                }catch (e:Exception){
                    e.printStackTrace()
                    return@launch
                }
                if (response.isSuccessful && response.body()!!.status){
                    val items = notifications
                    val pos = items.indexOf(data)
                    items.removeAt(pos)
                    adapter.setData(items)
                }else{
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }else{
                return@launch
            }
        }
    }
}