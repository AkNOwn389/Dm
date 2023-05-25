package com.aknown389.dm.pageView.recoverAccountView.viewModel

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aknown389.dm.pageView.recoverAccountView.models.RecoveryAccountVerifyOtp
import com.aknown389.dm.pageView.recoverAccountView.models.RecoveryAccountVerifyOtpResponse
import com.aknown389.dm.pageView.recoverAccountView.models.RecoveryAccountVerifyOtpResponseData
import com.aknown389.dm.pageView.recoverAccountView.models.RequestAccountRecovery
import com.aknown389.dm.pageView.recoverAccountView.models.RequestAccountRecoveryResponse
import com.aknown389.dm.pageView.recoverAccountView.models.RequestAccountRecoveryResponseData
import com.aknown389.dm.pageView.recoverAccountView.repository.RecoverAccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

@HiltViewModel
class RecoveryAccountViewModel:ViewModel() {
    var countDoneRunning = false
    private val repository = RecoverAccountRepository()
    val countDown:MutableLiveData<String> = MutableLiveData()
    var attempt = 5
    var isLoading = false
    var requestAccountRecoveryResponseData: RequestAccountRecoveryResponseData? = null
    var verifyOtpResponseData: RecoveryAccountVerifyOtpResponseData? = null
    val _request_recovery_response:MutableLiveData<RequestAccountRecoveryResponse?> = MutableLiveData()
    val _verify_code_response:MutableLiveData<RecoveryAccountVerifyOtpResponse?> = MutableLiveData()

    fun requestAccountRecovery(body:RequestAccountRecovery){
        viewModelScope.launch {
            isLoading = true
            if (isActive){
                try {
                    val response:Response<RequestAccountRecoveryResponse> = repository.requestAccountRecovery(body)
                    _request_recovery_response.value = response.body()!!
                    if (response.body()!!.status){
                        attempt = 5
                        startCountDown()
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    _request_recovery_response.value = null
                    return@launch
                }
            }
            isLoading = false
        }
    }
    fun verifyCode(body:RecoveryAccountVerifyOtp){
        viewModelScope.launch {
            isLoading = true
            if (isActive){
                try {
                    val response:Response<RecoveryAccountVerifyOtpResponse> = repository.requestAccountVerifyOtp(body)
                    _verify_code_response.value = response.body()
                }catch (e:Exception){
                    e.printStackTrace()
                    _verify_code_response.value = null
                    return@launch
                }
            }
            isLoading = false
        }
    }
    fun startCountDown(){
        val countDownTimer = object : CountDownTimer(300000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                countDown.value = String.format("%02d:%02d", minutes, seconds)
            }
            override fun onFinish() {
                countDown.value = "00:00"
                countDoneRunning = false
            }
        }
        if (!countDoneRunning){
            countDownTimer.start()
            countDoneRunning = true
        }
    }
}