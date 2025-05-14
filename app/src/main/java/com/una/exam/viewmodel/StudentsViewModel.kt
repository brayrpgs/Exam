package com.una.exam.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.una.exam.models.Student
import com.una.exam.network.RetrofitInstance
import kotlinx.coroutines.launch

class StudentsViewModel : ViewModel() {
    var students by mutableStateOf<List<Student>>(emptyList())
    var isLoading by mutableStateOf(true)
        private set

    fun fetchStudents(id: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                students = RetrofitInstance.apiStudents.getStudentsByCourse(id)
                Log.i("StudentsViewModel", "Fetched students: $students")
            } catch (e: Exception) {
                Log.e("StudentsViewModel", "Error: $e")
            } finally {
                isLoading = false
            }
        }
    }

    fun insertStudent(student: Student) {
        viewModelScope.launch {
            try {
                RetrofitInstance.apiStudents.insertStudent(student)
                fetchStudents(student.courseId!!) // sincroniza después
            } catch (e: Exception) {
                Log.e("StudentsViewModel", "Insert error: $e")
            }
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            try {
                RetrofitInstance.apiStudents.updateStudent(student)
                fetchStudents(student.courseId!!) // sincroniza después
            } catch (e: Exception) {
                Log.e("StudentsViewModel", "Update error: $e")
            }
        }
    }

    fun deleteStudent(id: Int, courseId: Int) {
        viewModelScope.launch {
            try {
                RetrofitInstance.apiStudents.deleteStudent(id)
                fetchStudents(courseId) // sincroniza después
            } catch (e: Exception) {
                Log.e("StudentsViewModel", "Delete error: $e")
            }
        }
    }
}
