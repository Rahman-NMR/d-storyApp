package com.rahman.storyapp.data.repository

import com.rahman.storyapp.data.remote.api.ApiService
import com.rahman.storyapp.data.remote.response.StoriesResponse

class StoryRepository(private val apiService: ApiService) {
    suspend fun getStories(): StoriesResponse {
        return apiService.getStories()
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryRepository? = null

        fun getInstance(apiService: ApiService): StoryRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = StoryRepository(apiService)
                INSTANCE = instance
                instance
            }
        }
    }
}