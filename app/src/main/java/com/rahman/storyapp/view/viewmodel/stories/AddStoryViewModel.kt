package com.rahman.storyapp.view.viewmodel.stories

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rahman.storyapp.data.remote.response.ErrorResponse
import com.rahman.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {
    private val _resultUpload = MutableLiveData<ErrorResponse>()
    val resultUpload: LiveData<ErrorResponse> get() = _resultUpload

    private val _imgUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> get() = _imgUri

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun uploadStory(photo: MultipartBody.Part, desc: RequestBody, lat: Double?, lng: Double?) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val response = repository.addNewStory(photo, desc, lat, lng)
                _resultUpload.value = response
                _message.value = response.message

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

    fun saveImageUri(uri: Uri?) {
        _imgUri.value = uri
    }

    fun clearMsg() {
        _message.value = null
    }
}