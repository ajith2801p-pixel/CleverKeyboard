package com.cleverkeyboard.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "word_shortcuts",
    indices = [Index(value = ["shortcut"], unique = true)]
)
data class WordShortcutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val shortcut: String,
    val expansion: String,
    val createdAt: Long = System.currentTimeMillis()
)
