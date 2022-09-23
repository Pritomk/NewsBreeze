package com.example.newsbreeze.newsList

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsbreeze.R
import com.example.newsbreeze.room.NewsItem

class NewsListAdapter(private val listener: OnClickedListener) : RecyclerView.Adapter<NewsListViewHolder>(),Filterable {

    //News item list for previous value
    private val newsItemList = ArrayList<NewsItem>()
    //News item list for updated value
    private val newsAllItemList = ArrayList<NewsItem>()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsListViewHolder {
        //Call view with news item layout
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        val viewHolder = NewsListViewHolder(view)
        context = parent.context
        //Call interface read button method by news item
        viewHolder.readBtn.setOnClickListener {
            listener.readButtonClicked(newsItemList[viewHolder.position])
        }

        view.setOnClickListener {
            listener.readButtonClicked(newsItemList[viewHolder.position])
        }

        //Call interface save button method by news item
        viewHolder.saveBtn.setOnClickListener {
            listener.saveButtonClicked(newsItemList[viewHolder.position], viewHolder.position)
        }



        //Set the flag image
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

        // Set the animation of recycler view
        holder.newsItemContainer.animation =
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim)

        // Set the value of recycler view items
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

        changeFont(holder)
    }

    override fun getItemCount(): Int {
        return newsItemList.size
    }

    private fun changeFont(holder: NewsListViewHolder) {
        val qPTypeFace = Typeface.createFromAsset(context.assets,"QueensPark.TTF" )
        val qPBTypeFace = Typeface.createFromAsset(context.assets,"QueensParkBold.TTF" )
        holder.title.typeface = qPBTypeFace
        holder.description.typeface = qPTypeFace
    }

    //Function for update the recycler view list and notify the adapter
    fun updateList(newList: List<NewsItem>) {
        newsItemList.clear()
        newsItemList.addAll(newList)

        newsAllItemList.clear()
        newsAllItemList.addAll(newList)

        notifyDataSetChanged()
    }

    // Filter method give the result with filtering the title
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

            notifyDataSetChanged()
        }

    }
}

class NewsListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    val newsItemContainer : RelativeLayout = itemView.findViewById(R.id.news_item_container)
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