package com.rahman.storyapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rahman.storyapp.data.remote.response.ErrorResponse
import com.rahman.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _registerResult = MutableLiveData<ErrorResponse>()
    val registerResult: LiveData<ErrorResponse> get() = _registerResult

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val response = userRepository.register(name, email, password)
                val msg = response.message

                _registerResult.value = response
                _message.value = msg
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody.message

                _message.value = errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMsg() {
        _message.value = null
    }
}