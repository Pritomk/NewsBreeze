package com.example.newsbreeze.saveNews

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsbreeze.databinding.ActivitySaveNewsBinding
import com.example.newsbreeze.news.NewsActivity
import com.example.newsbreeze.room.Converters
import com.example.newsbreeze.room.News
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SaveNewsActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding: ActivitySaveNewsBinding
    private lateinit var saveNewsActivityViewModel: SaveNewsActivityViewModel
    private lateinit var adapter: SaveListAdapter
    private lateinit var saveListRecylerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySaveNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        saveNewsActivityViewModel = ViewModelProvider(this)[SaveNewsActivityViewModel::class.java]

        saveListRecylerView = binding.saveListRecyclerview
        adapter = SaveListAdapter(this)
        saveListRecylerView.layoutManager = LinearLayoutManager(this)
        saveListRecylerView.adapter = adapter


        setTodayRecyclerFunc()

        binding.saveListSeeAllBtn.setOnClickListener {
            seeAllNewsFunc()
        }

        binding.newsSaveBackBtn.setOnClickListener {
            onBackPressed()
        }

        binding.saveNewsSearchView.setOnQueryTextListener(searchListener)
    }

    private fun seeAllNewsFunc() {
        binding.todayText.text = "All News"
        saveNewsActivityViewModel.readAllNews.observe(this) {
            adapter.updateList(it as ArrayList<News>)
        }
    }

    private fun setTodayRecyclerFunc() {
        val todayDate = getTodayDate()
        Log.d("tag","${getTodayDate()}")
        saveNewsActivityViewModel.todayNews(todayDate).observe(this) {
            Log.d("tag","$it")
            adapter.updateList(it as ArrayList<News>)
        }
    }

    override fun onItemClicked(news: News) {
        val newsIntent = Intent(this, NewsActivity::class.java)
        newsIntent.putExtra("newsId", news.newsId)
        startActivity(newsIntent)

    }

    private fun fromBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }


    private fun getTodayDate() : String {
        val c: Date = Calendar.getInstance().time
        val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate: String = df.format(c)
        return formattedDate
    }

    private val searchListener: SearchView.OnQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(p0: String?): Boolean {
            adapter.filter.filter(p0)
            return false
        }

        override fun onQueryTextChange(p0: String?): Boolean {
            Log.d("tag","$p0")
            adapter.filter.filter(p0)
            return false
        }

    }

}