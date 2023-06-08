package com.aknown389.dm.pageView.profile.remote

import com.aknown389.dm.models.postmodel.NormalResponseModel
import com.aknown389.dm.pageView.profile.dataClass.DeleteImageDataClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ProfileApi {
    @POST("/post/delete-post-image")
    suspend fun deleteImage(
        @Body body: DeleteImageDataClass,
    ):Response<NormalResponseModel>
}