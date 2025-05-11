package com.una.exam.data

import android.content.Context
import android.preference.DialogPreference
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.Executors

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "Course"
            ).addCallback(object: RoomDatabase.Callback(){
                override fun onCreate(db: SupportSQLiteDatabase){
                    super.onCreate(db)
                    Log.d("Room", "Database created")
                }

                override fun onOpen(db: SupportSQLiteDatabase){
                    super.onOpen(db)
                    Log.d("Room", "Database opened")

                }
            }).setQueryCallback({ sqlQuery, bindArgs ->
                Log.d("RoomSQL", "SQL Query: $sqlQuery SQL Args: $bindArgs")
            }, Executors.newSingleThreadExecutor()).build()
                    INSTANCE = instance
                    instance
        }
    }
}