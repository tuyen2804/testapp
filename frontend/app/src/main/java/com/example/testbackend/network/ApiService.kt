package com.example.testbackend.network

import com.example.testbackend.model.AuthResponse
import com.example.testbackend.Event
import com.example.testbackend.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("/api/auth/login")
    fun login(@Body user: User): Call<AuthResponse>


    @GET("/api/events")
    fun getEvents(): Call<List<Event>>
}
