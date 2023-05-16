package com.aknown389.dm.pageView.profile.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.models.profileModel.MeModel
import com.aknown389.dm.models.profileModel.UserProfileData
import com.aknown389.dm.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.lang.NullPointerException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private var db:AppDataBase,
    private val token:String,
    private val repository: Repository
):ViewModel() {
    val TAG = "ProfileActivity"
    val myDetailsResponse:MutableLiveData<UserProfileData> = MutableLiveData()
    var isLoading = false
    var hasMorePage = true
    var page = 1

    suspend fun saveMydetails(response: Response<MeModel>){
        db.profileDao().saveUserProfileDetails(response.body()!!.data)
        Log.d(TAG, "Save profile data: ${response.body()!!.data}")
    }
    suspend fun deleteProfileData(){
        db.profileDao().deleteProfileData()
        Log.d(TAG, "Profile details deleted")
    }

    fun getProfileDetails():UserProfileData{
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
                try {
                    val cache = getProfileDetails()
                    withContext(Dispatchers.Main){
                        myDetailsResponse.value = cache
                        Log.d(TAG, "cache load")
                    }
                }catch (e:NullPointerException){
                    e.printStackTrace()
                }
            }
            this@ProfileViewModel.isLoading = false
        }
    }
}