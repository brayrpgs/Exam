package com.una.exam.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.una.exam.daos.CourseDao
import com.una.exam.daos.StudentDao
import com.una.exam.models.Course
import com.una.exam.models.StudentEntity

@Database(
    entities = [Course::class, StudentEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun studentDao(): StudentDao

}