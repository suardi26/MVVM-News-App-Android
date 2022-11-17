package com.practice.mvvm_news_app_android.model

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)