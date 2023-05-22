package com.aknown389.dm.pageView.switchAccount.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.pageView.newsView.viewModel.NewViewModel

class SwitchAccountViewModelFactory(private var appDataBase: AppDataBase): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SwitchAccountViewModel(dataBase = appDataBase) as T
    }
}