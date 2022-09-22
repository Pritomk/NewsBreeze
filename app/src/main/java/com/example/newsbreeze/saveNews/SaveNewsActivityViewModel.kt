package com.example.newsbreeze.saveNews

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.newsbreeze.repository.NewsRepository
import com.example.newsbreeze.room.News
import com.example.newsbreeze.room.NewsDatabase
import com.example.newsbreeze.room.NewsItemDatabase

class SaveNewsActivityViewModel(application: Application) : AndroidViewModel(application) {

    //Initialize the news database news dao
    private val newsDao = NewsDatabase.getDatabase(application).newsDao()
    //Initialize the news database news dao
    private val newsItemDao = NewsItemDatabase.getDatabase(application).newsItemDao()
    //Initialize a repository variable with newsdao and newsitemdao
    private val repository = NewsRepository(newsDao, newsItemDao)

    //Get the read news item list
    val readAllNews: LiveData<List<News>> = repository.readNews

    //Get only todays news
    fun todayNews(date: String): LiveData<List<News>> {
        return repository.todayNews(date)
    }
}