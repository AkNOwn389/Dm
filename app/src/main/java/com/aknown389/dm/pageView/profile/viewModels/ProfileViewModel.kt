package com.aknown389.dm.pageView.profile.viewModels

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aknown389.dm.api.retroInstance.PostInstance
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.models.homepostmodels.FeedResponseModelV2
import com.aknown389.dm.models.profileModel.MeModel
import com.aknown389.dm.db.local.UserProfileData
import com.aknown389.dm.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private var db:AppDataBase,
    private val token:String,
    private val repository: Repository
):ViewModel() {
    val TAG = "ProfileActivity"
    val myDetailsResponse:MutableLiveData<UserProfileData?> = MutableLiveData()
    val _response:MutableLiveData<FeedResponseModelV2> = MutableLiveData()
    var isLoading = false
    var hasMorePage = true
    var page = 1

    fun updateActivity() {
        this.isLoading = true
        this.page +=1
        viewModelScope.launch {
            val res = try {
                PostInstance.api.getPostList(token, page)
            }catch (e: java.lang.Exception){
                e.printStackTrace()
                return@launch
            }
            try {
                hasMorePage = res.body()!!.hasMorePage
                _response.value = res.body()
            }catch (e:Exception){
                Log.d(TAG, e.stackTraceToString().toString())
            }
            isLoading = false
        }
    }
    fun loadPostList() {
        this.isLoading = true
        this.page = 1
        this.hasMorePage = true
        viewModelScope.launch {
            val res = try {
                PostInstance.api.getPostList(token, page)
            }catch (e: java.lang.Exception){
                e.printStackTrace()
                return@launch
            }
            try {
                hasMorePage = res.body()!!.hasMorePage
                _response.value = res.body()
            }catch (e:Exception){
                Log.d(TAG, e.stackTraceToString())
            }
            isLoading = false
        }
    }

    suspend fun saveMydetails(response: Response<MeModel>){
        db.profileDao().saveUserProfileDetails(response.body()!!.data)
        Log.d(TAG, "Save profile data: ${response.body()!!.data}")
    }
    suspend fun deleteProfileData(){
        try {
            db.profileDao().deleteProfileData()
        }catch (e:Exception){
            e.printStackTrace()
        }
        Log.d(TAG, "Profile details deleted")
    }

    private suspend fun getProfileDetails(): UserProfileData?{
        return try {
            db.profileDao().getMyProfileDetail()
        }catch (e: SQLiteException){
            Log.d(TAG, e.stackTraceToString().toString())
            null
        }catch (e:IllegalStateException){
            Log.d(TAG, e.stackTraceToString().toString())
            null
        }
    }

    private fun importProfileDataInDb(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cache = getProfileDetails()
                withContext(Dispatchers.Main){
                    if (cache != null){
                        myDetailsResponse.value = cache
                        Log.d(TAG, "cache load")
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }



    fun me() {
        importProfileDataInDb()
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
                return@launch
            }
            this@ProfileViewModel.isLoading = false
        }
    }
}