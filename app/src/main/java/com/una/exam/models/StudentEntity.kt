package com.una.exam.models


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Student")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val email: String,
    val phone: String,
    val courseId: Int? = null
)