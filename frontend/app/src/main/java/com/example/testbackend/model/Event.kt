package com.example.testbackend.model

data class Event(
    val name: String,
    val description: String,
    val start_time: String, // Đổi tên từ startTime
    val end_time: String,   // Đổi tên từ endTime
    val location: String,
    val background_image_url: String // Đổi tên từ backgroundImageUrl
)

