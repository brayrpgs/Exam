package com.una.exam.network

import com.una.exam.models.Student
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface StudentsService {
    @POST("api/student")
    suspend fun insertStudent(@Body student: Student): Student

    @GET("api/courses/{id}/students")
    suspend fun getStudentsByCourse(@Path("id") id: Int): List<Student>

    @PUT("api/student")
    suspend fun updateStudent(@Body student: Student): Student

    @DELETE("api/student/{id}")
    suspend fun deleteStudent(@Path("id") id: Int)
}