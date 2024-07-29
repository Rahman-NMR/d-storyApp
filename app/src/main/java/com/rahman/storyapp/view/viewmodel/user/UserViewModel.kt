package com.rahman.storyapp.view.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahman.storyapp.data.local.UserModel
import com.rahman.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    suspend fun isLogin(): Boolean {
        val user = userRepository.getUser()
        return user.userId.isNotEmpty() && user.token.isNotEmpty()
    }

    suspend fun getUser(): UserModel {
        return userRepository.getUser()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}