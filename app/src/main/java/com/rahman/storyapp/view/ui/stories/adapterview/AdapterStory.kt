package com.rahman.storyapp.view.ui.stories.adapterview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.rahman.storyapp.R
import com.rahman.storyapp.data.remote.response.ListStoryItem

class AdapterStory(private val function: (ListStoryItem) -> Unit) : RecyclerView.Adapter<AdapterStory.ViewHolder>() {
    private var dataStories = ArrayList<ListStoryItem>()

    fun storyList(list: List<ListStoryItem>) {
        val diffCallback = StoryDiffCallback(dataStories, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        dataStories.clear()
        dataStories.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(listStoryItem: ListStoryItem, function: (ListStoryItem) -> Unit) {
            val photo = itemView.findViewById<ShapeableImageView>(R.id.iv_item_photo)
            val name = itemView.findViewById<MaterialTextView>(R.id.tv_item_name)
            val desc = itemView.findViewById<MaterialTextView>(R.id.tv_item_desc)

            desc.visibility = if (listStoryItem.description.isNullOrEmpty()) View.GONE else View.VISIBLE
            Glide.with(itemView.context).load(listStoryItem.photoUrl)
                .placeholder(R.drawable.img_placeholder).into(photo)
            name.text = listStoryItem.name
            desc.text = listStoryItem.description

            itemView.setOnClickListener { function(listStoryItem) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_story, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = dataStories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataStories[position], function)
    }
}
