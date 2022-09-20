package com.example.newsbreeze.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsbreeze.room.News
import com.example.newsbreeze.room.NewsItem

@Dao
interface NewsItemDao {

    @Query("SELECT * FROM news_item_table ORDER BY id ASC")
    fun readAllNewsItem(): LiveData<List<NewsItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(newsItem: NewsItem)

    @Query("SELECT * FROM news_item_table WHERE id=:newsId")
    fun getNewsItemById(newsId: Long) : LiveData<List<NewsItem>>


}