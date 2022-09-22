package com.example.newsbreeze.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

//Make news item table in which the title and the description will be unique
@Entity(tableName = "news_item_table", indices = [Index(value = ["title","description"], unique = true)])
//NewsItem model consist some parameter and a primary key
data class NewsItem(
    @PrimaryKey
    val id: Long,
    val title: String,
    val author: String,
    val newsPicUrl: String,
    val description: String,
    val content: String,
    val date: String,
    val time: String,
    var flagSave: Boolean
)