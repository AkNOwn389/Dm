package com.aknown389.dm.pageView.notification.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.pageView.notification.models.NotificationDataClass
import com.aknown389.dm.pageView.notification.models.NotificationModel
import com.aknown389.dm.pageView.notification.remote.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(
    private val repository:Repository,
    private val database:AppDataBase,
):ViewModel() {
    val response:MutableLiveData<ArrayList<NotificationDataClass>> = MutableLiveData()
    var page = 1
    var hasMorePage = true
    var isLoading = false

    private suspend fun saveOnDb(value:List<NotificationDataClass>){
        database.notificationDao().insertNotification(value)
    }
    private suspend fun getOnDb():List<NotificationDataClass>{
        return database.notificationDao().getNotification()
    }
    private suspend fun deleteOnDb(){
        database.notificationDao().deleteAllNotification()
    }
    fun loadNotification(context: Context){
        page = 1
        hasMorePage = true
        viewModelScope.launch(Dispatchers.IO) {
            if (isLoading){return@launch}
            isLoading = true
            try {
                val res:Response<NotificationModel> = repository.getNotification(context, page)
                withContext(Dispatchers.Main){
                    response.value = res.body()!!.data as ArrayList<NotificationDataClass>
                }
                this@ViewModel.hasMorePage = res.body()!!.hasMorePage
                deleteOnDb()
                saveOnDb(res.body()!!.data)
                Log.d(TAG, "Saved ${res.body()!!.data.size} items to database")
            }catch (e:Exception){
                try {
                    val items = getOnDb() as ArrayList<NotificationDataClass>
                    Log.d(TAG, "get on db $items")
                    withContext(Dispatchers.Main){
                        response.value = items
                    }
                }catch (e:NullPointerException){
                    Log.d(TAG, "Error log ${e.printStackTrace()}")
                    Log.e(TAG, "Error log", e)
                    e.printStackTrace()
                }
            }
            isLoading = false
        }
    }
    fun updateNotification(context: Context){
        page +=1
        viewModelScope.launch {
            isLoading = true
            var isError = false
            do {
                try {
                    val res = repository.getNotification(context, page)
                    response.value = res.body()!!.data as ArrayList<NotificationDataClass>
                    this@ViewModel.hasMorePage = res.body()!!.hasMorePage
                    isError = false
                }catch (e:Exception){
                    isError = true
                }
            }while (isError)
            isLoading = false
        }
    }
    val TAG = "notificationviewmodel"
}