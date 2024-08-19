package com.rahman.storyapp.view.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rahman.storyapp.data.repository.UserRepository
import com.rahman.storyapp.di.Injection
import com.rahman.storyapp.view.viewmodel.stories.AddStoryViewModel
import com.rahman.storyapp.view.viewmodel.stories.StoriesViewModel
import com.rahman.storyapp.view.viewmodel.stories.StoriesLocationViewModel
import com.rahman.storyapp.view.viewmodel.user.LoginViewModel
import com.rahman.storyapp.view.viewmodel.user.RegisterViewModel
import com.rahman.storyapp.view.viewmodel.user.UserViewModel

class ViewModelFactory private constructor(private val repository: UserRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(repository) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(repository) as T
            modelClass.isAssignableFrom(UserViewModel::class.java) -> UserViewModel(repository) as T
            modelClass.isAssignableFrom(StoriesViewModel::class.java) -> StoriesViewModel(repository) as T
            modelClass.isAssignableFrom(StoriesLocationViewModel::class.java) -> StoriesLocationViewModel(repository) as T
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> AddStoryViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        fun getInstance(context: Context) = ViewModelFactory(Injection.provideRepository(context))
    }
}