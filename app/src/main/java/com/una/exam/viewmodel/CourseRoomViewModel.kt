package com.una.exam.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.una.exam.data.CourseRepository
import com.una.exam.data.DatabaseProvider
import com.una.exam.models.Course
import com.una.exam.network.RetrofitInstance
import com.una.exam.utils.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CourseRoomViewModel(application: Application) : AndroidViewModel(application) {
    private val courseRepository = CourseRepository(application)
    private val _dataOrigin = MutableStateFlow("LOADING")
    val dataOrigin:StateFlow<String> get()=_dataOrigin
    val courses: Flow<List<Course>> = courseRepository.getAllCourses()

    fun insertCourse(course:  List<Course>) {
        viewModelScope.launch {
            courseRepository.addCourse(course)
        }
    }

    fun updateCourse(course: Course) {
        viewModelScope.launch {
            courseRepository.updateCourse(course)
        }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch {
            courseRepository.deleteCourse(course)
        }
    }

    fun fetchCourse() {
        viewModelScope.launch {
            if (NetworkUtils.isNetworkAvailable(getApplication())) {

                _dataOrigin.value = "REMOTE"
                try {
                    val apiCourses = RetrofitInstance.api.getCourses()

                    val roomEvents = apiCourses.map { apiCourse ->
                        Course(
                            id = apiCourse.id,
                            name = apiCourse.name,
                            description = apiCourse.description,
                            schedule = apiCourse.schedule,
                            professor = apiCourse.professor,
                            imageUrl = apiCourse.imageUrl
                        )
                    }

                    insertCourse(roomEvents)

                    courseRepository.getAllCourses().collect { localCourses ->
                        Log.d("RoomCourse", "Courses in Room: $localCourses")
                    }

                } catch (e: Exception) {
                    Log.e("CourseRoomViewModel", "Error fetching courses from API", e)
                }
            } else {
                _dataOrigin.value = "LOCAL"
                Log.d("CourseRoomViewModel", "No internet connection, using local data.")
            }
        }
    }


}