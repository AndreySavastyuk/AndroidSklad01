package com.example.warehouse.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.warehouse.data.PickDao
import com.example.warehouse.data.PickLog
import com.example.warehouse.data.PickTask
import com.example.warehouse.data.Task
import com.example.warehouse.data.TaskDao
import com.example.warehouse.data.ReceiveTask
import androidx.room.TypeConverters



@Database(
    entities = [
        Task::class,
        ReceiveTask::class,
        PickTask::class,
        PickLog::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun pickDao(): PickDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "warehouse.db"
            )
                .fallbackToDestructiveMigration()
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
    }
}
