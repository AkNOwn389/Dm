package com.aknown389.dm.db.repository


import com.aknown389.dm.api.retrofitapi.AppApi
import com.aknown389.dm.db.HomeFeedDao
import com.aknown389.dm.models.homepostmodels.PostDataModel

class DataRepository(
    private val dataDao: HomeFeedDao,
    private val api: AppApi,
    private val token:String,
) {
    fun getData(): List<PostDataModel> {
        return dataDao.getFeed()
    }
}