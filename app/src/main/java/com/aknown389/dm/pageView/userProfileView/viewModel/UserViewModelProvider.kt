package com.aknown389.dm.pageView.userProfileView.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aknown389.dm.db.AppDataBase

class UserViewModelProvider(val dataBase: AppDataBase):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserProfileViewModel(dataBase) as T
    }
}