package com.aknown389.dm.pageView.changePassword.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aknown389.dm.models.postmodel.NormalResponseModel
import com.aknown389.dm.pageView.recoverAccountView.models.AccountRecoveryChangePasswordBody
import com.aknown389.dm.pageView.recoverAccountView.repository.RecoverAccountRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class ChangePasswordViewModel:ViewModel() {
    val _response:MutableLiveData<NormalResponseModel?> = MutableLiveData()
    private var repository = RecoverAccountRepository()
    var isLoadingShowing = false
    fun submit(body: AccountRecoveryChangePasswordBody){
        viewModelScope.launch {
            try {
                val response:Response<NormalResponseModel> = repository.recoveryAccountChangePassword(body)
                _response.value = response.body()
            }catch (e:Exception){
                _response.value = null
                e.printStackTrace()
                return@launch
            }
        }
    }
}