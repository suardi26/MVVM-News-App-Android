package com.practice.mvvm_news_app_android.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.practice.mvvm_news_app_android.R
import com.practice.mvvm_news_app_android.databinding.FragmentArticleBinding
import com.practice.mvvm_news_app_android.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleFragment : Fragment(R.layout.fragment_article) {
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewsViewModel by viewModels()
    private val args: ArticleFragmentArgs by navArgs<ArticleFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentArticleBinding.bind(view)

        val article = args.article
        Log.v("Check", article.toString())
        binding.webView.apply {
            webViewClient = WebViewClient()
            article.url?.let {  loadUrl(article.url)}
        }

        binding.fab.setOnClickListener {
            viewModel.savedArticle(article)
            Snackbar.make(view,"Article saved Successfully", Snackbar.LENGTH_SHORT).show()
        }
    }
}