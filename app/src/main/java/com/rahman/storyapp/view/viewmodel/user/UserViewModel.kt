package com.rahman.storyapp.view.viewmodel.user

import android.util.Log
import androidx.lifecycle.ViewModel
import com.rahman.storyapp.data.repository.UserRepository

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    suspend fun isLogin(): Boolean {
        val user = userRepository.getUser()
        Log.e("testData user", "$user")
        return user.userId.isNotEmpty() && user.token.isNotEmpty()
    }

    suspend fun logout() {
        userRepository.logout()
    }
}