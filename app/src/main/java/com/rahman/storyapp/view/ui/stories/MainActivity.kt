package com.rahman.storyapp.view.ui.stories

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rahman.storyapp.R
import com.rahman.storyapp.databinding.ActivityMainBinding
import com.rahman.storyapp.utils.DisplayMessage
import com.rahman.storyapp.view.ui.WelcomeActivity
import com.rahman.storyapp.view.ui.stories.adapterview.AdapterStory
import com.rahman.storyapp.view.ui.stories.adapterview.PaddingDecoration
import com.rahman.storyapp.view.viewmodel.stories.StoriesViewModel
import com.rahman.storyapp.view.viewmodel.user.UserViewModel
import com.rahman.storyapp.view.viewmodel.stories.ViewModelFactoryStory
import com.rahman.storyapp.view.viewmodel.user.ViewModelFactoryUser
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterStory: AdapterStory
    private val storiesViewModel: StoriesViewModel by viewModels {
        ViewModelFactoryStory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userFactory = ViewModelFactoryUser.getInstance(this)
        val userViewModel = ViewModelProvider(this, userFactory)[UserViewModel::class.java]

        binding.topAppBarMain.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_logout -> {
                    alertDialog(userViewModel)
                    true
                }

                else -> false
            }
        }

        adapterStory = AdapterStory {
            DisplayMessage.showToast(this, "${it.name}, ${it.createdAt}")
        }
        binding.rvListStory.addItemDecoration(PaddingDecoration(this, 32))
        binding.rvListStory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvListStory.adapter = adapterStory

        startup()
        viewModelObserver()
        binding.fabAddStory.setOnClickListener { startActivity(Intent(this, AddStoriesActivity::class.java)) }
        binding.swipeRefresh.setOnRefreshListener {
            startup()
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

    private fun startup() {
        storiesViewModel.clearMsg()
        storiesViewModel.showStories()
    }

    private fun viewModelObserver() {
        storiesViewModel.isLoading.observe(this) { isLoading ->
            binding.emptyMsg.visibility = if (isLoading || adapterStory.itemCount > 0) View.GONE else View.VISIBLE
            binding.circularProgressbar.visibility = if (isLoading && adapterStory.itemCount < 1) View.VISIBLE else View.GONE
            binding.linearProgressbar.visibility = if (isLoading && adapterStory.itemCount > 0) View.VISIBLE else View.GONE
        }
        storiesViewModel.message.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) DisplayMessage.showToast(this, msg)
        }
        storiesViewModel.stories.observe(this) { story ->
            if (story != null) {
                story.listStory.sortedByDescending { it.createdAt }
                adapterStory.storyList(story.listStory)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        startup()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}