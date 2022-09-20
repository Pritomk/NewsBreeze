package com.example.newsbreeze.saveNews

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.newsbreeze.repository.NewsRepository
import com.example.newsbreeze.room.News
import com.example.newsbreeze.room.NewsDatabase
import com.example.newsbreeze.room.NewsItemDatabase

class SaveNewsActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val newsDao = NewsDatabase.getDatabase(application).newsDao()
    private val newsItemDao = NewsItemDatabase.getDatabase(application).newsItemDao()
    private val repository = NewsRepository(newsDao, newsItemDao)

    val readAllNews: LiveData<List<News>> = repository.readNews

    fun todayNews(date: String): LiveData<List<News>> {
        return repository.todayNews(date)
    }
}