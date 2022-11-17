package com.practice.mvvm_news_app_android.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.practice.mvvm_news_app_android.api.NewsApi
import com.practice.mvvm_news_app_android.db.ArticleDao
import com.practice.mvvm_news_app_android.model.Article
import com.practice.mvvm_news_app_android.utils.Constants

class NewsRepository(
    private val newsApi: NewsApi,
    private val articleDao: ArticleDao
) {
    fun getBreakingNews(countryCode: String?): LiveData<PagingData<Article>> {

        val newsPagingSourceGet = NewsPagingSource(newsApi, null, countryCode ?: "us")

        return Pager(
            config = PagingConfig(
                pageSize = 5,
                maxSize = Constants.QUERY_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { newsPagingSourceGet }
        ).liveData
    }

    fun getSearchNews(query: String?): LiveData<PagingData<Article>> {

        val newsPagingSourceSearch = NewsPagingSource(newsApi, query ?: "", null)

        return Pager(
            config = PagingConfig(
                pageSize = 5,
                maxSize = Constants.QUERY_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { newsPagingSourceSearch }
        ).liveData
    }

    fun getSavedNews(): LiveData<List<Article>>{
        return articleDao.getAllArticles()
    }

    suspend fun upsert(article: Article): Long {
        return articleDao.upsert(article)
    }

    suspend fun deleteArticle(article: Article){
        articleDao.deleteArticle(article)
    }





}