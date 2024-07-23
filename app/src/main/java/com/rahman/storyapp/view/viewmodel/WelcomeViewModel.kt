package com.rahman.storyapp.view.viewmodel

import androidx.lifecycle.ViewModel
import com.rahman.storyapp.data.repository.UserRepository

class WelcomeViewModel(private val userRepository: UserRepository) : ViewModel() {
    suspend fun isAvailable(): Boolean {
        val uid = userRepository.getUserId()
        return uid?.isNotEmpty() ?: false
    }
}