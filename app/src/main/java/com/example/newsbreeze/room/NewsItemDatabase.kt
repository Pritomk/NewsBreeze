package com.example.newsbreeze.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsbreeze.dao.NewsDao
import com.example.newsbreeze.dao.NewsItemDao

@Database(entities = [NewsItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NewsItemDatabase : RoomDatabase() {

    abstract fun newsItemDao() : NewsItemDao

    companion object {
        @Volatile
        private var INSTANCE: NewsItemDatabase? = null

        fun getDatabase(context: Context): NewsItemDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                NewsItemDatabase::class.java, "news_item_database"
            ).build()
    }

}