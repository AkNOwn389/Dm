package com.aknown389.dm.pageView.homeFeed.remote.repository

import android.content.Context
import com.aknown389.dm.models.homepostmodels.FeedResponseModelV2
import com.aknown389.dm.pageView.homeFeed.remote.instance.Instance
import retrofit2.Response

class Repository {
    suspend fun newFeed(page:Int, context: Context):Response<FeedResponseModelV2>{
        return Instance(context = context).api.newsfeed(page = page)
    }
}