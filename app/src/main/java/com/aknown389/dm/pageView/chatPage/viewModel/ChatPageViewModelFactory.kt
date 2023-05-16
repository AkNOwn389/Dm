package com.aknown389.dm.pageView.chatPage.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aknown389.dm.repository.ChatPageRepository

class ChatPageViewModelFactory(
    private val repository: ChatPageRepository
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatPageVieModel(repository) as T
        }
}