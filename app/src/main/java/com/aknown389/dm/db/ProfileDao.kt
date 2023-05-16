package com.aknown389.dm.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.aknown389.dm.db.local.ImageDataModelEntity
import com.aknown389.dm.db.local.UserProfileDetailsDataEntities
import com.aknown389.dm.db.mappers.mapListImageDataModelEntityToListImageDataModel
import com.aknown389.dm.db.mappers.mapListImageDataModelToListImageDataModelEntity
import com.aknown389.dm.db.mappers.toUserProfileDetailsDataEntities
import com.aknown389.dm.db.mappers.toUserProfiledata
import com.aknown389.dm.models.profileGalleryModels.ImageDataModel
import com.aknown389.dm.models.profileModel.UserProfileData


@Dao
interface ProfileDao {
    @Transaction
    fun getImageGallery(): List<ImageDataModel>{
        val imageEntities = getImageDataEntities()
        return mapListImageDataModelEntityToListImageDataModel(imageEntities)
    }

    @Query("SELECT * FROM profileGallery")
    fun getImageDataEntities(): List<ImageDataModelEntity>

    @Transaction
    suspend fun insertImageToDb(input:List<ImageDataModel>){
        val images:List<ImageDataModelEntity> = mapListImageDataModelToListImageDataModelEntity(input)
        insertImage(images)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(images:List<ImageDataModelEntity>)

    @Query("DELETE FROM profileGallery")
    suspend fun deleteAllImageGalery()

    //profilePageMyDetails
    @Transaction
    fun getMyProfileDetail(): UserProfileData {
        return getUserProfileEntities().toUserProfiledata()
    }

    @Transaction
    suspend fun saveUserProfileDetails(input:UserProfileData){
        val data = input.toUserProfileDetailsDataEntities()
        insertMyDetails(data)
    }

    @Query("SELECT * FROM profileDetail")
    fun getUserProfileEntities(): UserProfileDetailsDataEntities

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMyDetails(me:UserProfileDetailsDataEntities)

    @Query("DELETE FROM profileDetail")
    suspend fun deleteProfileData()
}