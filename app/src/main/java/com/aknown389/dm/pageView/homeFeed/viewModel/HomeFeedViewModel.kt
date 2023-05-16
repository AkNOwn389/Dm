package com.aknown389.dm.pageView.homeFeed.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.db.local.HomeFeedDataEntity
import com.aknown389.dm.db.mappers.toHomeFeedDataEntity
import com.aknown389.dm.models.homepostmodels.FeedResponseModelV2
import com.aknown389.dm.models.homepostmodels.PostDataModel
import com.aknown389.dm.pageView.homeFeed.remote.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.sql.SQLException
import java.util.ArrayList
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class HomeFeedViewModel @Inject constructor(
    private val repository: Repository,
    private val dataBase: AppDataBase
):ViewModel()
{
    var newsfeed:MutableLiveData<ArrayList<PostDataModel>> = MutableLiveData()
    private var page = 1
    var isLoading = false
    var hasMorePage = true

    private suspend fun saveInDataBase(response:Response<FeedResponseModelV2>){
        if (response.body()!!.data.isNotEmpty()){
            dataBase.homeFeedDao().deleteAllFeed()
            val items = response.body()!!.data.map { it.toHomeFeedDataEntity() }
            dataBase.homeFeedDao().insertFeed(items)
        }
    }
    fun getFeed(context:Context){
        this.isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            this@HomeFeedViewModel.page = 1
            try {
                //return data base item first
                val response:Response<FeedResponseModelV2> = repository.newFeed(page = page, context = context.applicationContext)
                withContext(Dispatchers.Main){
                    val items = response.body()!!.data as ArrayList<PostDataModel>
                    newsfeed.value = items
                }
                this@HomeFeedViewModel.hasMorePage = response.body()!!.hasMorePage
                if (response.body()!!.status){
                    saveInDataBase(response)
                }
            }catch (e:Exception){
                Log.d(TAG, e.stackTraceToString())
                try {
                    val response = dataBase.homeFeedDao().getFeed()
                    withContext(Dispatchers.Main) {
                        val items = response as ArrayList<PostDataModel>
                        newsfeed.value = items
                    }
                }catch (e:SQLException){
                    e.printStackTrace()
                    return@launch
                }
            }
            this@HomeFeedViewModel.isLoading = false
        }
    }

    fun updateFeed(context: Context){
        this.isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            this@HomeFeedViewModel.page+=1
            var isError = false
            do {
            try {
                val response:Response<FeedResponseModelV2> = repository.newFeed(page = page, context = context.applicationContext)
                withContext(Dispatchers.Main){
                    newsfeed.value = response.body()!!.data as ArrayList<PostDataModel>
                }
                this@HomeFeedViewModel.hasMorePage = response.body()!!.hasMorePage
                if (response.body()!!.status){
                    val item = ArrayList<HomeFeedDataEntity>()
                    for (i in response.body()!!.data){
                        item.add(i.toHomeFeedDataEntity())
                    }
                    dataBase.homeFeedDao().insertFeed(item)
                }
                isError = false
            }catch (e:Exception){
                isError = true
            }
            }while (isError)
            this@HomeFeedViewModel.isLoading = false
        }
    }
    companion object{
        val TAG = "HomeViewModel"
    }
}