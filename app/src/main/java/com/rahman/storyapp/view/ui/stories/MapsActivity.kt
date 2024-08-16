package com.rahman.storyapp.view.ui.stories

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.rahman.storyapp.R
import com.rahman.storyapp.databinding.ActivityMapsBinding
import com.rahman.storyapp.utils.DisplayMessage
import com.rahman.storyapp.view.viewmodel.ViewModelFactory
import com.rahman.storyapp.view.viewmodel.stories.StoriesLocationViewModel

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private var _binding: ActivityMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    private val boundsBuilder = LatLngBounds.Builder()
    private val storiesViewModel: StoriesLocationViewModel by viewModels { ViewModelFactory.getInstance(this) }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) getMyLocation()
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(this.applicationContext, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mMap.isMyLocationEnabled = true
        else requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle()
        getMyLocation()
        getStoriesLocation()
    }

    private fun setMapStyle() {
        try {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps_styles))
        } catch (exception: Resources.NotFoundException) {
            finish()
        }
    }

    private fun getStoriesLocation() {
        storiesViewModel.clearMsg()
        storiesViewModel.showStories()

        storiesViewModel.isLoading.observe(this) { isLoading ->
            storiesViewModel.message.observe(this) { msg ->
                if (!isLoading) if (msg != null) DisplayMessage.showToast(this, msg)
            }
        }
        storiesViewModel.storiesLocation.observe(this) { story ->
            story.forEach { user ->
                val latLng = LatLng(user.lat ?: 0.0, user.lon ?: 0.0)
                mMap.addMarker(MarkerOptions().position(latLng).title(user.name).snippet(user.description).position(latLng))
                boundsBuilder.include(latLng)
            }

            val bounds: LatLngBounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}