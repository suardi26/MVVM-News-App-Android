package com.practice.mvvm_news_app_android.di

import android.content.Context
import androidx.room.Room
import com.practice.mvvm_news_app_android.api.NewsApi
import com.practice.mvvm_news_app_android.db.ArticleDao
import com.practice.mvvm_news_app_android.db.ArticleDatabase
import com.practice.mvvm_news_app_android.repository.NewsRepository
import com.practice.mvvm_news_app_android.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuleApp {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

    }

    @Provides
    @Singleton
    fun provideOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsApi(retrofit: Retrofit): NewsApi {
        return retrofit.create(NewsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideArticleDatabase(
        @ApplicationContext app: Context
    ): ArticleDatabase{
        return Room.databaseBuilder(
            app,
            ArticleDatabase::class.java,
            "article_db.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideArticleDao(
        articleDatabase: ArticleDatabase
    ): ArticleDao{
        return articleDatabase.getArticleDao()
    }

    @Provides
    @Singleton
    fun provideNewsRepository(
        newsApi: NewsApi,
        articleDao: ArticleDao,
    ): NewsRepository{
        return NewsRepository(newsApi, articleDao)
    }
}