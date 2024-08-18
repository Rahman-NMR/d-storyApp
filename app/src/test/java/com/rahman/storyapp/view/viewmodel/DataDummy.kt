package com.rahman.storyapp.view.viewmodel

import com.rahman.storyapp.data.database.StoryEntity

object DataDummy {
    fun generateDummyStoriesResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryEntity(
                i.toString(),
                i.toString(),
                i.toString(),
                i.toString(),
                i.toString(),
                i.toDouble(),
                i.toDouble(),
            )
            items.add(quote)
        }
        return items
    }
}