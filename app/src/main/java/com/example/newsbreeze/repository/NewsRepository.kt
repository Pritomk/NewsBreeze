package com.example.newsbreeze.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.newsbreeze.room.News
import com.example.newsbreeze.dao.NewsDao
import com.example.newsbreeze.dao.NewsItemDao
import com.example.newsbreeze.dao.OnlineDao
import com.example.newsbreeze.room.NewsItem
//Call news repository with news dao and news item dao
class NewsRepository(private val newsDao: NewsDao, private val newsItemDao : NewsItemDao) {
    //Call read news variable carry all the news list
    val readNews: LiveData<List<News>> = newsDao.readAllNews()
    //Initialize online news dao
    private val onlineNewsDao = OnlineDao()
    //Get the read news item list
    val breakingNewsItemList: LiveData<List<NewsItem>> = onlineNewsDao.newsItemList
    //Get the read news item list
    val readNewsItem: LiveData<List<NewsItem>> = newsItemDao.readAllNewsItem()

    //Insert news in news table function
    suspend fun insertNews(news: News) {
        newsDao.insertNews(news)
    }

    //Delete news from news table function
    suspend fun deleteNews(title: String, date: String) {
        newsDao.deleteNews(title,date)
    }

    //Get today news
    fun todayNews(date: String): LiveData<List<News>> {
        return newsDao.todaySaveNews(date)
    }

    //Call read breaking news with category
    fun readingBreakingNews(context: Context, category: String) {
        onlineNewsDao.readBreakingNews(context, category)
    }

    //Get the news by id from news table
    fun getNewsById(newsId: Long) : LiveData<List<News>> {
        return newsDao.getNewsById(newsId)
    }

    //Insert news item into news item table
    suspend fun insertNewsItem(newsItem: NewsItem) {
        newsItemDao.insertNews(newsItem)
    }

    //Get news item by id from news item table
    fun getNewsItemById(newsId: Long): LiveData<List<NewsItem>> {
        return newsItemDao.getNewsItemById(newsId)
    }

}