package com.una.exam.data

import android.content.Context
import com.una.exam.daos.CourseDao
import com.una.exam.models.Course
import kotlinx.coroutines.flow.Flow

class CourseRepository(context: Context){
    private val courseDao: CourseDao = DatabaseProvider.getDatabase(context).courseDao()

    fun getAllCourses(): Flow<List<Course>>{
        return courseDao.getAllCourses()
    }

    suspend fun addCourse(course: List<Course>){
        courseDao.addCourse(course)
    }

    suspend fun updateCourse(course: Course){
        courseDao.updateCourse(course)
    }

    suspend fun deleteCourse(course: Course){
        courseDao.deleteCourse(course)
    }
}