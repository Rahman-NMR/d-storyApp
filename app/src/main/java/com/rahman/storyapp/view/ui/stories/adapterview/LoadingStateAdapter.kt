package com.rahman.storyapp.view.ui.stories.adapterview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rahman.storyapp.databinding.ItemLoadingPagingBinding

class LoadingStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadingStateAdapter.LoadingViewHolder>() {
    class LoadingViewHolder(retry: () -> Unit, private val binding: ItemLoadingPagingBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            with(binding) {
                if (loadState is LoadState.Error) errorMsg.text = loadState.error.localizedMessage?.substringAfter(": ") ?: "Unknown Error"
                pagingProgressBar.isVisible = loadState is LoadState.Loading
                retryButton.isVisible = loadState is LoadState.Error
                errorMsg.isVisible = loadState is LoadState.Error
            }
        }
    }

    override fun onBindViewHolder(holder: LoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingViewHolder {
        val binding = ItemLoadingPagingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingViewHolder(retry, binding)
    }
}