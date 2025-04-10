package com.rahman.storyapp.view.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rahman.storyapp.data.local.UserModel
import com.rahman.storyapp.data.remote.response.ErrorResponse
import com.rahman.storyapp.data.remote.response.LoginResponse
import com.rahman.storyapp.data.repository.UserRepository
import com.rahman.storyapp.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<LoginResponse>()
    val loginResult: LiveData<LoginResponse> get() = _loginResult

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true

            wrapEspressoIdlingResource {
                try {
                    val response = userRepository.login(email, password)
                    val msg = response.message

                    _loginResult.value = response
                    _message.value = msg

                    if (response.error == false) {
                        val result = response.loginResult
                        if (result != null) {
                            val name = result.name ?: ""
                            val uid = result.userId ?: ""
                            val token = result.token ?: ""
                            userRepository.saveUser(UserModel(name, uid, token))
                        }
                    }
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
    }

    fun clearMsg() {
        _message.value = null
    }
}