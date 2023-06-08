package com.aknown389.dm.pageView.videoGallery.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aknown389.dm.db.AppDataBase

class VideoGalleryViewModelFactory(val dataBase: AppDataBase):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VideoGalleryViewModel(dataBase = dataBase) as T
    }
}