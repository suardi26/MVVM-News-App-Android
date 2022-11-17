package com.practice.mvvm_news_app_android.repository


import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.practice.mvvm_news_app_android.api.NewsApi
import com.practice.mvvm_news_app_android.model.Article
import com.practice.mvvm_news_app_android.model.NewsResponse
import com.practice.mvvm_news_app_android.utils.Constants
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class NewsPagingSource @Inject constructor(
    private val newsApi: NewsApi,
    private val query: String?,
    private val countryCode: String?,
) : PagingSource<Int, Article>() {

    companion object{
        private const val STARTING_PAGE_INDEX = 1
    }


    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        return try {

            val position = params.key ?: STARTING_PAGE_INDEX
            val response: Response<NewsResponse> =
                if(query == null && countryCode != null){
                    newsApi.getBreakingNews(
                        countryCode = countryCode,
                        pageNumber = position,
                        apiKey = Constants.API_KEY
                    )
                }else{
                    newsApi.searchForNews(
                        searchQuery = query!!,
                        pageNumber = position,
                        apiKey = Constants.API_KEY
                    )
                }

            val news = response.body()?.articles ?: emptyList()
            Log.v("Paging",position.toString())

            LoadResult.Page(
                data = news,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (news.isEmpty()) null else position + 1
            )

        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}