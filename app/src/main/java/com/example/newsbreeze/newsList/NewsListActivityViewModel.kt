package com.example.newsbreeze.newsList

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

class NewsListActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val newsDao = NewsDatabase.getDatabase(application).newsDao()
    private val newsItemDao = NewsItemDatabase.getDatabase(application).newsItemDao()
    private val repository = NewsRepository(newsDao, newsItemDao)

    val readNews: LiveData<List<News>> = repository.readNews
    val readNewsItem: LiveData<List<NewsItem>> = repository.readNewsItem
    val breakingNewsItemList: LiveData<List<NewsItem>> = repository.breakingNewsItemList

    fun insertNews(news: News) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertNews(news)
        }
    }

    fun deleteNews(title: String, date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNews(title, date)
        }
    }

    fun readBreakingNews(context: Context, category: String) {
        repository.readingBreakingNews(context, category)
    }

    fun insertNewsItem(newsItem: NewsItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertNewsItem(newsItem)
        }
    }

}