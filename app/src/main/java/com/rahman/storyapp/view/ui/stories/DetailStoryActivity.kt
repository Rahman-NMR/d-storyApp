package com.rahman.storyapp.view.ui.stories

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.rahman.storyapp.R
import com.rahman.storyapp.databinding.ActivityDetailStoryBinding
import com.rahman.storyapp.utils.DisplayMessage
import com.rahman.storyapp.view.viewmodel.stories.DetailStoriesViewModel
import com.rahman.storyapp.view.viewmodel.ViewModelFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DetailStoryActivity : AppCompatActivity() {
    private var _binding: ActivityDetailStoryBinding? = null
    private val binding get() = _binding!!
    private val detailStoryViewModel: DetailStoriesViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startup()
        viewModelObserver()

        binding.swipeRefresh.setOnRefreshListener {
            startup()
            binding.swipeRefresh.isRefreshing = false
        }
        binding.topAppBarDetailStory.setNavigationOnClickListener { finish() }
    }

    private fun startup() {
        detailStoryViewModel.clearMsg()
        detailStoryViewModel.showDetailStory(idStory ?: "")
    }

    private fun viewModelObserver() {
        detailStoryViewModel.message.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) DisplayMessage.showToast(this, msg)
        }
        detailStoryViewModel.isLoading.observe(this) {
            binding.detailStoryProgresbar.visibility = if (it) View.VISIBLE else View.GONE
        }
        detailStoryViewModel.detailStories.observe(this) { response ->
            val story = response.story
            if (story != null) {
                binding.nestedScrollLayout.visibility = View.VISIBLE
                binding.emptyMsg.visibility = View.GONE

                Glide.with(this).load(story.photoUrl)
                    .placeholder(R.drawable.img_placeholder).into(binding.ivItemPhoto)
                binding.tvItemName.text = story.name
                binding.tvItemDesc.text = story.description
                binding.tvItemTime.text = story.createdAt?.dateFormat()
            } else {
                binding.nestedScrollLayout.visibility = View.GONE
                binding.emptyMsg.visibility = View.VISIBLE
            }
        }
    }

    private fun String.dateFormat(): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")
        val date = format.parse(this) as Date
        val dateFormater = DateFormat.getDateInstance(DateFormat.FULL).format(date)
        val timeFormater = DateFormat.getTimeInstance(DateFormat.LONG).format(date)
        return "$dateFormater\n$timeFormater"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        var idStory: String? = null
    }
}