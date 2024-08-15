package com.rahman.storyapp.di

import android.content.Context
import com.rahman.storyapp.data.database.StoryDatabase
import com.rahman.storyapp.data.local.UserPreferences
import com.rahman.storyapp.data.local.dataStore
import com.rahman.storyapp.data.remote.api.ApiConfig
import com.rahman.storyapp.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreferences.getInstance(context.dataStore)
        val db = StoryDatabase.getDatabase(context)
        val user = runBlocking { pref.getUser.first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(apiService, pref, db)
    }
}