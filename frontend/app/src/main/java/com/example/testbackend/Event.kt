package com.example.testbackend


data class Event(
    val id: Int,
    val background_image_url: String?,
    val name: String,
    val start_time: String,
    val end_time: String,
    val location: String?,
    val description: String?
)
