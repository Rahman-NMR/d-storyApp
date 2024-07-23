package com.rahman.storyapp.view.ui.stories

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.color.MaterialColors
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import com.rahman.storyapp.R
import com.rahman.storyapp.databinding.ActivityMainBinding
import com.rahman.storyapp.di.Injection
import com.rahman.storyapp.view.ui.WelcomeActivity
import com.rahman.storyapp.view.viewmodel.ViewModelFactory
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = ViewModelFactory(Injection.provideRepository(this))
        val viewModel = ViewModelProvider(this, viewModelFactory)[StoriesViewModel::class.java]
        val appBarLayout = binding.appBarMain
        appBarLayout.statusBarForeground = MaterialShapeDrawable.createWithElevationOverlay(this)
        appBarLayout.setStatusBarForegroundColor(MaterialColors.getColor(appBarLayout, com.google.android.material.R.attr.colorSurface))

        binding.topAppBarMain.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_logout -> {
                    val snackbar = Snackbar.make(binding.root, "Logout?", Snackbar.LENGTH_SHORT)
                    snackbar.setAction("ya") {
                        runBlocking {
                            viewModel.logout()
                            startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
                            finish()
                        }
                    }.show()

                    true
                }

                else -> false
            }
        }
        binding.rvListStory.layoutManager = LinearLayoutManager(this)
        viewModel.showStories()
        viewModel.stories.observe(this) {
            val adapterStory = AdapterStory(it)
            binding.rvListStory.adapter = adapterStory
        }
        binding.fabAddStory.setOnClickListener { startActivity(Intent(this, AddStoriesActivity::class.java)) }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}