package com.una.exam.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.una.exam.models.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM Student")
    fun getStudents(): Flow<List<StudentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)

    @Query("SELECT * FROM Student WHERE courseId = :courseId")
    suspend fun getStudentsByCourse(courseId: Int): List<StudentEntity>

    @Query("DELETE FROM Student WHERE courseId = :courseId")
    suspend fun deleteStudentsByCourse(courseId: Int)

    @Query("SELECT * FROM Student WHERE id = :id LIMIT 1")
    suspend fun getStudentById(id: Int): StudentEntity?
}