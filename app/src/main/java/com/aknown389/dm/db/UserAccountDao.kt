package com.aknown389.dm.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.aknown389.dm.db.local.UserAccountDataClass
import com.aknown389.dm.models.loginRegModels.Info


@Dao
interface UserAccountDao {

    @Transaction
    suspend fun updateAccountImage(id: String, image: String){
        val account = getAccountsById(id)
        account.info.profileimg = image
        updateAccountInfoById(id = id, newInfo = account.info)
    }

    @Transaction
    suspend fun updateAccountName(id: String, name: String){
        val account = getAccountsById(id)
        account.info.name = name
        updateAccountInfoById(id = id, newInfo = account.info)
    }

    @Query("SELECT * FROM userAccount")
    suspend fun getAllAccounts(): List<UserAccountDataClass>

    @Query("SELECT * FROM userAccount WHERE id = :id")
    suspend fun getAccountsById(id: String): UserAccountDataClass

    @Query("UPDATE userAccount SET info = :newInfo WHERE id = :id")
    suspend fun updateAccountInfoById(id: String, newInfo:Info)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(input: UserAccountDataClass)

    @Query("UPDATE userAccount SET lastLogin = :lastLogin WHERE id = :id")
    suspend fun updateAccountLastLogin(id: String, lastLogin: String)

    @Query("UPDATE userAccount SET accessToken = :accessToken WHERE id = :id")
    suspend fun updateAccountToken(id: String, accessToken: String)

    @Query("UPDATE userAccount SET refreshToken = :refreshToken WHERE id = :id")
    suspend fun updateAccountRefreshToken(id: String, refreshToken: String)

    @Query("DELETE FROM userAccount WHERE id = :id")
    suspend fun deleteAccountById(id: String)

    @Query("DELETE FROM userAccount")
    suspend fun deleteAllAccounts()
}