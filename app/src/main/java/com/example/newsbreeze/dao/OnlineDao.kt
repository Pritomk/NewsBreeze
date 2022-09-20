package com.example.newsbreeze.dao

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.newsbreeze.room.News
import com.example.newsbreeze.room.NewsItem
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class OnlineDao {
    var newsItemList: MutableLiveData<List<NewsItem>> = MutableLiveData()

    fun readBreakingNews(context: Context, category: String) {
        val api = "https://newsapi.org/v2/top-headlines?country=us&category=$category&apiKey=3c2989530bd0446caca6df3a6518f448"
//        val api = "https://randomuser.me/api/"
        val body = HashMap<String, String>()
        body["apiKey"] = "3c2989530bd0446caca6df3a6518f448"
        val jsonObjectRequest = object : JsonObjectRequest(Method.GET,api,
            null,{ response ->
            try {
                val jsonArray = response.getJSONArray("articles")
                val itemList = ArrayList<NewsItem>()
                for (i in 0..jsonArray.length()) {
                    val jsonObject = jsonArray[i] as JSONObject
                    val newsItem = NewsItem(
                        jsonObject.getString("title"),
                        jsonObject.getString("author"),
                        jsonObject.getString("urlToImage"),
                        jsonObject.getString("description"),
                        jsonObject.getString("content"),
                        dateFormatChange(jsonObject.getString("publishedAt")),
                        timeFormatChange(jsonObject.getString("publishedAt")),
                        false)
                    itemList.add(newsItem)
                    newsItemList.value = itemList
                }
            } catch (error : JSONException) {
                error.printStackTrace()
            }
        },{
            Log.e("tag","${it.message}")
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String>? {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }

        }

        val socketTime = 3000
        val policy = DefaultRetryPolicy(
            socketTime,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        jsonObjectRequest.retryPolicy = policy

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(jsonObjectRequest)
    }

    fun dateFormatChange(dateString: String) : String {
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val output = SimpleDateFormat("dd/MM/yyyy")

        var d: Date? = null
        try {
            d = input.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val formatted: String = output.format(d)
        Log.i("DATE", "" + formatted)
        return formatted;
    }

    fun timeFormatChange(dateString: String) : String {
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val output = SimpleDateFormat("h:mm a")

        var d: Date? = null
        try {
            d = input.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val formatted: String = output.format(d)
        Log.i("TIME", "" + formatted)
        return formatted;
    }


}