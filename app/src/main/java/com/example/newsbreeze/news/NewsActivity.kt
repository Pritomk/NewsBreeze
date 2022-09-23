package com.example.newsbreeze.news

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
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
import com.example.newsbreeze.newsList.NewsListViewHolder
import com.example.newsbreeze.room.News
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
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
    private lateinit var newsPic: Bitmap
    private lateinit var newsPicUrl: String
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        Initialize the binding
        binding = ActivityNewsBinding.inflate(layoutInflater)
        //link the xml file with the backend file
        setContentView(binding.root)

        //Initialize the view model for fetch the data
        newsActivityViewModel = ViewModelProvider(this)[NewsActivityViewModel::class.java]

        //Call the data setting function
        getAndSetNewsData()

        //Add the listener for news save button
        binding.newsSaveBtn.setOnClickListener {
            saveButtonFunc()
        }

        //Add the listener for news back button
        binding.newsBackBtn.setOnClickListener {
            onBackPressed()
        }

    }

    //Function to change the font in page
    private fun changeFont() {
        //Call Queens park and Queens park bold font from asset folder and save into a typeface
        val qPTypeFace = Typeface.createFromAsset(assets,"QueensPark.TTF" )
        val qPBTypeFace = Typeface.createFromAsset(assets,"QueensParkBold.TTF" )

        //Change the textView's type face according to the design
        binding.newsTitle.typeface = qPBTypeFace
        binding.newsContent.typeface = qPTypeFace
        binding.newsDescription.typeface = qPTypeFace
    }


    //Function to set the data in page
    @SuppressLint("ResourceAsColor")
    private fun getAndSetNewsData() {
        //Call intent to get the news id from SaveListActivity and NewsListActivity
        newsId = intent.getLongExtra("newsId", 0)
        //Get the boolean value indicate that news id coming from where
        val newsListActivity = intent.getBooleanExtra("newsList", false)

        /*Call set details according to newsId but at a time one of the function will use*/
        if (newsListActivity){
            setDetailFromNewsList()
        } else {
            setDetailsFromSaveList()
        }

        /*Set the listener to after clicking on flag save news will delete from the database*/
        binding.newsFlagSave.setOnClickListener {
            deleteNewsFunc()
        }
    }

    /*Function to set the details in page which are coming from save list activity*/
    private fun setDetailsFromSaveList() {
        //Fetch the livedata of news list with a observer
        newsActivityViewModel.getNewsById(newsId).observe(this) { newsList ->
            //If some data is coming from news table database
            if (newsList.isNotEmpty()) {
                val news = newsList[0]
                //Set the global variable
                title = news.title
                author = news.author
                description = news.description
                content = news.content
                date = news.date
                flagSave = news.flagSave
                newsPic = news.newsPic!!
                //Set the details in page
                binding.newsTitle.text = news.title
                binding.newsAuthorName.text = news.author
                binding.newsContent.text = news.content
                binding.newsDescription.text = news.description
                binding.newsDate.text = news.date
                /*Set the news pic with center crop property which allows to set fullscreen image with bitmap*/
                Glide.with(this).load(news.newsPic).centerCrop().into(binding.newsImg)
            }
            //Call update flag function
            updateFlagSave()
            //Call update flag function
            changeFont()
        }
    }

    /*Function to set the details in page which are coming from save list activity*/
    private fun setDetailFromNewsList() {
        //If some data is coming from news item table database
        newsActivityViewModel.getNewsItemById(newsId).observe(this) { newsList ->
            if (newsList.isNotEmpty()) {
                val news = newsList[0]
                //Set the global variable
                title = news.title
                author = news.author
                description = news.description
                content = news.content
                date = news.date
                flagSave = news.flagSave
                newsPicUrl = news.newsPicUrl
                //Set the details in page
                binding.newsTitle.text = news.title
                binding.newsAuthorName.text = news.author
                binding.newsContent.text = news.content
                binding.newsDescription.text = news.description
                binding.newsDate.text = news.date
                /*Set the news pic with center crop property which allows to set fullscreen image with pic url*/
                Glide.with(this).load(news.newsPicUrl).centerCrop().into(binding.newsImg)
            }
            //Call update flag function
            updateFlagSave()
            //Call update flag function
            changeFont()
        }

    }

    //Delete function to delete the news with title and date
    private fun deleteNewsFunc() {
        //Delete the news according to the title and date
        newsActivityViewModel.deleteNews(title, date)
        //Change flag save variable with false value which indicate news has not saved
        flagSave = false

        updateFlagSave()
    }
    /*Save button functionality and save news in news table*/
    private fun saveButtonFunc() {
        //Change the flag save with true value which indicate news has saved
        flagSave = true
        updateFlagSave()
        //Launch global scope coroutine
        GlobalScope.launch {
            /*Take either of the url or bitmap*/
            val newsBitmap = if (newsPicUrl.isNotEmpty()) getByteArrayImage(newsPicUrl) else newsPic
            val byteArray = fromBitmap(newsBitmap)
            /*Image reduced function to save the bitmap*/
            val reducedImage = imageSizeReduce(byteArray)
            val bitmap = toBitmap(reducedImage)
            //Make a news model with global variable and give the flag save value as true
            val news = News(
                0,
                title,
                author,
                bitmap,
                description,
                content,
                getTodayDate(),
                getCurrentTime(),
                true
            )
            //Insert new news in news_table database
            newsActivityViewModel.insertNews(news)
        }
    }


    //Function to get the current time as 11:02 am
    private fun getCurrentTime() : String {
        val df: DateFormat = SimpleDateFormat("h:mm a")
        val date: String = df.format(Calendar.getInstance().time)
        return date
    }

    //Function to get the current date as 23-09-2022
    private fun getTodayDate() : String {
        val c: Date = Calendar.getInstance().time
        val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate: String = df.format(c)
        return formattedDate
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


    /*Function to update the flag according the save flag*/
    private fun updateFlagSave() {
        if (flagSave) {
            Glide.with(this).load(R.drawable.ic_save).into(binding.newsFlagSave)
        } else {
            Glide.with(this).load(R.drawable.ic_unsave).into(binding.newsFlagSave)
        }
    }



}