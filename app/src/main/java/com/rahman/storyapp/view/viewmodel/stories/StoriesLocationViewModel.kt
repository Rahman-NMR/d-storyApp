package com.rahman.storyapp.view.viewmodel.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rahman.storyapp.data.remote.response.ErrorResponse
import com.rahman.storyapp.data.remote.response.ListStoryItem
import com.rahman.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class StoriesLocationViewModel(private val repository: UserRepository) : ViewModel() {
    private val _storiesLocation = MutableLiveData<List<ListStoryItem>>()
    val storiesLocation: LiveData<List<ListStoryItem>> get() = _storiesLocation

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun showStories() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val response = repository.getStoriesLocation()
                _storiesLocation.value = response.listStory

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