package com.rahman.storyapp.view.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rahman.storyapp.data.repository.UserRepository
import com.rahman.storyapp.di.Injection
import com.rahman.storyapp.view.viewmodel.stories.AddStoryViewModel
import com.rahman.storyapp.view.viewmodel.stories.StoriesViewModel
import com.rahman.storyapp.view.viewmodel.user.LoginViewModel
import com.rahman.storyapp.view.viewmodel.user.RegisterViewModel
import com.rahman.storyapp.view.viewmodel.user.UserViewModel

class ViewModelFactory private constructor(private val repository: UserRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(StoriesViewModel::class.java)) {
            return StoriesViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        fun getInstance(context: Context) = ViewModelFactory(Injection.provideRepository(context))
    }
}