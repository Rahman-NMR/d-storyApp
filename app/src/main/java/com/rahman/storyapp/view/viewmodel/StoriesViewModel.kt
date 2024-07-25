package com.rahman.storyapp.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rahman.storyapp.data.remote.response.ErrorResponse
import com.rahman.storyapp.data.remote.response.ListStoryItem
import com.rahman.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class StoriesViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> get() = _stories

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun showStories() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val response = storyRepository.getStories()
                _stories.value = response.listStory

            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody.message

                _message.value = errorMessage
            } catch (e: Exception) {
                val msg = e.message?.substringAfter(": ") ?: "Unknown Error"
                _message.value = msg
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMsg() {
        _message.value = null
    }
}