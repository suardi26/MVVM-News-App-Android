package com.practice.mvvm_news_app_android.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.practice.mvvm_news_app_android.model.Article
import com.practice.mvvm_news_app_android.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    fun getBreakingNews(countryCode: String): LiveData<PagingData<Article>> {
        return repository.getBreakingNews(countryCode).cachedIn(viewModelScope)
    }

    fun getSearchNews(query: String): LiveData<PagingData<Article>> {
        return repository.getSearchNews(query).cachedIn(viewModelScope)
    }

    fun savedArticle(article: Article): Job {
        return viewModelScope.launch {
            repository.upsert(article)
        }
    }

    fun getSavedNews(): LiveData<List<Article>> {
        return repository.getSavedNews()
    }

    fun deleteArticle(article: Article): Job {
        return viewModelScope.launch {
            repository.deleteArticle(article)
        }
    }
}