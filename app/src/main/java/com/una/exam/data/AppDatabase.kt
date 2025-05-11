package com.una.exam.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.una.exam.daos.CourseDao
import com.una.exam.models.Course

@Database(
    entities = [Course::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase(){
    abstract fun courseDao(): CourseDao
}