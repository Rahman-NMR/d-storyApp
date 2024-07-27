package com.rahman.storyapp.view.viewmodel.user

import androidx.lifecycle.ViewModel
import com.rahman.storyapp.data.repository.UserRepository

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    suspend fun isAvailable(): Boolean {
        val token = userRepository.getTokenUser()
        return token?.isNotEmpty() ?: false
    }

    suspend fun logout() {
        userRepository.logout()
    }
}