package com.aknown389.dm.pageView.mainSearch.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aknown389.dm.pageView.mainSearch.dataClass.MainSearchItemData
import com.aknown389.dm.pageView.mainSearch.remote.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel():ViewModel() {
    private var repository = Repository()
    val responseData:MutableLiveData<ArrayList<MainSearchItemData>> = MutableLiveData()
    val responseUpdate:MutableLiveData<ArrayList<MainSearchItemData>> = MutableLiveData()
    val recentData:MutableLiveData<ArrayList<MainSearchItemData>> = MutableLiveData()
    var hasMorePage = true
    var isLoading = false
    var page = 1

    fun search(context: Context, text:String, type:String){
        page = 1
        viewModelScope.launch {
            isLoading = true
           try {
               val response = repository.mainSearch(context = context, user = text, type = type, page = page)
               responseData.value = response.body()!!.data as ArrayList<MainSearchItemData>
               isLoading = false
           }catch (e:Exception){
               isLoading = false
               return@launch
           }
        }
    }

    fun updateSearch(context: Context, text: String, searchtype:String){
        page += 1
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.mainSearch(context, user = text, type = searchtype, page = page)
                responseUpdate.value = response.body()!!.data as ArrayList<MainSearchItemData>
                this@SearchViewModel.hasMorePage = response.body()!!.hasMorePage
                isLoading = false
            }catch (e:Exception){
                isLoading = false
                return@launch
            }
        }
    }
    fun loadRecent(context: Context) {
        viewModelScope.launch {
            try {
                val response = repository.loadSearchRecent(context)
                val items = response.body()!!.data as ArrayList<MainSearchItemData>
                val top = MainSearchItemData(searchType = 99)
                items.add(0, top)
                recentData.value = items
            } catch (e: Exception) {
                return@launch
            }
        }
    }
}