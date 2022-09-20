package com.example.newsbreeze.room

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "news_table", indices = [Index(value = ["title","description"], unique = true)])
data class News(
    @PrimaryKey(autoGenerate = true)
    val newsId: Long,
    val title: String,
    val author: String,
    var newsPic: Bitmap?,
    val description: String,
    val content: String,
    val date: String,
    val time: String,
    var flagSave: Boolean
)
