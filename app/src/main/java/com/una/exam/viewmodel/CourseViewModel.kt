package com.una.exam.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.una.exam.models.Course
import com.una.exam.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import androidx.compose.runtime.State

class CourseViewModel: ViewModel() {

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> get() = _courses
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun fetchCourses() {
        viewModelScope.launch {
            try {
                _courses.value = RetrofitInstance.api.getCourses()
                Log.i("CourseViewModel", "Fetching data from API")

            } catch (e: Exception) {
                Log.e("CourseViewModelError", "Error: $e")
            }
        }
    }

    fun addCourse(course: Course) {
        viewModelScope.launch {
            try {

                val name = course.name.toRequestBody("text/plain".toMediaTypeOrNull())
                val description = course.description.toRequestBody("text/plain".toMediaTypeOrNull())
                val schedule = course.schedule.toRequestBody("text/plain".toMediaTypeOrNull())
                val professor = course.professor.toRequestBody("text/plain".toMediaTypeOrNull())

                val imageFile = course.imageFile
                val imagePart = imageFile?.let {
                    val requestFile = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", it.name, requestFile)
                }

                Log.i("CourseViewModelInfo", "Event: $course")
                val response = RetrofitInstance.api.addCourse(name, description, schedule, professor, imagePart)
                _courses.value += response
                Log.i("CourseViewModelInfo", "Response: $response")

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("CourseViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
            } catch (e: Exception) {
                Log.e("CourseViewModelError", "Error: ${e.message}", e)
            }
        }
    }

    fun updateCourse(course: Course) {
        viewModelScope.launch {
            try {
                Log.i("CourseViewModelInfo", "Course $course")

                val name = course.name.toRequestBody("text/plain".toMediaTypeOrNull())
                val description = course.description.toRequestBody("text/plain".toMediaTypeOrNull())
                val schedule = course.schedule.toRequestBody("text/plain".toMediaTypeOrNull())
                val professor = course.professor.toRequestBody("text/plain".toMediaTypeOrNull())

                val imageFile = course.imageFile
                val imagePart = imageFile?.let {
                    val requestFile = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", it.name, requestFile)
                }

                val courseId = requireNotNull(course.id) { "Id of the course couldn't be null" }
                val response = RetrofitInstance.api.updateCourse(courseId, name, description, schedule, professor, imagePart)

                _courses.value = _courses.value.map { e ->
                    if (e.id == response.id) response else e
                }

                Log.i("CourseViewModelInfo", "Response: $response")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("CourseViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
            } catch (e: Exception) {
                Log.e("CourseViewModelError", "Error: ${e.message}", e)
            }
        }
    }

    fun deleteCourse(courseId: Int?) {
        courseId?.let { id ->
            viewModelScope.launch {
                try {

                    RetrofitInstance.api.deleteCourse(id)
                    _courses.value = _courses.value.filter { it.id != courseId }

                } catch (e: HttpException) {
                    _errorMessage.value = "The course could not be deleted because it already has students registered."

                } catch (e: Exception) {
                    _errorMessage.value = "Unexpected error while deleting the course."
                    Log.e("CourseViewModelError", "Error deleting course: ${e.message}")
                }
            }
        } ?: run {
            _errorMessage.value = "ID null"
            Log.e("CourseViewModelError", "Error: courseId is null")
        }
    }


    fun clearErrorMessage() {
        _errorMessage.value = null
    }


}
