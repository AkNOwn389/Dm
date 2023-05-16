package com.aknown389.dm.pageView.notification.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.pageView.notification.remote.repository.Repository

class NotificationViewModelFactory(
    private val repository: Repository,
    private val dataBase: AppDataBase
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModel(repository, database = dataBase) as T
    }
}