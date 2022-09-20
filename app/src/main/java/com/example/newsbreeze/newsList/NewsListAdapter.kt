package com.example.newsbreeze.newsList

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsbreeze.R
import com.example.newsbreeze.room.News
import com.example.newsbreeze.room.NewsItem

class NewsListAdapter(private val listener: OnClickedListener) : RecyclerView.Adapter<NewsListViewHolder>(),Filterable {

    private val newsItemList = ArrayList<NewsItem>()
    private val newsAllItemList = ArrayList<NewsItem>()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        val viewHolder = NewsListViewHolder(view)
        context = parent.context
        viewHolder.readBtn.setOnClickListener {
            listener.readButtonClicked(newsItemList[viewHolder.position])
        }

        viewHolder.saveBtn.setOnClickListener {
            listener.saveButtonClicked(newsItemList[viewHolder.position], viewHolder.position)
        }

        viewHolder.flagImg.setOnClickListener {
            if (newsItemList[viewHolder.position].flagSave == true) {
                listener.flagSaveButtonClicked(
                    newsItemList[viewHolder.position],
                    viewHolder.position
                )
            } else {
                listener.flagDeleteButtonClicked(
                    newsItemList[viewHolder.position],
                    viewHolder.position
                )
            }
        }

        return viewHolder
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: NewsListViewHolder, position: Int) {
        val newsItem = newsItemList[position]
        Glide.with(context).load(newsItem.newsPicUrl).centerCrop().into(holder.image)
        holder.title.text = newsItem.title
        holder.description.text = newsItem.description
        holder.date.text = newsItem.date
        holder.time.text = newsItem.time

        if (newsItem.flagSave) {
            Glide.with(context).load(R.drawable.ic_save).into(holder.flagImg)
        } else {
            Glide.with(context).load(R.drawable.ic_unsave).into(holder.flagImg)
        }
    }

    override fun getItemCount(): Int {
        return newsItemList.size
    }

    fun updateList(newList: List<NewsItem>) {
        newsItemList.clear()
        newsItemList.addAll(newList)

        newsAllItemList.clear()
        newsAllItemList.addAll(newList)

        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return filterMethod
    }

    private val filterMethod = object : Filter() {
        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            val filteredList: ArrayList<NewsItem> = ArrayList()
            if (charSequence!!.isEmpty()) {
                filteredList.addAll(newsItemList)
            } else {
                for (item in newsAllItemList) {
                    if (item.title.lowercase().contains(charSequence.toString().lowercase())) {
                        filteredList.add(item)
                    }
                }
            }

            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
            newsItemList.clear()
            val list = filterResults?.values as? Collection<*>

            list?.let {
                newsItemList.addAll(it as Collection<NewsItem>)
            }

            Log.d("tag","${newsItemList.size}")

            notifyDataSetChanged()
        }

    }
}

class NewsListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    val image: ImageView = itemView.findViewById(R.id.news_item_img)
    val title: TextView = itemView.findViewById(R.id.news_item_title)
    val description: TextView = itemView.findViewById(R.id.news_item_desc)
    val date: TextView = itemView.findViewById(R.id.news_item_date)
    val saveBtn: TextView = itemView.findViewById(R.id.news_item_save_btn)
    val readBtn: TextView = itemView.findViewById(R.id.news_item_read_btn)
    val flagImg: ImageView = itemView.findViewById(R.id.news_item_flag_save)
    val time: TextView = itemView.findViewById(R.id.news_item_time)
}

interface OnClickedListener {
    fun readButtonClicked(newsItem: NewsItem)
    fun saveButtonClicked(newsItem: NewsItem,position: Int)
    fun flagSaveButtonClicked(newsItem: NewsItem,position: Int)
    fun flagDeleteButtonClicked(newsItem: NewsItem,position: Int)
}