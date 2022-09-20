package com.example.newsbreeze.news

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.newsbreeze.repository.NewsRepository
import com.example.newsbreeze.room.News
import com.example.newsbreeze.room.NewsDatabase
import com.example.newsbreeze.room.NewsItem
import com.example.newsbreeze.room.NewsItemDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewsActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val newsDao = NewsDatabase.getDatabase(application).newsDao()
    private val newsItemDao = NewsItemDatabase.getDatabase(application).newsItemDao()
    private val repository = NewsRepository(newsDao,newsItemDao)

    fun insertNews(news: News) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertNews(news)
        }
    }

    fun deleteNews(title: String, date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNews(title,date)
        }
    }

    fun getNewsById(newsId: Long) : LiveData<List<News>> {
        return repository.getNewsById(newsId)
    }

    fun getNewsItemById(newsId: Long) : LiveData<List<NewsItem>> {
        return repository.getNewsItemById(newsId)
    }

}