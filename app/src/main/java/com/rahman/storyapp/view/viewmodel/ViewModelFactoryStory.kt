package com.rahman.storyapp.view.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rahman.storyapp.data.repository.StoryRepository
import com.rahman.storyapp.di.Injection

class ViewModelFactoryStory private constructor(private val storyRepository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoriesViewModel::class.java)) {
            return StoriesViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactoryStory? = null
        fun getInstance(context: Context): ViewModelFactoryStory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactoryStory(Injection.provideStoryRepository(context))
            }.also { instance = it }
    }
}