package com.example.testbackend.model

import com.example.testbackend.User

data class AuthResponse(val auth: Boolean, val user: User?)