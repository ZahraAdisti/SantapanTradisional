package com.example.santapantradisional.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.santapantradisional.database.dao.IUserDao
import com.example.santapantradisional.database.dao.RestaurantDao
import com.example.santapantradisional.models.Restaurant
import com.example.santapantradisional.models.Users

@Database(entities = [Users::class, Restaurant::class], version = 4)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usersDao(): IUserDao
    abstract fun restaurantDao(): RestaurantDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "AppDatabase"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
