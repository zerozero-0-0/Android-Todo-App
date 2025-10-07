package com.example.todox.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.todox.data.dao.TodoDao
import com.example.todox.data.model.Todo
import kotlinx.serialization.json.Json

@Database(
    entities = [Todo::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        fun build(context: Context): AppDatabase {
            val json = Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            }
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "todos.db"
            )
                .addTypeConverter(Converters(json))
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
        }
    }
}
