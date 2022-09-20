package com.example.newsbreeze.saveNews

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.newsbreeze.repository.NewsRepository
import com.example.newsbreeze.room.News
import com.example.newsbreeze.room.NewsDatabase

class SaveNewsActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = NewsDatabase.getDatabase(application).newsDao()
    private val repository = NewsRepository(dao)

    val readAllNews: LiveData<List<News>> = repository.readNews

    fun todayNews(date: String): LiveData<List<News>> {
        return repository.todayNews(date)
    }
}