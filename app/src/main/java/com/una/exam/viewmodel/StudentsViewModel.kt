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
                val remoteStudents = RetrofitInstance.apiStudents.getStudentsByCourse(courseId)
                students = remoteStudents
                repository.deleteStudentsByCourse(courseId)
                repository.addStudents(remoteStudents)
                dataOrigin = "REMOTE"
            } catch (e: Exception) {
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
