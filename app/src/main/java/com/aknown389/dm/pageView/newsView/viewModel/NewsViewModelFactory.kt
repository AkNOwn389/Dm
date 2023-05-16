package com.aknown389.dm.pageView.newsView.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aknown389.dm.db.AppDataBase

class NewsViewModelFactory(
    private val dao:AppDataBase
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewViewModel(dao = dao) as T
    }
}