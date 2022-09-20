package com.example.newsbreeze.room

data class NewsItem (
    val title: String,
    val author: String,
    val newsPicUrl: String,
    val description: String,
    val content: String,
    val date: String,
    val time: String,
    var flagSave: Boolean
)