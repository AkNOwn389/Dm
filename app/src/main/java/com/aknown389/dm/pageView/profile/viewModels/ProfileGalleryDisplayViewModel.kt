package com.aknown389.dm.pageView.profile.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.models.global.ImageUrl
import com.aknown389.dm.models.profileGalleryModels.MyGalleryResponseModel
import com.aknown389.dm.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

const val TAG = "ProfileActivityLog"
@HiltViewModel
class ProfileGalleryDisplayViewModel @Inject constructor(
    private val repository: Repository,
    private val token: String,
    private val db:AppDataBase
    ): ViewModel() {

    var hasMorePage = true
    var isLoading = false
    var page = 1
    val _mygalleryresponse: MutableLiveData<List<ImageUrl>> = MutableLiveData()

    private suspend fun saveOnDb(response: Response<MyGalleryResponseModel>){
        if (response.body()!!.data.isNotEmpty()){
            db.profileDao().insertImageToDb(response.body()!!.data)
            Log.d(TAG, "save data in database ${response.body()!!.data}")
        }
    }

    private suspend fun deleteAllImages(){
        db.profileDao().deleteAllImageGalery()
    }
    fun myGallery(){
        if (this.isLoading){
            return
        }
        this.isLoading = true
        page = 1
        importGalleryFromDb()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response: Response<MyGalleryResponseModel> = repository.myGallery(page, token)
                this@ProfileGalleryDisplayViewModel.hasMorePage = response.body()!!.hasMorePage
                withContext(Dispatchers.Main){
                    _mygalleryresponse.value = response.body()!!.data
                }
                if (response.body()!!.data.isNotEmpty()){
                    Log.d(TAG, "deleting old images")
                    deleteAllImages()
                    saveOnDb(response)
                    Log.d(TAG, "save images in db")
                }
            }catch (e:Exception){
                Log.d(TAG, e.stackTraceToString())
            }
            this@ProfileGalleryDisplayViewModel.isLoading = false
        }
    }
    private fun importGalleryFromDb(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = db.profileDao().getImageGallery()
                withContext(Dispatchers.Main){
                    _mygalleryresponse.value = response
                    Log.d(TAG, "get images in db : $response")
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
    fun updateGallery(){
        page += 1
        if (this.isLoading){
            return
        }
        viewModelScope.launch{
            this@ProfileGalleryDisplayViewModel.isLoading = true
            try {
                val response: Response<MyGalleryResponseModel> = repository.myGallery(page, token)
                this@ProfileGalleryDisplayViewModel.hasMorePage = response.body()!!.hasMorePage
                _mygalleryresponse.value = response.body()!!.data
            }catch (e:Exception){
                return@launch
            }
            this@ProfileGalleryDisplayViewModel.isLoading = false
        }
    }
}