package com.cleverkeyboard.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_snippets")
data class SavedSnippetEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val isSensitive: Boolean = false
)
