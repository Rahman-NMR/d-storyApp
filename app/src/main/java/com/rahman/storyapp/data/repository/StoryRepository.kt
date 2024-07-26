package com.rahman.storyapp.data.repository

import com.rahman.storyapp.data.remote.api.ApiService
import com.rahman.storyapp.data.remote.response.ErrorResponse
import com.rahman.storyapp.data.remote.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(private val apiService: ApiService) {
    suspend fun getStories(): StoriesResponse {
        return apiService.getStories()
    }

    suspend fun addNewStory(photo: MultipartBody.Part, desc: RequestBody): ErrorResponse {
        return apiService.addNewStories(photo, desc)
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