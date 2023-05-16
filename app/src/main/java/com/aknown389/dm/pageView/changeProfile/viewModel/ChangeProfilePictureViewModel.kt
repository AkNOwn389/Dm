package com.aknown389.dm.pageView.changeProfile.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aknown389.dm.models.postmodel.UploadPostResponseModel
import com.aknown389.dm.repository.Repository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class ChangeProfilePictureViewModel(private val repository: Repository):ViewModel() {
    val updateprofileresponse: MutableLiveData<Response<UploadPostResponseModel>> = MutableLiveData()
    fun updateProfilePicture(token: String, image: MultipartBody.Part, caption: RequestBody){
        viewModelScope.launch {
            try {
                val response: Response<UploadPostResponseModel> =
                    repository.updateProfilePicture(token, image, caption)
                updateprofileresponse.value = response
            } catch (e: java.lang.Exception) {
                return@launch
            }
        }
    }
}