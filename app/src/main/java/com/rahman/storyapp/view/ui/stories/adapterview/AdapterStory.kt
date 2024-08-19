package com.rahman.storyapp.view.ui.stories.adapterview

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isGone
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.rahman.storyapp.R
import com.rahman.storyapp.data.database.StoryEntity
import com.rahman.storyapp.databinding.ItemStoryBinding
import com.rahman.storyapp.view.ui.stories.DetailStoryActivity
import com.rahman.storyapp.view.ui.stories.DetailStoryActivity.Companion.EXTRA_DESC
import com.rahman.storyapp.view.ui.stories.DetailStoryActivity.Companion.EXTRA_NAME
import com.rahman.storyapp.view.ui.stories.DetailStoryActivity.Companion.EXTRA_PHOTO
import com.rahman.storyapp.view.ui.stories.DetailStoryActivity.Companion.EXTRA_TIME

class AdapterStory : PagingDataAdapter<StoryEntity, AdapterStory.ViewHolder>(DiffCallback) {
    class ViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listStoryItem: StoryEntity) {
            val photo = binding.ivItemPhoto
            val name = binding.tvItemName
            val desc = binding.tvItemDesc
            val sePhoto = binding.root.context.getString(R.string.se_photo)
            val seName = binding.root.context.getString(R.string.se_name)
            val seDesc = binding.root.context.getString(R.string.se_desc)

            desc.isGone = listStoryItem.description.isNullOrEmpty()
            Glide.with(binding.root.context).load(listStoryItem.photoUrl)
                .placeholder(R.drawable.img_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(photo)
            name.text = listStoryItem.name
            desc.text = listStoryItem.description

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, DetailStoryActivity::class.java)
                    .putExtra(EXTRA_PHOTO, listStoryItem.photoUrl)
                    .putExtra(EXTRA_NAME, listStoryItem.name)
                    .putExtra(EXTRA_DESC, listStoryItem.description)
                    .putExtra(EXTRA_TIME, listStoryItem.createdAt)
                val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(binding.root.context as Activity, Pair(photo, sePhoto), Pair(name, seName), Pair(desc, seDesc))
                binding.root.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) holder.bind(story)
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}