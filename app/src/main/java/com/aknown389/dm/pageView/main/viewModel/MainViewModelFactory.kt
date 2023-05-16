package com.aknown389.dm.pageView.main.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.repository.Repository
import dagger.Component.Factory
import javax.inject.Inject

@Factory
class MainViewModelFactory @Inject constructor(
    private val dataBase: AppDataBase,
    private val token:String,
    private val repository: Repository
    ): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository = repository, db = dataBase, token = token) as T
    }
}