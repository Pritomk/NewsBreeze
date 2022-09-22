package com.example.newsbreeze.newsList

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
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
import com.example.newsbreeze.room.Converters
import com.example.newsbreeze.room.News
import com.example.newsbreeze.room.NewsItem
import com.example.newsbreeze.saveNews.SaveNewsActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class NewsListActivity : AppCompatActivity(), OnClickedListener {

    private lateinit var binding: ActivityNewsListBinding
    private lateinit var newsListActivityViewModel: NewsListActivityViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsListAdapter
    private val categories = arrayOf("business", "technology", "sports")
    private var checkedItem: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //        Initialize the binding
        binding = ActivityNewsListBinding.inflate(layoutInflater)
        //link the xml file with the backend file
        setContentView(binding.root)

        //Initialize the view model for fetch the data
        newsListActivityViewModel = ViewModelProvider(this)[NewsListActivityViewModel::class.java]

        //Call the function which will provide the news with business category
        newsListActivityViewModel.readBreakingNews(this, categories[0])

        setupRecyclerView()

        //Save button redirect to save news activity
        binding.newsListSavedBtn.setOnClickListener {
            startActivity(Intent(this, SaveNewsActivity::class.java))
        }

        //Set the search listener into search view
        binding.newsSearchView.setOnQueryTextListener(searchListener)

        //Show a dialog to choose the category of news when filter button has been clicked
        binding.filterBtn.setOnClickListener {
            showAlertDialog()
        }

        changeFont()
    }

    //Change font function change the title text view font
    private fun changeFont() {
        val chomskyTypeFace = Typeface.createFromAsset(assets, "Chomsky.otf");
        binding.title.typeface = chomskyTypeFace
    }

    //Set up recycler view function
    private fun setupRecyclerView() {
        //Initialize the recyclerview
        recyclerView = binding.newsListRecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        //Set layout manager of recycler view
        recyclerView.layoutManager = linearLayoutManager
        //Initialize adapter
        adapter = NewsListAdapter(this)
        //set adapter of recyclerview
        recyclerView.adapter = adapter
        //Get the news item from online dao and insert into local database
        newsListActivityViewModel.breakingNewsItemList.observe(this) { newsList ->
            for (newsItem in newsList) {
                newsListActivityViewModel.insertNewsItem(newsItem)
            }
        }

        //Call local database to get the news item list and update the adapter
        newsListActivityViewModel.readNewsItem.observe(this) {
            adapter.updateList(it)
        }
    }

    //Redirect to news activity with news id
    override fun readButtonClicked(newsItem: NewsItem) {
        val newsIntent = Intent(this, NewsActivity::class.java)
        newsIntent.putExtra("newsId", newsItem.id)
        startActivity(newsIntent)
    }

    //Save button call insert news function
    override fun saveButtonClicked(newsItem: NewsItem, position: Int) {
        insertNews(newsItem, position)
    }

    //Flag save button call insert news function
    override fun flagSaveButtonClicked(newsItem: NewsItem, position: Int) {
        insertNews(newsItem, position)
    }

    // Delete button delete the news which title and date will be match
    override fun flagDeleteButtonClicked(newsItem: NewsItem, position: Int) {
        newsListActivityViewModel.deleteNews(newsItem.title, newsItem.date)
    }

    //Insert news method take news item and position from adapter
    private fun insertNews(newsItem: NewsItem, position: Int) {

        //Change news item flag as true
        newsItem.flagSave = true
        //notify adapter at particular position
        adapter.notifyItemChanged(position)

        //Launch a global scope coroutine
        GlobalScope.launch {

            //Get the bitmap from url and change to byte array
//            and reduce its size
            val bitmap = getByteArrayImage(newsItem.newsPicUrl)
            val byteArray = fromBitmap(bitmap)
            val reducedImage = imageSizeReduce(byteArray)

            //Make a news model with news item values
            val news = News(
                0,
                newsItem.title,
                newsItem.author,
                toBitmap(reducedImage),
                newsItem.description,
                newsItem.content,
                getTodayDate(),
                getCurrentTime(),
                true
            )
            //insert news into news table
            newsListActivityViewModel.insertNews(news)
        }
    }

    //Function to get byte array from image url
    private suspend fun getByteArrayImage(imageUrl: String): Bitmap {
        val loading = ImageLoader(this)
        //Make a Image request from from image url
        val request = ImageRequest.Builder(this)
            .data(imageUrl)
            .build()
        //Make a drawable resource
        val result = (loading.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }

    //Function to get the bytearray from bitmap
    fun fromBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        //Compress the image from output stream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    //Function to change the bytearray into a bitmap
    fun toBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    //Image reducing function
    private fun imageSizeReduce(img: ByteArray): ByteArray {
        var image = img
        //Take the image and compress it
        while (image.size > 500000) {
            val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
            //Resize the image
            val resized = Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * 0.8).toInt(),
                (bitmap.height * 0.8).toInt(),
                true
            )
            val stream = ByteArrayOutputStream()
            resized.compress(Bitmap.CompressFormat.PNG, 100, stream)
            image = stream.toByteArray()
        }
        return image
    }

    //Function to get the current date as 23-09-2022
    private fun getTodayDate(): String {
        val c: Date = Calendar.getInstance().time
        val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate: String = df.format(c)
        return formattedDate
    }

    //Function to get the current time as 11:02 am
    private fun getCurrentTime(): String {
        val df: DateFormat = SimpleDateFormat("h:mm a")
        val date: String = df.format(Calendar.getInstance().time)
        return date
    }

    //Search listener for search view
    private val searchListener: SearchView.OnQueryTextListener =
        object : SearchView.OnQueryTextListener {
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

    //Show category alert dialog
    private fun showAlertDialog() {
        //Make a alert dialog with alert dialog builder
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("AlertDialog")
        //call alert dialog with category array and checked item global variable
        alertDialog.setSingleChoiceItems(
            categories, checkedItem
        ) { _, which ->
            when (which) {
                0 -> {
                    //Call read breaking news with business category
                    newsListActivityViewModel.readBreakingNews(this, categories[0])
                    checkedItem = 0
                }
                1 -> {
                    //Call read breaking news with technology category
                    newsListActivityViewModel.readBreakingNews(this, categories[1])
                    checkedItem = 1
                }
                2 -> {
                    //Call read breaking news with sports category
                    newsListActivityViewModel.readBreakingNews(this, categories[2])
                    checkedItem = 2
                }
            }
        }
        val alert = alertDialog.create()
        alert.setCanceledOnTouchOutside(true)
        //Show the created dialog
        alert.show()
    }


}