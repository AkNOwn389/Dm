package com.aknown389.dm.pageView.switchAccount.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.db.local.UserAccountDataClass
import com.aknown389.dm.pageView.switchAccount.remote.repository.SwitchAccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SwitchAccountViewModel @Inject constructor(private var dataBase: AppDataBase):ViewModel() {
    private var repository:SwitchAccountRepository = SwitchAccountRepository()
    val userAccount:MutableLiveData<List<UserAccountDataClass>> = MutableLiveData()

    fun getUserAccountFromDataBase(context: Context){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val accountToReturn = ArrayList<UserAccountDataClass>()
                val account = dataBase.userDao().getAllAccounts()
                Log.d(TAG, account.toString())
                for (i in account){
                    accountToReturn.add(i)
                }
                withContext(Dispatchers.Main){
                    userAccount.value = accountToReturn
                }
            }catch (e:Exception){
                Log.d(TAG, e.printStackTrace().toString())
                return@launch
            }
        }
    }
    private val TAG = "switchAccount"
}