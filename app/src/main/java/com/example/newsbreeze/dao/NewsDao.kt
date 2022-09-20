package com.example.newsbreeze.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.newsbreeze.room.News

@Dao
interface NewsDao {

    @Query("SELECT * FROM news_table ORDER BY newsId DESC")
    fun readAllNews(): LiveData<List<News>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNews(news: News)

    @Query("DELETE FROM news_table WHERE title=:title AND date=:date")
    suspend fun deleteNews(title: String, date: String)

    @Query("SELECT * FROM news_table WHERE date=:date ORDER BY newsId DESC")
    fun todaySaveNews(date: String) : LiveData<List<News>>

    @Query("SELECT * FROM news_table WHERE newsId=:newsId")
    fun getNewsById(newsId: Long) : LiveData<List<News>>

}