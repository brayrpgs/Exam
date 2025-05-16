package com.una.exam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.una.exam.data.StudentRepository
import com.una.exam.models.Student
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudentsViewModelRoom(private val repository: StudentRepository) : ViewModel() {

    val students: StateFlow<List<Student>> = repository.getAllStudents()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insertStudent(student: Student) = viewModelScope.launch {
        repository.addStudents(listOf(student))
    }

    fun updateStudent(student: Student) = viewModelScope.launch {
        repository.updateStudent(student)
    }

    fun deleteStudent(student: Student) = viewModelScope.launch {
        repository.deleteStudent(student)
    }
}
