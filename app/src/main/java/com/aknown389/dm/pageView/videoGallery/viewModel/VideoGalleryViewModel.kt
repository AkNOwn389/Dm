package com.aknown389.dm.pageView.videoGallery.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.pageView.videoGallery.dataClass.VideoGalleryDataClassResponse
import com.aknown389.dm.pageView.videoGallery.repository.VideoGalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoGalleryViewModel @Inject constructor(private val dataBase: AppDataBase): ViewModel() {
    private val repository = VideoGalleryRepository()
    var isLoading = false
    var page = 1
    var hasMorePage = true
    val _response:MutableLiveData<VideoGalleryDataClassResponse?> = MutableLiveData()


    fun getVideos(context: Context){
        hasMorePage = true
        isLoading = true
        page = 1
        viewModelScope.launch {
            val response = try {
                repository.myVideoGallery(context, page)
            }catch (e:Exception){
                _response.value = null
                return@launch
            }
            hasMorePage = response.body()!!.hasMorePage
            _response.value = response.body()
            isLoading = false
        }
    }
    fun updateVideos(context: Context){
        isLoading = true
        page += 1
        viewModelScope.launch {
            val response = try {
                repository.myVideoGallery(context, page)
            }catch (e:Exception){
                _response.value = null
                return@launch
            }
            hasMorePage = response.body()!!.hasMorePage
            _response.value = response.body()
            isLoading = false
        }
    }
}