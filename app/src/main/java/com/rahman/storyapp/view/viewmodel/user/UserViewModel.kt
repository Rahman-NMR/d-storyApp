package com.rahman.storyapp.view.viewmodel.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahman.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    suspend fun isLogin(): Boolean {
        val user = userRepository.getUser()
        Log.e("testData user", "$user")
        return user.userId.isNotEmpty() && user.token.isNotEmpty()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}