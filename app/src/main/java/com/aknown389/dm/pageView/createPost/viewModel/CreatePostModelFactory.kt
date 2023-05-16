package com.aknown389.dm.pageView.createPost.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aknown389.dm.repository.Repository

class CreatePostModelFactory (
    private val repository: Repository
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CreatePostViewModel(repository) as T
        }
}