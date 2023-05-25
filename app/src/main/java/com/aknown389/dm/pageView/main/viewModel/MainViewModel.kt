package com.aknown389.dm.pageView.main.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.models.global.LoginBahavior
import com.aknown389.dm.models.loginRegModels.LogoutResponseModel
import com.aknown389.dm.models.logoutmodel.LogOutBodyModel
import com.aknown389.dm.models.postmodel.UploadPostResponseModel
import com.aknown389.dm.models.profileModel.MeModel
import com.aknown389.dm.models.profileModel.RequestAvatarResponse
import com.aknown389.dm.models.profileModel.UpdateDetailsBodyModel
import com.aknown389.dm.models.profileModel.UserProfileData
import com.aknown389.dm.models.showFriendModels.UserCardViewResponseModel
import com.aknown389.dm.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val db: AppDataBase,
    private val repository: Repository,
    private val token: String
    ): ViewModel() {
    val TAG = "MainViewModel"
    val requestAvatarresponse:MutableLiveData<Response<RequestAvatarResponse>> = MutableLiveData()
    val logoutresponse:MutableLiveData<Response<LogoutResponseModel>> = MutableLiveData()
    val updatedetailsresponse:MutableLiveData<Response<UploadPostResponseModel>> = MutableLiveData()
    val getfriendresponse:MutableLiveData<UserCardViewResponseModel> = MutableLiveData()
    val myDetailsResponse:MutableLiveData<UserProfileData> = MutableLiveData()
    val loginBahaviorResponse:MutableLiveData<LoginBahavior> = MutableLiveData()
    var isLoading = false
    var hasMorePage = true
    var page = 1
    fun bahavior(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response:Response<LoginBahavior> = repository.behavior(token)
                withContext(Dispatchers.Main){
                    loginBahaviorResponse.value = response.body()!!
                }
            }catch (e:Exception){
                val errorResponse = LoginBahavior(
                    status_code = 200,
                    message = "error network",
                    id = 0,
                    status = true,
                    username = "error network"
                )
                withContext(Dispatchers.Main){
                    loginBahaviorResponse.value = errorResponse
                }
            }
        }
    }
    fun getAvatar(username:String){
        viewModelScope.launch {
            var isError: Boolean = false
            do {
                try {
                    val response: Response<RequestAvatarResponse> =
                        repository.getAvatar(token, username)
                    requestAvatarresponse.value = response
                    isError = false
                } catch (e: Exception) {
                    isError = true
                }
            }while (isError)
        }
    }
    fun logout(body: LogOutBodyModel) {
        viewModelScope.launch {
            var isError:Boolean = false
            do {
                try {
                    val response: Response<LogoutResponseModel> = repository.logout(token, body)
                    logoutresponse.value = response
                    isError = false
                } catch (e: Exception) {
                    isError = true
                }
            } while (isError)
        }
    }
    suspend fun saveMydetails(response: Response<MeModel>){
        db.profileDao().saveUserProfileDetails(response.body()!!.data)
        Log.d(TAG, "Save profile data: ${response.body()!!.data}")
    }
    suspend fun deleteProfileData(){
        db.profileDao().deleteProfileData()
        Log.d(TAG, "Profile details deleted")
    }

    private suspend fun getProfileDetails(): UserProfileData {
        return db.profileDao().getMyProfileDetail()
    }
    fun me() {
        this.isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response: Response<MeModel> = repository.me(token)
                withContext(Dispatchers.Main){
                    myDetailsResponse.value = response.body()?.data
                }
                if (response.isSuccessful){
                    if (response.body() != null){
                        deleteProfileData()
                        saveMydetails(response)
                    }
                }
            }catch (e:java.lang.Exception){
                val cache = getProfileDetails()
                withContext(Dispatchers.Main){
                    myDetailsResponse.value = cache
                    Log.d(TAG, "cache load")
                }
            }
            this@MainViewModel.isLoading = false
        }
    }

    fun updateDetails(body: UpdateDetailsBodyModel){
        viewModelScope.launch {
            try {
                val response:Response<UploadPostResponseModel> = repository.updateDetails(token, body)
                updatedetailsresponse.value = response
            }catch (e:java.lang.Exception){
                return@launch
            }
        }
    }
    fun getFriends(page: Int){
        this.isLoading = true
        viewModelScope.launch {
            try {
                val response:Response<UserCardViewResponseModel> = repository.getFriends(token, page)
                this@MainViewModel.hasMorePage = response.body()!!.data.size == 16
                getfriendresponse.value = response.body()
            }catch (e: java.lang.Exception){
                return@launch
            }
            this@MainViewModel.isLoading = false
        }
    }
}