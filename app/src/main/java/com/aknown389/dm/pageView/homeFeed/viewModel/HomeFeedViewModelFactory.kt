package com.aknown389.dm.pageView.homeFeed.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.pageView.homeFeed.remote.repository.Repository

class HomeFeedViewModelFactory (
    private val repository: Repository,
    private val dataBase: AppDataBase
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeFeedViewModel(repository, dataBase = dataBase) as T
    }
}