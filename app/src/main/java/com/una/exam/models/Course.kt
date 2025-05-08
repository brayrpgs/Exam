package com.una.exam.models

import java.io.File

data class Course (
    val id: Int?, // Optional
    val name: String,
    val description: String,
    val imageUrl: String?, // Optional
    val schedule: String,
    val professor: String,
    @Transient val imageFile: File? = null
)