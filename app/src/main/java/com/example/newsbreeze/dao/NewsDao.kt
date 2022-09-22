package com.example.newsbreeze.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.newsbreeze.room.News

@Dao
interface NewsDao {

    //Get all the news from news table
    @Query("SELECT * FROM news_table ORDER BY newsId DESC")
    fun readAllNews(): LiveData<List<News>>

    //Insert the news into news table and ignore which title and descriptions are same
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNews(news: News)

    //Delete the news which title and date are matched
    @Query("DELETE FROM news_table WHERE title=:title AND date=:date")
    suspend fun deleteNews(title: String, date: String)

    //Get only todays news
    @Query("SELECT * FROM news_table WHERE date=:date ORDER BY newsId DESC")
    fun todaySaveNews(date: String) : LiveData<List<News>>

    //Get only one news which news id is same
    @Query("SELECT * FROM news_table WHERE newsId=:newsId")
    fun getNewsById(newsId: Long) : LiveData<List<News>>

}