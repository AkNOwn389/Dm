package com.aknown389.dm.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.aknown389.dm.db.local.HomeFeedDataEntity
import com.aknown389.dm.db.mappers.mapHomeFeedDataEntityListToPostDataModelList
import com.aknown389.dm.models.homepostmodels.PostDataModel


@Dao
interface HomeFeedDao {
    @Transaction
    fun reportLike(id:String):Boolean{
        return try {
            updateItemLike(id = String())
            true
        }catch (e:Exception){
            e.printStackTrace()
            false
        }
    }
    @Transaction
    fun reportUnLike(id: String): Boolean {
        return try {
            updateItemUnLike(id = String())
            true
        }catch (e:Exception){
            e.printStackTrace()
            false
        }
    }
    @Query("UPDATE homeFeeds SET isLike = 1 WHERE id = :id")
    fun updateItemLike(id: String)
    @Query("UPDATE homeFeeds SET isLike = 0 WHERE id = :id")
    fun updateItemUnLike(id: String)

    @Transaction
    fun getFeed(): List<PostDataModel> {
        val homeFeedDataEntities = getHomeFeedDataEntities()
        return mapHomeFeedDataEntityListToPostDataModelList(homeFeedDataEntities)
    }

    @Query("SELECT * FROM homeFeeds")
    fun getHomeFeedDataEntities(): List<HomeFeedDataEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeed(feed:List<HomeFeedDataEntity>)

    @Query("DELETE FROM homeFeeds")
    suspend fun deleteAllFeed()

}