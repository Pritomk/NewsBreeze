package com.example.newsbreeze.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsbreeze.dao.NewsDao

//Database with news class entities with version 1
@Database(entities = [News::class], version = 1, exportSchema = false)
//Use custom converters class
@TypeConverters(Converters::class)
abstract class NewsDatabase : RoomDatabase() {

    //Take a news dao abstract fun
    abstract fun newsDao() : NewsDao

    /*
    Companion object with news database instance
    if Instance has already initialized then we don't need to
    initialize it again
    */
    companion object {
        @Volatile
        private var INSTANCE: NewsDatabase? = null

        fun getDatabase(context: Context): NewsDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        //Build database function build the database
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                NewsDatabase::class.java, "news_database"
            ).build()
    }

}