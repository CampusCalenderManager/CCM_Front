package com.example.ccm.LocalDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Database(entities = [User::class], version = 1)
@TypeConverters(
    value = [
        CategoryListTypeConverter::class,
        CalendarScheduleObjectListTypeConverter::class
    ]
)
abstract class UserDatabase: RoomDatabase() {
    abstract fun userDao(): UserDAO

    companion object {
        private var instance: UserDatabase? = null

        @Synchronized
        fun getInstance(context: Context): UserDatabase? {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .addLast(LocalDateTimeAdapter())
                .build()
            if (instance == null) {
                synchronized(UserDatabase::class){
                    instance = Room
                        .databaseBuilder(
                            context.applicationContext,
                            UserDatabase::class.java,
                            "user-database"
                        )
                        .addTypeConverter(CategoryListTypeConverter(moshi))
                        .addTypeConverter(CalendarScheduleObjectListTypeConverter(moshi))
                        .build()
                }
            }

            return instance
        }
    }
}