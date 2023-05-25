package com.aknown389.dm.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.aknown389.dm.db.local.NewsDataEntities
import com.aknown389.dm.db.local.RemoteKey

@Dao
interface NewsDao {

    @Transaction
    suspend fun reportUnLike(id:String):Boolean{
        return try {
            updateItemLike(id = id)
            true
        }catch (e:Exception){
            e.printStackTrace()
            false
        }
    }
    @Query("UPDATE NewsData SET is_like = 0 WHERE id = :id")
    suspend fun updateItemUnLike(id: String)
    @Transaction
    suspend fun reportLike(id:String):Boolean{
        return try {
            updateItemLike(id = id)
            true
        }catch (e:Exception){
            e.printStackTrace()
            false
        }
    }
    @Query("UPDATE NewsData SET is_like = 1 WHERE id = :id")
    suspend fun updateItemLike(id: String)

    @Query("SELECT * FROM NewsData")
    suspend fun getAllNewsData():List<NewsDataEntities>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(input:List<NewsDataEntities>)

    @Query("DELETE FROM NewsData")
    suspend fun deleteAllNews()


    @Query("SELECT * FROM newsRemoteKey")
    suspend fun getAllRemoteKey():List<RemoteKey>


    @Query("SELECT * FROM  newsRemoteKey WHERE dataId = :id")
    suspend fun getRemoteKey(id:String):RemoteKey


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKey(input:List<RemoteKey>)

    @Query("DELETE FROM newsRemoteKey")
    suspend fun deleteRemoteKey()

}