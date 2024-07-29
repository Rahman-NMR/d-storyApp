package com.rahman.storyapp.view.ui.stories

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.rahman.storyapp.R
import com.rahman.storyapp.databinding.ActivityDetailStoryBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DetailStoryActivity : AppCompatActivity() {
    private var _binding: ActivityDetailStoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photoUrl = intent.getStringExtra(EXTRA_PHOTO) ?: ""
        val name = intent.getStringExtra(EXTRA_NAME) ?: getString(R.string.text_empty)
        val description = intent.getStringExtra(EXTRA_DESC) ?: getString(R.string.text_empty)
        val createdAt = intent.getStringExtra(EXTRA_TIME) ?: getString(R.string.text_empty)

        binding.topAppBarDetailStory.setNavigationOnClickListener { finish() }
        Glide.with(this).load(photoUrl)
            .placeholder(R.drawable.img_placeholder).into(binding.ivItemPhoto)
        binding.tvItemName.text = name
        binding.tvItemDesc.text = description
        binding.tvItemTime.text = createdAt.dateFormat()
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
        const val EXTRA_PHOTO = "extra_photo"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_DESC = "extra_desc"
        const val EXTRA_TIME = "extra_time"
    }
}