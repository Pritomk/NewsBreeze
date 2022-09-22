package com.example.newsbreeze.saveNews

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsbreeze.R
import com.example.newsbreeze.newsList.NewsListViewHolder
import com.example.newsbreeze.room.News
import com.example.newsbreeze.room.NewsItem

class SaveListAdapter(private val listener: OnClickListener) : RecyclerView.Adapter<SaveListViewHolder>(),Filterable {
    //News item list for previous value
    private val newsList = ArrayList<News>()
    //News item list for updated value
    private val newsAllList = ArrayList<News>()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaveListViewHolder {
        //Call view with save news item layout
        val view = LayoutInflater.from(parent.context).inflate(R.layout.save_list_item_layout,parent, false)
        val viewHolder = SaveListViewHolder(view)
        context = parent.context
        view.setOnClickListener {
            listener.onItemClicked(newsList[viewHolder.position])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: SaveListViewHolder, position: Int) {
        // Set the value of recycler view items
        val news = newsList[position]
        Glide.with(context).load(news.newsPic).centerCrop().into(holder.listImg)
        holder.title.text = news.title
        holder.date.text = news.date
        // Set the animation of recycler view
        holder.saveListItemContainer.animation =
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim)
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

    //Function for update the recycler view list and notify the adapter
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
    val saveListItemContainer: RelativeLayout = itemView.findViewById(R.id.save_list_item_container)
}

interface OnClickListener {
    fun onItemClicked(news: News)
}