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

    //Query to get all the news item from news item table by ascending order
    @Query("SELECT * FROM news_item_table ORDER BY id ASC")
    fun readAllNewsItem(): LiveData<List<NewsItem>>

//    Insert the news item in news item table and replace
//    the news which title adn description both are same
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(newsItem: NewsItem)

    //Query to get the news item by news id primary key
    @Query("SELECT * FROM news_item_table WHERE id=:newsId")
    fun getNewsItemById(newsId: Long) : LiveData<List<NewsItem>>


}