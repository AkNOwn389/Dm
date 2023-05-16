package com.aknown389.dm.pageView.profile.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.repository.Repository
import dagger.Component.Factory
import javax.inject.Inject

@Factory
class ProfileViewModelFactory @Inject constructor (
    private val repository: Repository,
    private val token:String,
    private val db: AppDataBase
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(repository = repository, token = token, db = db) as T
    }
}