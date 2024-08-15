package com.rahman.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.rahman.storyapp.data.local.UserModel
import com.rahman.storyapp.data.local.UserPreferences
import com.rahman.storyapp.data.paging.StoriesPagingSource
import com.rahman.storyapp.data.remote.api.ApiService
import com.rahman.storyapp.data.remote.response.ErrorResponse
import com.rahman.storyapp.data.remote.response.ListStoryItem
import com.rahman.storyapp.data.remote.response.LoginResponse
import com.rahman.storyapp.data.remote.response.StoriesResponse
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

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

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoriesPagingSource(apiService) }
        ).liveData
    }

    suspend fun getStoriesLocation(): StoriesResponse {
        return apiService.getStoriesWithLocation()
    }

    suspend fun addNewStory(photo: MultipartBody.Part, desc: RequestBody, lat: Double?, lng: Double?): ErrorResponse {
        val latitudeBody = lat.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val longitudeBody = lng.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        return if (lat != null && lng != null) apiService.addNewStoriesLocation(photo, desc, latitudeBody, longitudeBody)
        else apiService.addNewStories(photo, desc)
    }

    companion object {
        fun getInstance(
            apiService: ApiService,
            userPreferences: UserPreferences
        ) = UserRepository(apiService, userPreferences)
    }
}