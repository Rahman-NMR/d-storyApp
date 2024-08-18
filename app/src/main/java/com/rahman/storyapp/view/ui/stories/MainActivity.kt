package com.rahman.storyapp.view.ui.stories

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rahman.storyapp.R
import com.rahman.storyapp.data.database.StoryDatabase
import com.rahman.storyapp.databinding.ActivityMainBinding
import com.rahman.storyapp.utils.DisplayMessage
import com.rahman.storyapp.view.ui.WelcomeActivity
import com.rahman.storyapp.view.ui.stories.adapterview.AdapterStory
import com.rahman.storyapp.view.ui.stories.adapterview.LoadingStateAdapter
import com.rahman.storyapp.view.ui.stories.adapterview.PaddingDecoration
import com.rahman.storyapp.view.viewmodel.ViewModelFactory
import com.rahman.storyapp.view.viewmodel.stories.StoriesViewModel
import com.rahman.storyapp.view.viewmodel.user.UserViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterStory: AdapterStory
    private val storiesViewModel: StoriesViewModel by viewModels { ViewModelFactory.getInstance(this) }
    private val userViewModel: UserViewModel by viewModels { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapter()
        viewModelObserver()
        loginSession()
        uiAction()
        fabScrollToTop()
    }

    private fun fabScrollToTop() {
        val fabToTop = binding.fabToTop
        val fabAddStory = binding.fabAddStory
        val rvScrolling = binding.rvListStory
        var isFabVisible = false

        val listener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && !isFabVisible) {
                    fabToTop.show()
                    fabAddStory.shrink()
                    isFabVisible = true
                } else if (dy < 0 && isFabVisible) {
                    fabToTop.hide()
                    fabAddStory.extend()
                    isFabVisible = false
                }
            }
        }

        rvScrolling.addOnScrollListener(listener)
    }

    private fun alertDialog() {
        MaterialAlertDialogBuilder(this)
            .setCancelable(true)
            .setTitle(getString(R.string.dialog_title))
            .setMessage(getString(R.string.dialog_message))
            .setPositiveButton(getString(R.string.dialog_positive)) { dialog, _ ->
                dialog.dismiss()
                logoutUser()
            }
            .setNegativeButton(getString(R.string.dialog_negative)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun logoutUser() {
        lifecycleScope.launch {
            userViewModel.logout()
            StoryDatabase.getDatabase(this@MainActivity).apply {
                remoteKeysDao().deleteRemoteKeys()
                storyDao().clearStories()
            }

            startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
            finish()
        }
    }

    private fun loginSession() {
        val isLogin = runBlocking { userViewModel.isLogin() }
        if (!isLogin) logoutUser()
    }

    private fun setupAdapter() {
        adapterStory = AdapterStory().apply {
            addLoadStateListener { loadState ->
                val load = loadState.refresh is LoadState.Loading
                val empty = itemCount < 1

                binding.circularProgressbar.isVisible = load && empty
                binding.linearProgressbar.isVisible = load && !empty
                binding.rvListStory.isVisible = !empty
                binding.emptyMsg.isVisible = empty && !load
                binding.retryButton.isVisible = empty && !load

                val errorState = loadState.refresh as? LoadState.Error
                val errorMsg = errorState?.let { it.error.localizedMessage?.substringAfter(": ") ?: "Unknown Error" }
                if (errorMsg != null) DisplayMessage.showToast(this@MainActivity, errorMsg)
            }
        }

        binding.rvListStory.apply {
            addItemDecoration(PaddingDecoration(this@MainActivity, 32))
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = adapterStory.withLoadStateFooter(footer = LoadingStateAdapter { adapterStory.retry() })
        }
    }

    private fun adapterRefresh() {
        adapterStory.refresh()
    }

    private fun uiAction() {
        binding.fabAddStory.setOnClickListener { startActivity(Intent(this, AddStoriesActivity::class.java)) }
        binding.retryButton.setOnClickListener { adapterRefresh() }
        binding.fabToTop.setOnClickListener { binding.rvListStory.smoothScrollToPosition(0) }
        binding.swipeRefresh.setOnRefreshListener {
            adapterRefresh()
            binding.swipeRefresh.isRefreshing = false
        }
        binding.topAppBarMain.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_logout -> {
                    alertDialog()
                    true
                }

                R.id.action_maps -> {
                    startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    private fun viewModelObserver() {
        storiesViewModel.stories.observe(this) { story ->
            adapterStory.submitData(lifecycle, story)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}