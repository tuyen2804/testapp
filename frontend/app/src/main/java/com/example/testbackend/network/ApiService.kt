package com.example.testbackend.network

import com.example.testbackend.model.AuthResponse
import com.example.testbackend.model.Event
import com.example.testbackend.model.User
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("/api/auth/login")
    fun login(@Body user: User): Call<AuthResponse>

    @GET("/api/events")
    fun getEvents(@Header("Authorization") authToken: String): Call<List<Event>>

    @POST("/api/events/uploadBase64")
    fun uploadBase64Image(
        @Body imageData: JsonObject
    ): Call<JsonObject>

    @POST("/api/add")
    fun addEvent(
        @Body event: Event,
        @Header("Authorization") authToken: String
    ): Call<Void>
}
