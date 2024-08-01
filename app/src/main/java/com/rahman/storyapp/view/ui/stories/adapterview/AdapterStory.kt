package com.rahman.storyapp.view.ui.stories.adapterview

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.rahman.storyapp.R
import com.rahman.storyapp.data.remote.response.ListStoryItem
import com.rahman.storyapp.view.ui.stories.DetailStoryActivity
import com.rahman.storyapp.view.ui.stories.DetailStoryActivity.Companion.EXTRA_DESC
import com.rahman.storyapp.view.ui.stories.DetailStoryActivity.Companion.EXTRA_NAME
import com.rahman.storyapp.view.ui.stories.DetailStoryActivity.Companion.EXTRA_PHOTO
import com.rahman.storyapp.view.ui.stories.DetailStoryActivity.Companion.EXTRA_TIME

class AdapterStory : ListAdapter<ListStoryItem, AdapterStory.ViewHolder>(DiffCallback()) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(listStoryItem: ListStoryItem) {
            val photo = itemView.findViewById<ShapeableImageView>(R.id.iv_item_photo)
            val name = itemView.findViewById<MaterialTextView>(R.id.tv_item_name)
            val desc = itemView.findViewById<MaterialTextView>(R.id.tv_item_desc)
            val sePhoto = itemView.context.getString(R.string.se_photo)
            val seName = itemView.context.getString(R.string.se_name)
            val seDesc = itemView.context.getString(R.string.se_desc)

            desc.visibility = if (listStoryItem.description.isNullOrEmpty()) View.GONE else View.VISIBLE
            Glide.with(itemView.context).load(listStoryItem.photoUrl)
                .placeholder(R.drawable.img_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(photo)
            name.text = listStoryItem.name
            desc.text = listStoryItem.description

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                    .putExtra(EXTRA_PHOTO, listStoryItem.photoUrl)
                    .putExtra(EXTRA_NAME, listStoryItem.name)
                    .putExtra(EXTRA_DESC, listStoryItem.description)
                    .putExtra(EXTRA_TIME, listStoryItem.createdAt)
                val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(itemView.context as Activity, Pair(photo, sePhoto), Pair(name, seName), Pair(desc, seDesc))
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_story, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<ListStoryItem>() {
        override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem == newItem
        }
    }
}
