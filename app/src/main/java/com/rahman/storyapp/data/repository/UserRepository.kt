package com.rahman.storyapp.data.repository

import com.rahman.storyapp.data.local.UserModel
import com.rahman.storyapp.data.local.UserPreferences
import com.rahman.storyapp.data.remote.api.ApiService
import com.rahman.storyapp.data.remote.response.DetailStoriesResponse
import com.rahman.storyapp.data.remote.response.ErrorResponse
import com.rahman.storyapp.data.remote.response.LoginResponse
import com.rahman.storyapp.data.remote.response.StoriesResponse
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository(private val apiService: ApiService, private val preference: UserPreferences) {
    suspend fun register(name: String, email: String, password: String): ErrorResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun saveUser(user: UserModel) {
        preference.saveUser(user)
    }

    suspend fun getUser(): UserModel {
        return preference.getUser.first()
    }

    suspend fun logout() {
        preference.logout()
    }

    suspend fun getStories(): StoriesResponse {
        return apiService.getStories()
    }

    suspend fun detailStories(id: String): DetailStoriesResponse {
        return apiService.detailStories(id)
    }

    suspend fun addNewStory(photo: MultipartBody.Part, desc: RequestBody): ErrorResponse {
        return apiService.addNewStories(photo, desc)
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