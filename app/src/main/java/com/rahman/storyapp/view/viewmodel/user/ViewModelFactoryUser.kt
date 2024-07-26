package com.rahman.storyapp.view.viewmodel.user

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rahman.storyapp.data.repository.UserRepository
import com.rahman.storyapp.di.Injection

class ViewModelFactoryUser private constructor(private val userRepository: UserRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(userRepository) as T
        }
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(userRepository) as T
        }
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactoryUser? = null
        fun getInstance(context: Context): ViewModelFactoryUser =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactoryUser(Injection.provideRepository(context))
            }.also { instance = it }
    }
}