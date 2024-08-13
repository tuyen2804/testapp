package com.example.testbackend.network

import com.example.testbackend.model.AuthResponse
import com.example.testbackend.model.Event
import com.example.testbackend.model.User
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("/api/auth/login")
    fun login(@Body user: User): Call<AuthResponse>

    @GET("/api/events")
    fun getEvents(@Header("Authorization") authToken: String): Call<List<Event>>

    @Multipart
    @POST("/api/events/upload")
    fun uploadImage(
        @Part file: MultipartBody.Part
    ): Call<JsonObject> // Giả sử server trả về URL hình ảnh dưới dạng chuỗi

    @POST("/api/add")
    fun addEvent(
        @Body event: Event,
        @Header("Authorization") authToken: String
    ): Call<Void> // Giả sử không cần phần thân phản hồi cụ thể
}
