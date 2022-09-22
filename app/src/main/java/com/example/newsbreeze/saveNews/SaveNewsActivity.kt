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

        //        Initialize the binding
        binding = ActivitySaveNewsBinding.inflate(layoutInflater)
        //link the xml file with the backend file
        setContentView(binding.root)

        //Initialize the view model for fetch the data
        saveNewsActivityViewModel = ViewModelProvider(this)[SaveNewsActivityViewModel::class.java]

        //Initialize the recyclerview
        saveListRecylerView = binding.saveListRecyclerview
        //Initialize adapter
        adapter = SaveListAdapter(this)
        //Set layout manager of recycler view
        saveListRecylerView.layoutManager = LinearLayoutManager(this)
        //set adapter of recyclerview
        saveListRecylerView.adapter = adapter


        setTodayRecyclerFunc()

        //Call see all function
        binding.saveListSeeAllBtn.setOnClickListener {
            seeAllNewsFunc()
        }
        //Back method
        binding.newsSaveBackBtn.setOnClickListener {
            onBackPressed()
        }

        //Set the search listener into search view
        binding.saveNewsSearchView.setOnQueryTextListener(searchListener)
    }

    private fun seeAllNewsFunc() {
        binding.todayText.text = "All News"
        //Get all the news from local database with see all
        saveNewsActivityViewModel.readAllNews.observe(this) {
            adapter.updateList(it as ArrayList<News>)
        }
    }

    private fun setTodayRecyclerFunc() {
        val todayDate = getTodayDate()
        //Get today news list
        saveNewsActivityViewModel.todayNews(todayDate).observe(this) {
            adapter.updateList(it as ArrayList<News>)
        }
    }

    //Start news activity with news id
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

    //Function to get the current date as 23-09-2022
    private fun getTodayDate() : String {
        val c: Date = Calendar.getInstance().time
        val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate: String = df.format(c)
        return formattedDate
    }

    //Search listener for search view
    private val searchListener: SearchView.OnQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(p0: String?): Boolean {
            adapter.filter.filter(p0)
            return false
        }

        override fun onQueryTextChange(p0: String?): Boolean {
            //Call adapter filter function with changed string
            adapter.filter.filter(p0)
            return false
        }

    }

}