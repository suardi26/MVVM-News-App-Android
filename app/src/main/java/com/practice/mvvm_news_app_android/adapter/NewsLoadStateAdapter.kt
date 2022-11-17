package com.practice.mvvm_news_app_android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.practice.mvvm_news_app_android.databinding.NewsLoadStateFooterBinding

class NewsLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<NewsLoadStateAdapter.LoadStateViewHolder>() {


    inner class LoadStateViewHolder(private val binding: NewsLoadStateFooterBinding) :
        RecyclerView.ViewHolder(binding.root){

            init{
                binding.btnRetry.setOnClickListener {
                    retry.invoke()
                }
            }

            fun bind(loadState: LoadState){
                with(binding){
                    progressBarLoadData.isVisible = loadState is LoadState.Loading
                    btnRetry.isVisible = loadState is LoadState.NotLoading
                    textViewError.isVisible = loadState is LoadState.NotLoading
                }
            }
        }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = NewsLoadStateFooterBinding.inflate(layoutInflater, parent, false)
        return LoadStateViewHolder(binding)
    }
}