package com.aknown389.dm.pageView.notification.remote

import android.content.Context
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.aknown389.dm.R
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.pageView.notification.remote.instance.Instance
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
class ReportSeen @Inject constructor(
    val id:Int,
    val context: Context,
    private val constraintLayout: ConstraintLayout

) {
    private var dataBase: AppDataBase = AppDataBase.getDatabase(context)

    init {
        GlobalScope.launch(Dispatchers.Main) {
            val response = try {
                Instance(context).api.bahaviorSeenNotification(id)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful){
                if (response.body()!!.status) {
                    withContext(Dispatchers.IO){
                        val result = dataBase.notificationDao().reportSeen(id)
                        if (!result){
                            Toast.makeText(context, "Error saving in database", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    constraintLayout.setBackgroundResource(R.drawable.chat_item_ripple)
                }
                if (response.body()!!.message == "Already seen"){
                    Toast.makeText(context, "Already seen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}