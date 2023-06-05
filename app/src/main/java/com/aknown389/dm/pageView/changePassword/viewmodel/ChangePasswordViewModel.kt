package com.aknown389.dm.pageView.changePassword.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aknown389.dm.models.postmodel.NormalResponseModel
import com.aknown389.dm.pageView.changePassword.dataClass.ChangePasswordBody
import com.aknown389.dm.pageView.changePassword.remote.ChangePasswordInstance
import kotlinx.coroutines.launch

class ChangePasswordViewModel:ViewModel() {
    val _responseChangePassword:MutableLiveData<NormalResponseModel> = MutableLiveData()
    var newPassword:String? = null


    fun submit(context: Context, body: ChangePasswordBody){
        viewModelScope.launch {
            val response = try {
                ChangePasswordInstance(context).api.changePassword(body)
            }catch (e:Exception){
                //e.printStackTrace()
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                return@launch
            }
            try {
                _responseChangePassword.value = response.body()
            }catch (e:Exception){
                //e.printStackTrace()
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                return@launch
            }
        }
    }






}