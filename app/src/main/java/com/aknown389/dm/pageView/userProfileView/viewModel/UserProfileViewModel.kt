package com.aknown389.dm.pageView.userProfileView.viewModel

import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.aknown389.dm.api.retroInstance.PostInstance
import com.aknown389.dm.api.retroInstance.ProfileInstance
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.models.homepostmodels.PostDataModel
import com.aknown389.dm.models.userviewModels.UserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(database:AppDataBase):ViewModel() {
    val _response:MutableLiveData<List<PostDataModel>> = MutableLiveData()
    val _userDataResponse:MutableLiveData<UserViewModel> = MutableLiveData()
    var page = 1
    var hasMorePage = true
    var isLoading = false

    fun getUserdata(token: String, username: String){
        viewModelScope.launch {
            val response = try {
                ProfileInstance.api.profileview(token, username)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            try {
                _userDataResponse.value = response.body()
            }catch (e:java.lang.Exception){
                e.printStackTrace()
                return@launch
            }
        }
    }
    fun loadPostList(token:String, username: String) {
        this.page = 1
        viewModelScope.launch {
            isLoading = true
            val response = try {
                PostInstance.api.postview(token, username, page)
            }catch (e:java.lang.Exception){
                e.printStackTrace()
                return@launch
            }
            if (response.isSuccessful){
                try {
                    _response.value = response.body()!!.data
                }catch (e:Exception){
                    e.printStackTrace()
                    return@launch
                }
            }

            isLoading = false
        }
    }
    fun updatePost(token:String, username:String) {
        this.page+=1
        viewModelScope.launch{
            isLoading = true
            val response = try {
                PostInstance.api.postview(token = token, user = username, page = page)
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }
            try {
                if (response.isSuccessful && response.body() != null){
                    hasMorePage = response.body()!!.hasMorePage
                }
                _response.value = response.body()!!.data
            }catch (e:Exception){
                e.printStackTrace()
                return@launch
            }

            isLoading = false
        }
    }
}