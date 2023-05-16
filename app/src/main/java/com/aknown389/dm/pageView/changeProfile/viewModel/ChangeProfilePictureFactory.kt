package com.aknown389.dm.pageView.changeProfile.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aknown389.dm.repository.Repository

class ChangeProfilePictureFactory(
    private val repository: Repository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChangeProfilePictureViewModel(repository) as T
    }
}