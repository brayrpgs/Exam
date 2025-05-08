package com.una.exam.network
import com.una.exam.models.Course
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface APIService {

    @GET("api/courses")
    suspend fun getCourses(): List<Course>

    @Multipart
    @POST("api/courses")
    suspend fun addCourse(
        @Part("Name") name: RequestBody,
        @Part("Description") description: RequestBody,
        @Part("Schedule") schedule: RequestBody,
        @Part("Professor") professor: RequestBody,
        @Part imageUrl: MultipartBody.Part? = null
    ): Course

    @Multipart
    @PUT("api/courses")
    suspend fun updateCourse(
        @Part("id") id: Int,
        @Part("Name") name: RequestBody,
        @Part("Description") description: RequestBody,
        @Part("Schedule") schedule: RequestBody,
        @Part("Professor") professor: RequestBody,
        @Part imageUrl: MultipartBody.Part? = null

    ): Course

    @DELETE("api/courses/{id}")
    suspend fun deleteCourse(@Path("id") id: Int)

}