package com.aknown389.dm.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aknown389.dm.db.local.UserAccountDataClass


@Dao
interface UserAccountDao {

    @Query("SELECT * FROM userAccount")
    fun getAccounts():List<UserAccountDataClass>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(input:UserAccountDataClass)

    @Query("DELETE FROM userAccount")
    suspend fun deleteAllAccount()
}