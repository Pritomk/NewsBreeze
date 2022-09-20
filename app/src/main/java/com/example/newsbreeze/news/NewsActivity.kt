package com.example.newsbreeze.news

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.bumptech.glide.Glide
import com.example.newsbreeze.R
import com.example.newsbreeze.databinding.ActivityNewsBinding
import com.example.newsbreeze.room.News
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding
    private lateinit var newsActivityViewModel: NewsActivityViewModel
    private var newsId by Delegates.notNull<Long>()
    private var title: String = ""
    private var date: String = ""
    private lateinit var author: String
    private lateinit var content: String
    private lateinit var description: String
    private var flagSave = false
    private lateinit var newsPicUrl: String
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newsActivityViewModel = ViewModelProvider(this)[NewsActivityViewModel::class.java]

        getAndSetNewsData()

        binding.newsSaveBtn.setOnClickListener {
            saveButtonFunc()
        }

        binding.newsBackBtn.setOnClickListener {
            onBackPressed()
        }


    }


    @SuppressLint("ResourceAsColor")
    private fun getAndSetNewsData() {
        newsId = intent.getLongExtra("newsId", 0)

        setDetailsFromSaveList()
        setDetailFromNewsList()


        binding.newsFlagSave.setOnClickListener {
            deleteNewsFunc()
        }
    }

    private fun setDetailsFromSaveList() {
        newsActivityViewModel.getNewsById(newsId).observe(this) { newsList ->
            if (newsList.isNotEmpty()) {
                val news = newsList[0]
                title = news.title
                date = news.date
                flagSave = news.flagSave
                binding.newsTitle.text = news.title
                binding.newsAuthorName.text = news.author
                binding.newsContent.text = news.content
                binding.newsDescription.text = news.description
                binding.newsDate.text = news.date
                Glide.with(this).load(news.newsPic).centerCrop().into(binding.newsImg)
            }
            updateFlagSave()
        }
    }

    private fun setDetailFromNewsList() {
//        newsPicUrl = intent.getStringExtra("newsPicUrl").toString()
//        title = intent.getStringExtra("title").toString()
//        author = intent.getStringExtra("author").toString()
//        content = intent.getStringExtra("content").toString()
//        description = intent.getStringExtra("description").toString()
//        date = intent.getStringExtra("date").toString()
//        if (newsPicUrl.isNotEmpty()) {
//            Glide.with(this).load(newsPicUrl).centerCrop().into(binding.newsImg)
//        }
//        binding.newsTitle.text = title
//        binding.newsAuthorName.text = author
//        binding.newsContent.text = content
//        binding.newsDate.text = date
//        binding.newsContent.text = content
//        binding.newsDescription.text = description
        newsActivityViewModel.getNewsItemById(newsId).observe(this) { newsList ->
            if (newsList.isNotEmpty()) {
                val news = newsList[0]
                title = news.title
                date = news.date
                flagSave = news.flagSave
                binding.newsTitle.text = news.title
                binding.newsAuthorName.text = news.author
                binding.newsContent.text = news.content
                binding.newsDescription.text = news.description
                binding.newsDate.text = news.date
                Glide.with(this).load(news.newsPicUrl).centerCrop().into(binding.newsImg)
            }
            updateFlagSave()
        }

    }

    private fun deleteNewsFunc() {
        Log.d("title","$title $date")
        newsActivityViewModel.deleteNews(title, date)
        flagSave = false
        updateFlagSave()
    }

    private fun saveButtonFunc() {
        flagSave = true
        updateFlagSave()
        GlobalScope.launch {
            val news = News(
                0,
                title,
                author,
                getByteArrayImage(newsPicUrl),
                description,
                content,
                getTodayDate(),
                getCurrentTime(),
                true
            )
            newsActivityViewModel.insertNews(news)
        }

    }

    private fun getCurrentTime() : String {
        val df: DateFormat = SimpleDateFormat("h:mm a")
        val date: String = df.format(Calendar.getInstance().time)
        return date
    }


    private fun getTodayDate() : String {
        val c: Date = Calendar.getInstance().time
        val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate: String = df.format(c)
        return formattedDate
    }


    private suspend fun getByteArrayImage(imageUrl: String): Bitmap {
        val loading = ImageLoader(this)
        val request = ImageRequest.Builder(this)
            .data(imageUrl)
            .build()

        val result = (loading.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }

    private fun updateFlagSave() {
        if (flagSave) {
            Glide.with(this).load(R.drawable.ic_save).into(binding.newsFlagSave)
        } else {
            Glide.with(this).load(R.drawable.ic_unsave).into(binding.newsFlagSave)
        }
    }

    fun toBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }


}