package com.rahman.storyapp.view.ui.stories

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rahman.storyapp.R
import com.rahman.storyapp.databinding.ActivityAddStoriesBinding
import com.rahman.storyapp.utils.DisplayMessage
import com.rahman.storyapp.utils.ImageOperation.getImageUri
import com.rahman.storyapp.utils.ImageOperation.reduceFileImage
import com.rahman.storyapp.utils.ImageOperation.uriToFile
import com.rahman.storyapp.view.viewmodel.stories.AddStoryViewModel
import com.rahman.storyapp.view.viewmodel.stories.ViewModelFactoryStory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoriesActivity : AppCompatActivity() {
    private var _binding: ActivityAddStoriesBinding? = null
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null
    private val addStoryViewModel: AddStoryViewModel by viewModels {
        ViewModelFactoryStory.getInstance(this)
    }

    private val launcherGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            saveImage()
        }
    }
    private val launcherIntentCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSusccess ->
        if (isSusccess) saveImage()
        else currentImageUri = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addStoryViewModel.clearMsg()
        viewModelObserver()
        uiAction()
    }

    private fun uiAction() {
        binding.topAppBarAddStories.setNavigationOnClickListener { finish() }
        binding.buttonAdd.setOnClickListener {
            hideKeyboard(currentFocus ?: View(this@AddStoriesActivity))
            currentFocus?.clearFocus()

            if (binding.edAddDescription.text.toString().trim().isNotEmpty()) uploadImage()
            else showSnackbar(getString(R.string.msg_empty_description))
        }

        binding.btnOpenGallery.setOnClickListener { startGallery() }
        binding.btnOpenCamera.setOnClickListener { startCamera() }
        binding.btnDelete.setOnClickListener {
            addStoryViewModel.saveImageUri(null)
            binding.imgPreview.setImageResource((R.drawable.img_placeholder))
        }
    }

    private fun viewModelObserver() {
        addStoryViewModel.isLoading.observe(this) {
            binding.addStoriesProgressbar.visibility = if (it) View.VISIBLE else View.GONE
        }
        addStoryViewModel.message.observe(this) { msg ->
            if (msg != null) DisplayMessage.showToast(this, msg)
        }
        addStoryViewModel.resultUpload.observe(this) { result ->
            if (result.error == false) finish()
        }
        addStoryViewModel.currentImageUri.observe(this) { uri ->
            currentImageUri = uri
            binding.imgPreview.setImageURI(uri)
            binding.btnDelete.isEnabled = uri != null
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun saveImage() {
        addStoryViewModel.saveImageUri(currentImageUri)
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.edAddDescription.text.toString().trim()

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            addStoryViewModel.uploadStory(multipartBody, requestBody)

        } ?: showSnackbar(getString(R.string.msg_empty_image))
    }

    private fun showSnackbar(text: String) {
        DisplayMessage.showSnackbar(binding.root, text, getString(R.string.oke))
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}