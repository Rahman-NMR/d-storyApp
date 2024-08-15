package com.rahman.storyapp.view.viewmodel.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rahman.storyapp.data.database.StoryEntity
import com.rahman.storyapp.data.repository.UserRepository

class StoriesViewModel(repository: UserRepository) : ViewModel() {
    val stories: LiveData<PagingData<StoryEntity>> = repository.getStories().cachedIn(viewModelScope)
}