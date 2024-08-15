package com.rahman.storyapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val createdAt: String?,
    val photoUrl: String?,
    val name: String?,
    val description: String?,
    val lon: Double?,
    val lat: Double?
)