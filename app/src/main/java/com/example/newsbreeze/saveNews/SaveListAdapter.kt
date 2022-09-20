package com.example.newsbreeze.saveNews

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
import com.example.newsbreeze.newsList.NewsListViewHolder
import com.example.newsbreeze.room.News
import com.example.newsbreeze.room.NewsItem

class SaveListAdapter(private val listener: OnClickListener) : RecyclerView.Adapter<SaveListViewHolder>(),Filterable {
    private val newsList = ArrayList<News>()
    private val newsAllList = ArrayList<News>()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaveListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.save_list_item_layout,parent, false)
        val viewHolder = SaveListViewHolder(view)
        context = parent.context
        view.setOnClickListener {
            listener.onItemClicked(newsList[viewHolder.position])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: SaveListViewHolder, position: Int) {
        val news = newsList[position]
        Glide.with(context).load(news.newsPic).centerCrop().into(holder.listImg)
        holder.title.text = news.title
        holder.date.text = news.date
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    fun updateList(newList: ArrayList<News>) {
        newsList.clear()
        newsList.addAll(newList)

        newsAllList.clear()
        newsAllList.addAll(newList)

        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return filterMethod
    }

    private val filterMethod = object : Filter() {
        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            val filteredList: ArrayList<News> = ArrayList()
            if (charSequence!!.isEmpty()) {
                filteredList.addAll(newsList)
            } else {
                for (item in newsAllList) {
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
            newsList.clear()
            val list = filterResults?.values as? Collection<*>

            list?.let {
                newsList.addAll(it as Collection<News>)
            }

            Log.d("tag","${newsList.size}")

            notifyDataSetChanged()
        }

    }


}

class SaveListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val listImg: ImageView = itemView.findViewById(R.id.save_list_img)
    val hashTag: TextView = itemView.findViewById(R.id.save_list_hash_tag)
    val title: TextView = itemView.findViewById(R.id.save_list_title)
    val date: TextView = itemView.findViewById(R.id.save_list_date)
}

interface OnClickListener {
    fun onItemClicked(news: News)
}