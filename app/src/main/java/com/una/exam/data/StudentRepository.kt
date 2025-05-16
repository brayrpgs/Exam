package com.una.exam.data

import android.content.Context
import com.una.exam.daos.StudentDao
import com.una.exam.models.Student
import com.una.exam.mappers.toEntity
import com.una.exam.mappers.toStudent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StudentRepository(context: Context) {
    private val studentDao: StudentDao = DatabaseProvider.getDatabase(context).studentDao()

    fun getAllStudents(): Flow<List<Student>> {
        return studentDao.getStudents()
            .map { entityList -> entityList.map { it.toStudent() } }
    }

    suspend fun addStudents(students: List<Student>) {
        val entities = students.map { it.toEntity() }
        studentDao.insertStudents(entities)
    }

    suspend fun updateStudent(student: Student) {
        studentDao.updateStudent(student.toEntity())
    }

    suspend fun deleteStudent(student: Student) {
        studentDao.deleteStudent(student.toEntity())
    }

    suspend fun getStudentsByCourseId(courseId: Int): List<Student> {
        return studentDao.getStudentsByCourse(courseId).map { it.toStudent() }
    }

    suspend fun deleteStudentsByCourse(courseId: Int) {
        studentDao.deleteStudentsByCourse(courseId)
    }

    suspend fun getStudentById(id: Int): Student? {
        return studentDao.getStudentById(id)?.toStudent()
    }

    suspend fun deleteStudentById(id: Int) {
        val student = getStudentById(id)
        if (student != null) {
            deleteStudent(student)
        }
    }
}
