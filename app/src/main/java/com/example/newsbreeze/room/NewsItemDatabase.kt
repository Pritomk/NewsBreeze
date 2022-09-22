package com.example.newsbreeze.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsbreeze.dao.NewsItemDao

//Database with news class entities with version 1
@Database(entities = [NewsItem::class], version = 1, exportSchema = false)
//Use custom converters class
@TypeConverters(Converters::class)
abstract class NewsItemDatabase : RoomDatabase() {

    //Take a news dao abstract fun
    abstract fun newsItemDao(): NewsItemDao

    /*
    Companion object with news database instance
    if Instance has already initialized then we don't need to
    initialize it again
    */
    companion object {
        @Volatile
        private var INSTANCE: NewsItemDatabase? = null

        fun getDatabase(context: Context): NewsItemDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        //Build database function build the database
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                NewsItemDatabase::class.java, "news_item_database"
            ).build()
    }

}