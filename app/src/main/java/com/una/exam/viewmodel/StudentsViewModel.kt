package com.una.exam.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.una.exam.data.StudentRepository
import com.una.exam.models.Student
import com.una.exam.network.RetrofitInstance
import kotlinx.coroutines.launch

class StudentsViewModel(private val repository: StudentRepository) : ViewModel() {
    var students by mutableStateOf<List<Student>>(emptyList())
    var isLoading by mutableStateOf(true)
        private set
    var dataOrigin by mutableStateOf("LOADING")
        private set

    fun fetchStudents(courseId: Int) {
        viewModelScope.launch {
            isLoading = true
            dataOrigin = "LOADING"
            try {
                val response = RetrofitInstance.apiStudents.getStudentsByCourse(courseId)

                if (response.isSuccessful && response.body() != null) {
                    val remoteStudents = response.body()!!
                    students = remoteStudents
                    repository.deleteStudentsByCourse(courseId)
                    repository.addStudents(remoteStudents)

                    val rawResponse = response.raw()
                    dataOrigin = when {
                        rawResponse.networkResponse != null -> "REMOTE"
                        rawResponse.cacheResponse != null -> "LOCAL"
                        else -> "UNKNOWN"
                    }
                } else {
                    Log.e(
                        "StudentsViewModel",
                        "API error: ${response.code()} - ${response.message()}"
                    )
                    students = repository.getStudentsByCourseId(courseId)
                    dataOrigin = "LOCAL"
                }
            } catch (e: Exception) {
                Log.e("StudentsViewModel", "Network error: ${e.message}")
                students = repository.getStudentsByCourseId(courseId)
                dataOrigin = "LOCAL"
            } finally {
                isLoading = false
            }
        }
    }


    fun insertStudent(student: Student) {
        viewModelScope.launch {
            try {
                RetrofitInstance.apiStudents.insertStudent(student)
                repository.addStudents(listOf(student))
                Log.i("Test", "$student.courseId")
                fetchStudents(student.courseId!!)
            } catch (e: Exception) {
                Log.e("StudentsViewModel", "Insert error: $e")
            }
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            try {
                RetrofitInstance.apiStudents.updateStudent(student)
                repository.updateStudent(student)
                fetchStudents(student.courseId!!)
            } catch (e: Exception) {
                Log.e("StudentsViewModel", "Update error: $e")
            }
        }
    }

    fun deleteStudent(id: Int, courseId: Int) {
        viewModelScope.launch {
            try {
                RetrofitInstance.apiStudents.deleteStudent(id)
                repository.deleteStudentById(id)
                fetchStudents(courseId)
            } catch (e: Exception) {
                Log.e("StudentsViewModel", "Delete error: $e")
            }
        }
    }
}
