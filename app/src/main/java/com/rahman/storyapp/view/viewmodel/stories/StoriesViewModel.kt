package com.rahman.storyapp.view.viewmodel.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rahman.storyapp.data.remote.response.ListStoryItem
import com.rahman.storyapp.data.repository.UserRepository

class StoriesViewModel(repository: UserRepository) : ViewModel() {
    val stories: LiveData<PagingData<ListStoryItem>> = repository.getStories().cachedIn(viewModelScope)
}