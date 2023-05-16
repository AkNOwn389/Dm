package com.aknown389.dm.db.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aknown389.dm.db.mappers.toHomeFeedDataEntity
import com.aknown389.dm.db.AppDataBase
import com.aknown389.dm.models.homepostmodels.FeedResponseModelV2
import com.aknown389.dm.repository.Repository
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class HomeItemRemoteMediator(
    private val backend: Repository,
    private val database: AppDataBase,
    private val token:String
):RemoteMediator<Int, FeedResponseModelV2>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, FeedResponseModelV2>
    ): MediatorResult {
        return try {
            val pageKey = when (loadType) {
                LoadType.REFRESH -> {1}
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    lastItem.nextPageKey
                }
            }
            val response = backend.getFeed(token=token, page = pageKey)
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.homeFeedDao().deleteAllFeed()
                }
                database.homeFeedDao().insertFeed(response.body()!!.data.map { it.toHomeFeedDataEntity() })
            }
            MediatorResult.Success(endOfPaginationReached = response.body()!!.hasMorePage)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}