package com.aknown389.dm.pageView.createPost.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aknown389.dm.models.postmodel.UploadPostResponseModel
import com.aknown389.dm.models.postmodel.UploadTextPostBody
import com.aknown389.dm.models.profileModel.RequestAvatarResponse
import com.aknown389.dm.repository.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class CreatePostViewModel(private val repository: Repository): ViewModel() {
    val requestAvatarresponse:MutableLiveData<Response<RequestAvatarResponse>> = MutableLiveData()
    val uploadPostresponse: MutableLiveData<Response<UploadPostResponseModel>> = MutableLiveData()
    val uploadTextresponse: MutableLiveData<Response<UploadPostResponseModel>> = MutableLiveData()

    fun getAvatar(token: String, username:String){
        viewModelScope.launch {
            var isError: Boolean = false
            do {
                if (isActive) {
                    try {
                        val response: Response<RequestAvatarResponse> =
                            repository.getAvatar(token, username)
                        requestAvatarresponse.value = response
                        isError = false
                    } catch (e: Exception) {
                        e.printStackTrace()
                        isError = true
                        delay(3000)
                    }
                }else{
                    return@launch
                }
                delay(3000)
            }while (isError)
        }
    }
    fun uploadImage(token: String, file: List<MultipartBody.Part>, caption: RequestBody, privacy: RequestBody){
        viewModelScope.launch {
            try {
                val response: Response<UploadPostResponseModel> =
                    repository.uploadImage(token=token, file=file, caption=caption, privacy=privacy)
                uploadPostresponse.value = response
            } catch (e: Exception) {
                e.stackTrace
                return@launch
            }
        }
    }

    fun uploadTextPost(token: String, caption: UploadTextPostBody) {
        viewModelScope.launch {
            try {
                val response: Response<UploadPostResponseModel> =
                    repository.uploadTextPost(token, caption)
                uploadTextresponse.value = response
            } catch (e: Exception) {
                return@launch
            }
        }
    }
}