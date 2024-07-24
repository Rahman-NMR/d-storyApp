package com.rahman.storyapp.view.ui.stories

import androidx.recyclerview.widget.DiffUtil
import com.rahman.storyapp.data.remote.response.ListStoryItem
import java.util.ArrayList

class StoryDiffCallback(private val oldList: ArrayList<ListStoryItem>, private val newList: List<ListStoryItem>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldStory = oldList[oldItemPosition]
        val newStory = newList[newItemPosition]
        return oldStory.photoUrl == newStory.photoUrl && oldStory.name == newStory.name
    }

}
