package com.rahman.storyapp.view.ui.stories

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rahman.storyapp.R
import com.rahman.storyapp.databinding.ActivityMainBinding
import com.rahman.storyapp.di.Injection
import com.rahman.storyapp.view.ui.WelcomeActivity
import com.rahman.storyapp.view.viewmodel.StoriesViewModel
import com.rahman.storyapp.view.viewmodel.ViewModelFactoryStory
import com.rahman.storyapp.view.viewmodel.UserViewModel
import com.rahman.storyapp.view.viewmodel.ViewModelFactoryUser
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterStory: AdapterStory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userFactory = ViewModelFactoryUser(Injection.provideRepository(this))
        val storiesFactory = ViewModelFactoryStory(Injection.provideStoryRepository(this))
        val userViewModel = ViewModelProvider(this, userFactory)[UserViewModel::class.java]
        val storiesViewModel = ViewModelProvider(this, storiesFactory)[StoriesViewModel::class.java]

        binding.topAppBarMain.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_logout -> {
                    alertDialog(userViewModel)
                    true
                }

                else -> false
            }
        }

        adapterStory = AdapterStory()
        binding.rvListStory.layoutManager = LinearLayoutManager(this)
        binding.rvListStory.adapter = adapterStory

        startup(storiesViewModel)
        viewModelObserver(storiesViewModel)
        binding.fabAddStory.setOnClickListener { startActivity(Intent(this, AddStoriesActivity::class.java)) }
        binding.swipeRefresh.setOnRefreshListener {
            startup(storiesViewModel)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun alertDialog(userViewModel: UserViewModel) {
        MaterialAlertDialogBuilder(this)
            .setCancelable(true)
            .setTitle(getString(R.string.dialog_title))
            .setMessage(getString(R.string.dialog_message))
            .setPositiveButton(getString(R.string.dialog_positive)) { dialog, _ ->
                runBlocking {
                    userViewModel.logout()
                    dialog.dismiss()
                    startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
                    finish()
                }
            }
            .setNegativeButton(getString(R.string.dialog_negative)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun startup(storiesViewModel: StoriesViewModel) {
        storiesViewModel.clearMsg()
        storiesViewModel.showStories()
    }

    private fun viewModelObserver(storiesViewModel: StoriesViewModel) {
        storiesViewModel.isLoading.observe(this) {
            if (adapterStory.itemCount < 1) {
                binding.circularProgressbar.visibility = if (it) View.VISIBLE else View.GONE
                binding.linearProgressbar.visibility = View.GONE
            } else {
                binding.circularProgressbar.visibility = View.GONE
                binding.linearProgressbar.visibility = if (it) View.VISIBLE else View.GONE
            }
        }
        storiesViewModel.message.observe(this) { msg ->
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
        storiesViewModel.stories.observe(this) { story ->
            if (story != null) {
                adapterStory.storyList(story)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}