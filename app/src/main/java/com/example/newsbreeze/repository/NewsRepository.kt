package com.example.newsbreeze.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.newsbreeze.room.News
import com.example.newsbreeze.dao.NewsDao
import com.example.newsbreeze.dao.OnlineDao
import com.example.newsbreeze.room.NewsItem

class NewsRepository(private val newsDao: NewsDao) {
    val readNews: LiveData<List<News>> = newsDao.readAllNews()
    private val onlineNewsDao = OnlineDao()
    val breakingNewsItemList: LiveData<List<NewsItem>> = onlineNewsDao.newsItemList

    suspend fun insertNews(news: News) {
        newsDao.insertNews(news)
    }

    suspend fun deleteNews(title: String, date: String) {
        newsDao.deleteNews(title,date)
    }

    fun todayNews(date: String): LiveData<List<News>> {
        return newsDao.todaySaveNews(date)
    }

    fun readingBreakingNews(context: Context, category: String) {
        onlineNewsDao.readBreakingNews(context, category)
    }

    fun getNewsById(newsId: Long) : LiveData<List<News>> {
        return newsDao.getNewsById(newsId)
    }
}