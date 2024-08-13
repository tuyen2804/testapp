package com.example.testbackend.model

data class AuthResponse(
    val auth: Boolean,
    val token: String?,
    val user: User?
)

