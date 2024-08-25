package com.example.oasis_con.model

data class Post(
    val id: String,
    val title: String,
    val content: String,
    var fileUrl: String? = null
)
