package com.practice.mvvm_news_app_android.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.practice.mvvm_news_app_android.model.Article


@Dao
interface ArticleDao{

    // Update Or Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    // Show Articles
    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    // Delete
    @Delete
    suspend fun deleteArticle(article: Article)

}