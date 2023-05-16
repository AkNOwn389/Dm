package com.aknown389.dm.repository

import com.aknown389.dm.api.retroInstance.PostInstance
import com.aknown389.dm.api.retroInstance.RetrofitInstance
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.models.homepostmodels.FeedResponseModelV2
import com.aknown389.dm.models.postmodel.UploadPostResponseModel
import com.aknown389.dm.models.postmodel.UploadTextPostBody
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class GlobalRepository @Inject constructor(
    private val api: RetrofitInstance,
    private val db: AppDataBase
) {
    /*
    private val homeFeedDao = db.homefeedDao()
    fun getHomeFeeds(page: Int, token: String) =
        networkBoundResource(
            query = {
                homeFeedDao.getFeed()
            },
            fetch = {
                delay(2000)
                api.api.newsfeed(page = page, token = token)
            },
            saveFetchResult ={ response ->
                db.withTransaction {
                    homeFeedDao.deleteAllFeed()
                    homeFeedDao.insertFeed(response.body()!!)
                }
            }
        )

     */
    suspend fun loadFeed(token: String, page:Int):Response<FeedResponseModelV2>{
        return RetrofitInstance.api.newsfeed(page = page, token = token)
    }

    suspend fun uploadImage(token: String, file: List<MultipartBody.Part>, caption: RequestBody, privacy: RequestBody): Response<UploadPostResponseModel> {
        return PostInstance.api.uploadMultipleImages(token, caption = caption, images = file, privacy = privacy)
    }

    suspend fun uploadTextPost(token: String, caption: UploadTextPostBody): Response<UploadPostResponseModel> {
        return PostInstance.api.uploadTextPost(token, caption)
    }
    suspend fun updateProfilePicture(token: String, image: MultipartBody.Part, caption: RequestBody): Response<UploadPostResponseModel> {
        return RetrofitInstance.api.updateProfilePicture(token, image, caption)

    }

    suspend fun updateProfileCover(token: String, image: MultipartBody.Part, caption: RequestBody): Response<UploadPostResponseModel> {
        return RetrofitInstance.api.updateProfileCover(token, image, caption)
    }
}