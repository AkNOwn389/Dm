package com.aknown389.dm.pageView.profile

import android.content.Context
import com.aknown389.dm.models.postmodel.NormalResponseModel
import com.aknown389.dm.pageView.profile.dataClass.DeleteImageDataClass
import com.aknown389.dm.pageView.profile.remote.ProfileObject
import retrofit2.Response

class ProfileRepository {
    suspend fun deleteImage(body:DeleteImageDataClass, context: Context):Response<NormalResponseModel>{
        return ProfileObject(context).api.deleteImage(body)
    }
}