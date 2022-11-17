package com.practice.mvvm_news_app_android.ui.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.practice.mvvm_news_app_android.R
import com.practice.mvvm_news_app_android.adapter.NewsAdapter
import com.practice.mvvm_news_app_android.adapter.NewsLoadStateAdapter
import com.practice.mvvm_news_app_android.databinding.FragmentSearchNewsBinding
import com.practice.mvvm_news_app_android.utils.Constants
import com.practice.mvvm_news_app_android.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    private var _binding: FragmentSearchNewsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewsViewModel by viewModels<NewsViewModel>()
    private val newsAdapter: NewsAdapter = NewsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchNewsBinding.bind(view)

        setupRecyclerView()
        newsAdapter.addLoadStateListener { loadState ->
            if (hasInternetConnection()) {
                binding.apply {
                    when (val currentState = loadState.source.refresh) {

                        is LoadState.Loading -> {
                            paginationProgressBar.visibility = View.VISIBLE
                            recyclerViewSearchNews.visibility = View.GONE
                            hideComponentError()
                        }

                        is LoadState.Error -> {

                            when (currentState.error) {
                                is IOException -> {
                                    textViewNotFound.text =
                                        getString(R.string.movie_not_found, "Network Failure")
                                }
                                else -> {
                                    textViewNotFound.text =
                                        getString(R.string.movie_not_found, "Conversion Error")
                                }
                            }

                            showComponentError()
                            paginationProgressBar.visibility = View.GONE
                        }

                        is LoadState.NotLoading -> {

                            paginationProgressBar.visibility = View.GONE

                            if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && newsAdapter.itemCount < 1) {
                                recyclerViewSearchNews.visibility = View.GONE
                                textViewNotFound.text =
                                    getString(R.string.movie_not_found, "Article Not Found !!!")
                                showComponentError()
                            } else {
                                hideComponentError()
                                recyclerViewSearchNews.visibility = View.VISIBLE

                            }

                        }
                    }


                }
            } else {
                binding.apply {
                    textViewNotFound.text =
                        getString(R.string.movie_not_found, "Not Internet Connection")
                }
            }
        }

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }

            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

        var job: Job? = null
        binding.editTextSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.getSearchNews(editable.toString()).observe(viewLifecycleOwner) {
                            newsAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                        }
                    }
                }
            }
        }

    }

    private fun setupRecyclerView() {
        binding.apply {
            recyclerViewSearchNews.setHasFixedSize(true)
            recyclerViewSearchNews.layoutManager = LinearLayoutManager(activity)
            recyclerViewSearchNews.adapter = newsAdapter.withLoadStateHeaderAndFooter(
                header = NewsLoadStateAdapter { newsAdapter.retry() },
                footer = NewsLoadStateAdapter { newsAdapter.retry() }
            )
            buttonRetry.setOnClickListener {
                newsAdapter.retry()
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false

            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION") return networkInfo.isConnected
        }
    }

    private fun showComponentError() {
        binding.apply {
            textViewStatus.visibility = View.VISIBLE
            buttonRetry.visibility = View.VISIBLE
            textViewNotFound.visibility = View.VISIBLE
        }
    }

    private fun hideComponentError() {
        binding.apply {
            binding.apply {
                textViewStatus.visibility = View.GONE
                buttonRetry.visibility = View.GONE
                textViewNotFound.visibility = View.GONE
            }
        }
    }

}