package com.aknown389.dm.pageView.mainSearch.remote.repository

import android.content.Context
import com.aknown389.dm.models.mainSearchActivityModels.SearchResponse
import com.aknown389.dm.models.postmodel.NormalResponseModel
import com.aknown389.dm.models.searchActivityModels.searchItemsModels
import com.aknown389.dm.pageView.mainSearch.remote.instance.Instance
import retrofit2.Response


class Repository {
    suspend fun searchUser(context: Context, user:String, page:Int):Response<searchItemsModels>{
        return Instance(context).api.searchuser(user, page)
    }

    suspend fun mainSearch(
        context: Context,
        user: String,
        type: String,
        page: Int
    ): Response<SearchResponse> {
        return Instance(context).api.mainSearch(user, type, page)
    }

    suspend fun saveRecent(context: Context,user: String): Response<NormalResponseModel> {
        return Instance(context).api.saveRecent(user)
    }

    suspend fun loadSearchRecent(context: Context,): Response<SearchResponse> {
        return Instance(context = context).api.loadSearchRecent()
    }
}