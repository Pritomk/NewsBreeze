package com.example.newsbreeze.newsList

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.newsbreeze.databinding.ActivityNewsListBinding
import com.example.newsbreeze.news.NewsActivity
import com.example.newsbreeze.room.News
import com.example.newsbreeze.room.NewsItem
import com.example.newsbreeze.saveNews.SaveNewsActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class NewsListActivity : AppCompatActivity(), OnClickedListener {

    private lateinit var binding: ActivityNewsListBinding
    private lateinit var newsListActivityViewModel: NewsListActivityViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsListAdapter
    private val categories = arrayOf("business", "technology", "sports")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newsListActivityViewModel = ViewModelProvider(this)[NewsListActivityViewModel::class.java]

        newsListActivityViewModel.readBreakingNews(this, categories[0])

        setupRecyclerView()

        binding.newsListSavedBtn.setOnClickListener {
            startActivity(Intent(this, SaveNewsActivity::class.java))
        }

        binding.newsSearchView.setOnQueryTextListener(searchListener)

        binding.filterBtn.setOnClickListener {
            showAlertDialog()
        }
    }

    private fun setupRecyclerView() {
        recyclerView = binding.newsListRecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        adapter = NewsListAdapter(this)
        recyclerView.adapter = adapter
        newsListActivityViewModel.breakingNewsItemList.observe(this) { newsList ->
            for (newsItem in newsList) {
                newsListActivityViewModel.insertNewsItem(newsItem)
            }
        }

        newsListActivityViewModel.readNewsItem.observe(this) {
            adapter.updateList(it)
        }
    }

    override fun readButtonClicked(newsItem: NewsItem) {
        val newsIntent = Intent(this, NewsActivity::class.java)
        newsIntent.putExtra("newsId", newsItem.id)
        startActivity(newsIntent)
    }

    override fun saveButtonClicked(newsItem: NewsItem, position: Int) {
        insertNews(newsItem, position)
    }

    override fun flagSaveButtonClicked(newsItem: NewsItem, position: Int) {
        insertNews(newsItem, position)
    }

    override fun flagDeleteButtonClicked(newsItem: NewsItem, position: Int) {
        newsListActivityViewModel.deleteNews(newsItem.title, newsItem.date)
    }

    private fun insertNews(newsItem: NewsItem, position: Int) {

        newsItem.flagSave = true
        adapter.notifyItemChanged(position)

        GlobalScope.launch {
            val news = News(
                0,
                newsItem.title,
                newsItem.author,
                getByteArrayImage(newsItem.newsPicUrl),
                newsItem.description,
                newsItem.content,
                getTodayDate(),
                getCurrentTime(),
                true
            )
            newsListActivityViewModel.insertNews(news)
        }
    }

    private suspend fun getByteArrayImage(imageUrl: String): Bitmap {
        val loading = ImageLoader(this)
        val request = ImageRequest.Builder(this)
            .data(imageUrl)
            .build()

        val result = (loading.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap

    }

    private fun getTodayDate(): String {
        val c: Date = Calendar.getInstance().time
        val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate: String = df.format(c)
        return formattedDate
    }

    private fun getCurrentTime(): String {
        val df: DateFormat = SimpleDateFormat("h:mm a")
        val date: String = df.format(Calendar.getInstance().time)
        return date
    }

    private val searchListener: SearchView.OnQueryTextListener =
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                adapter.filter.filter(p0)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter.filter.filter(p0)
                return false
            }

        }

    private fun showAlertDialog() {
        val alertDialog = AlertDialog.Builder(this);
        alertDialog.setTitle("AlertDialog");
        var checkedItem = 0
        alertDialog.setSingleChoiceItems(
            categories, checkedItem
        ) { _, which ->
            when (which) {
                0 -> {
                    newsListActivityViewModel.readBreakingNews(this, categories[0])
                    checkedItem = 0
                }
                1 -> {
                    newsListActivityViewModel.readBreakingNews(this, categories[1])
                    checkedItem = 1
                }
                2 -> {
                    newsListActivityViewModel.readBreakingNews(this, categories[2])
                    checkedItem = 2
                }
            }
        }
        val alert = alertDialog.create()
        alert.setCanceledOnTouchOutside(true)
        alert.show()
    }


}