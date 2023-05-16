package com.aknown389.dm.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aknown389.dm.db.conventers.ListConventer
import com.aknown389.dm.db.local.HomeFeedDataEntity
import com.aknown389.dm.db.local.ImageDataModelEntity
import com.aknown389.dm.db.local.NewsDataEntities
import com.aknown389.dm.db.local.RemoteKey
import com.aknown389.dm.db.local.UserAccountDataClass
import com.aknown389.dm.db.local.UserProfileDetailsDataEntities
import com.aknown389.dm.pageView.notification.models.NotificationDataClass


@Database(entities = [
    HomeFeedDataEntity::class,
    ImageDataModelEntity::class,
    UserProfileDetailsDataEntities::class,
    NewsDataEntities::class,
    RemoteKey::class,
    NotificationDataClass::class,
    UserAccountDataClass::class,
                     ],
    version = 6,
)
@TypeConverters(ListConventer::class)
abstract class AppDataBase:RoomDatabase() {

    abstract fun homeFeedDao(): HomeFeedDao

    abstract fun profileDao(): ProfileDao

    abstract fun newDao(): NewsDao

    abstract fun notificationDao(): NotificationDao

    abstract fun userDao(): UserAccountDao

    companion object {

        @Volatile
        private var INSTANCE: AppDataBase? = null
        fun getDatabase(context: Context): AppDataBase {
            //context.getDatabasePath("dm_app_database").delete()
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "dm_app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}