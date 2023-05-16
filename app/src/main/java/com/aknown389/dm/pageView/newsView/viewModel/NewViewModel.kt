package com.aknown389.dm.pageView.newsView.viewModel

import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.db.local.NewsDataEntities
import com.aknown389.dm.models.news.NewsArticlesModels
import com.aknown389.dm.pageView.newsView.remote.instance.NewsViewInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class NewViewModel @Inject constructor(
    private val dao:AppDataBase
    ):ViewModel(){
    val newsResponse:MutableLiveData<ArrayList<NewsDataEntities>> = MutableLiveData()
    private var page = 1
    var isLoading = false
    var hasMorePage = false
    fun getNews(context:Context){
        if (this@NewViewModel.isLoading){
            return
        }
        page = 1
        viewModelScope.launch(Dispatchers.IO) {
            this@NewViewModel.isLoading = true

            try {
                val response:Response<NewsArticlesModels> = NewsViewInstance(context = context.applicationContext).api.getNews(page = page)
                withContext(Dispatchers.Main){
                    newsResponse.value = response.body()!!.data as ArrayList<NewsDataEntities>
                }
                dao.newDao().deleteAllNews()
                dao.newDao().insertNews(response.body()!!.data)
                this@NewViewModel.hasMorePage = response.body()!!.hasMorePage!!
                this@NewViewModel.isLoading = false
            }catch (e:Exception){
                try{
                    val items:List<NewsDataEntities> = dao.newDao().getAllNewsData()
                    withContext(Dispatchers.Main){
                        newsResponse.value = items as ArrayList<NewsDataEntities>
                    }
                }catch (e: SQLiteException){
                    e.printStackTrace()
                }
                this@NewViewModel.isLoading = false
            }
        }
    }
    fun updateNews(context: Context){
        if (this@NewViewModel.isLoading){
            return
        }
        page +=1
        viewModelScope.launch(Dispatchers.IO) {
            this@NewViewModel.isLoading = true
            try{
                val response:Response<NewsArticlesModels> = NewsViewInstance(context = context.applicationContext).api.getNews(page = page)
                withContext(Dispatchers.Main){
                    newsResponse.value = response.body()!!.data as ArrayList<NewsDataEntities>
                }
                this@NewViewModel.hasMorePage = response.body()!!.hasMorePage!!
                dao.newDao().deleteAllNews()
                dao.newDao().insertNews(response.body()!!.data)
                this@NewViewModel.hasMorePage = response.body()!!.hasMorePage!!
                this@NewViewModel.isLoading = false
            }catch (e:Exception){
                try{
                    val items:List<NewsDataEntities> = dao.newDao().getAllNewsData()
                    withContext(Dispatchers.Main){
                        newsResponse.value = items as ArrayList<NewsDataEntities>
                    }
                }catch (e: SQLiteException){
                    e.printStackTrace()
                }
                this@NewViewModel.isLoading = false
            }
        }
    }
}