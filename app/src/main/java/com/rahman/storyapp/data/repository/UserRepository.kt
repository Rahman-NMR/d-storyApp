package com.rahman.storyapp.data.repository

import com.rahman.storyapp.data.local.UserPreferences
import com.rahman.storyapp.data.remote.api.ApiService
import com.rahman.storyapp.data.remote.response.ErrorResponse
import com.rahman.storyapp.data.remote.response.LoginResponse
import kotlinx.coroutines.flow.first

class UserRepository(private val apiService: ApiService, private val preference: UserPreferences) {
    suspend fun register(name: String, email: String, password: String): ErrorResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun saveUserId(uid: String) {
        preference.saveUser(uid)
    }

    suspend fun getUserId(): String? {
        return preference.getUser.first()
    }

    suspend fun logout() {
        preference.logout()
    }

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(apiService: ApiService, userPreferences: UserPreferences): UserRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = UserRepository(apiService, userPreferences)
                INSTANCE = instance
                instance
            }
        }
    }
}