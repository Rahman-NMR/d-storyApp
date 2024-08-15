package com.rahman.storyapp.view.ui.stories.adapterview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textview.MaterialTextView
import com.rahman.storyapp.R

class LoadingStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadingStateAdapter.LoadingViewHolder>() {
    class LoadingViewHolder(retry: () -> Unit, itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val progressBar = itemView.findViewById<CircularProgressIndicator>(R.id.paging_progress_bar)
        private val retryButton = itemView.findViewById<MaterialButton>(R.id.retry_button)
        private val errorMsg = itemView.findViewById<MaterialTextView>(R.id.error_msg)

        init {
            retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) errorMsg.text = loadState.error.localizedMessage?.substringAfter(": ") ?: "Unknown Error"
            progressBar.isVisible = loadState is LoadState.Loading
            retryButton.isVisible = loadState is LoadState.Error
            errorMsg.isVisible = loadState is LoadState.Error
        }
    }

    override fun onBindViewHolder(holder: LoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_loading_paging, parent, false)
        return LoadingViewHolder(retry, itemView)
    }
}