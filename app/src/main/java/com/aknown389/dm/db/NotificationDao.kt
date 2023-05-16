package com.aknown389.dm.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.aknown389.dm.pageView.notification.models.NotificationDataClass

@Dao
interface NotificationDao {
    @Transaction
    fun reportSeen(id:Int):Boolean{
        return try {
            updateNotificationSeen(id = id)
            true
        }catch (e:Exception){
            e.printStackTrace()
            false
        }
    }
    @Query("UPDATE NotificationData SET seen = 1 WHERE id = :id")
    fun updateNotificationSeen(id: Int)

    @Query("SELECT * FROM NotificationData")
    suspend fun getNotification():List<NotificationDataClass>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(input:List<NotificationDataClass>)


    @Query("DELETE FROM NotificationData")
    suspend fun deleteAllNotification()


}