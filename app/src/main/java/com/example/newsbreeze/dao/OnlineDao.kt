package com.example.newsbreeze.dao

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
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
    //Take a mutable livedata list which can take news item model
    var newsItemList: MutableLiveData<List<NewsItem>> = MutableLiveData()

    //Function to get the news file with corresponding category
    fun readBreakingNews(context: Context, category: String) {
        //News api url with api to get the url
        val api = "https://newsapi.org/v2/top-headlines?country=us&category=$category&apiKey=3c2989530bd0446caca6df3a6518f448"
        /*Body consist with apikey*/
        val body = HashMap<String, String>()
        body["apiKey"] = "3c2989530bd0446caca6df3a6518f448"
        //Make a GET json request to the api
        val jsonObjectRequest = object : JsonObjectRequest(Method.GET,api,
            null,{ response ->
            try {
                //Fetch the json array from response of the api
                val jsonArray = response.getJSONArray("articles")
                //Make a array to store the newsitem
                val itemList = ArrayList<NewsItem>()
                for (i in 0..jsonArray.length()) {
                    //Take the json object from json array
                    val jsonObject = jsonArray[i] as JSONObject
                    //Make NewsItem model from json object
                    val newsItem = NewsItem(
                        (i+1).toLong(),
                        jsonObject.getString("title"),
                        jsonObject.getString("author"),
                        jsonObject.getString("urlToImage"),
                        jsonObject.getString("description"),
                        jsonObject.getString("content"),
                        dateFormatChange(jsonObject.getString("publishedAt")),
                        timeFormatChange(jsonObject.getString("publishedAt")),
                        false)
                    itemList.add(newsItem)
                    /*Insert the value of arraylist into mutable livedata
                    When the data got some changes the observer notify the owner
                    of the livedata
                     */
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
        //Make retry policy 3000 mseconds
        val policy = DefaultRetryPolicy(
            socketTime,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        //Set the json object request retry policy
        jsonObjectRequest.retryPolicy = policy

        //Initialize a request volley request queue
        val requestQueue = Volley.newRequestQueue(context)
        //Add json object request into volley request queue
        requestQueue.add(jsonObjectRequest)
    }

    //Change the date format according to the design
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

    //Fetch the time format from input date
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