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

    //Initialize the news database news dao
    private val newsDao = NewsDatabase.getDatabase(application).newsDao()
    //Initialize the news database news dao
    private val newsItemDao = NewsItemDatabase.getDatabase(application).newsItemDao()
    //Initialize a repository variable with newsdao and newsitemdao
    private val repository = NewsRepository(newsDao,newsItemDao)

    //Insert news in news table function
    fun insertNews(news: News) {
        //Call a viewmodelscope coroutine to call suspend function
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertNews(news)
        }
    }

    //Delete news from news table function
    fun deleteNews(title: String, date: String) {
        //Call a viewmodelscope coroutine to call suspend function
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNews(title,date)
        }
    }

    //Get the news by newsid from news table
    fun getNewsById(newsId: Long) : LiveData<List<News>> {
        return repository.getNewsById(newsId)
    }

    //Get the news item by newsid from news item table
    fun getNewsItemById(newsId: Long) : LiveData<List<NewsItem>> {
        return repository.getNewsItemById(newsId)
    }

}