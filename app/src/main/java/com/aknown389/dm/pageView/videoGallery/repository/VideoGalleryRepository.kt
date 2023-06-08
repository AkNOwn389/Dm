package com.aknown389.dm.pageView.videoGallery.repository

import android.content.Context
import com.aknown389.dm.pageView.videoGallery.dataClass.VideoGalleryDataClassResponse
import com.aknown389.dm.pageView.videoGallery.remote.VideoGalleryRemoteClass
import retrofit2.Response

class VideoGalleryRepository {
    suspend fun myVideoGallery(context: Context, page:Int): Response<VideoGalleryDataClassResponse>{
        return VideoGalleryRemoteClass(context).api.myVideoGallery(page)
    }
}