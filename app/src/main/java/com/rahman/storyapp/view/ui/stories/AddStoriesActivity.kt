package com.rahman.storyapp.view.ui.stories

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.rahman.storyapp.R
import com.rahman.storyapp.databinding.ActivityAddStoriesBinding
import com.rahman.storyapp.utils.DisplayMessage
import com.rahman.storyapp.utils.ImageOperation.getImageUri
import com.rahman.storyapp.utils.ImageOperation.reduceFileImage
import com.rahman.storyapp.utils.ImageOperation.uriToFile
import com.rahman.storyapp.view.viewmodel.ViewModelFactory
import com.rahman.storyapp.view.viewmodel.stories.AddStoryViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoriesActivity : AppCompatActivity() {
    private var _binding: ActivityAddStoriesBinding? = null
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null
    private val addStoryViewModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
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
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
        when {
            permission[ACCESS_FINE_LOCATION] ?: false || permission[ACCESS_COARSE_LOCATION] ?: false -> getLocation()
            else -> binding.materialCheckBox.isChecked = false
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLocation() {
        if (checkPermission(ACCESS_FINE_LOCATION) && checkPermission(ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                } else showSnackbar(getString(R.string.location_not_found))
            }
        } else requestPermissionLauncher.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        addStoryViewModel.clearMsg()
        viewModelObserver()
        uiAction()
    }

    private fun uiAction() {
        binding.topAppBarAddStories.setNavigationOnClickListener { finish() }
        binding.buttonAdd.setOnClickListener {
            hideKeyboard(currentFocus ?: View(this@AddStoriesActivity))
            currentFocus?.clearFocus()

            if (binding.materialCheckBox.isChecked && latitude == null && longitude == null) {
                showSnackbar(getString(R.string.location_not_found))
            } else {
                if (binding.edAddDescription.text.toString().trim().isNotEmpty()) uploadImage()
                else showSnackbar(getString(R.string.msg_empty_description))
            }
        }

        binding.btnOpenGallery.setOnClickListener { startGallery() }
        binding.btnOpenCamera.setOnClickListener { startCamera() }
        binding.btnDelete.setOnClickListener {
            addStoryViewModel.saveImageUri(null)
            binding.imgPreview.setImageResource((R.drawable.img_placeholder))
        }
        binding.materialCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                latitude = null
                longitude = null
            } else getLocation()
        }
    }

    private fun viewModelObserver() {
        addStoryViewModel.isLoading.observe(this) { binding.addStoriesProgressbar.isVisible = it }
        addStoryViewModel.message.observe(this) { msg ->
            if (msg != null) DisplayMessage.showToast(this, msg)
        }
        addStoryViewModel.resultUpload.observe(this) { result ->
            if (result.error == false) {
                val intent = Intent(this@AddStoriesActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
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
            val description = binding.edAddDescription.text.toString().trim()

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)

            addStoryViewModel.uploadStory(multipartBody, requestBody, latitude, longitude)
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