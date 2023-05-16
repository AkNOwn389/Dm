package com.aknown389.dm.pageView.changeCover.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aknown389.dm.repository.Repository

class ChangeCoverFactory(
    private val repository: Repository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChangeCoverViewModel(repository) as T
    }
}