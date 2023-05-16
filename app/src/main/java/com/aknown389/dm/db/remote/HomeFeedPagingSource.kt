package com.aknown389.dm.db.remote

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aknown389.dm.models.homepostmodels.PostDataModel
import com.aknown389.dm.repository.Repository
import retrofit2.HttpException
import java.io.IOException

class HomeFeedPagingSource(
    private val token:String,
    private val context: Context
): PagingSource<Int, PostDataModel>() {
    private val repository:Repository = Repository()
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostDataModel> {
        return try {
            val page = params.key ?: 1
            val response = repository.getFeed(token = token, page = page)
            LoadResult.Page(
                data = response.body()!!.data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (!response.body()!!.hasMorePage) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PostDataModel>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?:state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}