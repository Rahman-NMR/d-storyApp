package com.rahman.storyapp.view.viewmodel.user

import androidx.lifecycle.ViewModel
import com.rahman.storyapp.data.repository.UserRepository

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    suspend fun isAvailable(): Boolean {
        val uid = userRepository.getUserId()
        return uid?.isNotEmpty() ?: false
    }

    suspend fun logout() {
        userRepository.logout()
    }
}