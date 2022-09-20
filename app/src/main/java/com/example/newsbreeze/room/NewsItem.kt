package com.example.newsbreeze.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "news_item_table", indices = [Index(value = ["title","description"], unique = true)])
data class NewsItem(
    @PrimaryKey
    val id: Int,
    val title: String,
    val author: String,
    val newsPicUrl: String,
    val description: String,
    val content: String,
    val date: String,
    val time: String,
    var flagSave: Boolean
)