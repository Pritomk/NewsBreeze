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

    //Initialize the news database news dao
    private val newsDao = NewsDatabase.getDatabase(application).newsDao()
    //Initialize the news database news dao
    private val newsItemDao = NewsItemDatabase.getDatabase(application).newsItemDao()
    //Initialize a repository variable with newsdao and newsitemdao
    private val repository = NewsRepository(newsDao, newsItemDao)

    val readNews: LiveData<List<News>> = repository.readNews
    //Get the read news item list
    val readNewsItem: LiveData<List<NewsItem>> = repository.readNewsItem
    //Get the news item list
    val breakingNewsItemList: LiveData<List<NewsItem>> = repository.breakingNewsItemList

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
            repository.deleteNews(title, date)
        }
    }

    //Call reading breaking news
    fun readBreakingNews(context: Context, category: String) {
        repository.readingBreakingNews(context, category)
    }

    //Insert news item
    fun insertNewsItem(newsItem: NewsItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertNewsItem(newsItem)
        }
    }

}