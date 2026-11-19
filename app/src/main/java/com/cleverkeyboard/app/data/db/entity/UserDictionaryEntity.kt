package com.cleverkeyboard.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_dictionary",
    indices = [Index(value = ["word"], unique = true), Index(value = ["language"])]
)
data class UserDictionaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val frequency: Int = 1,
    val shortcut: String? = null,
    val language: String = "EN",
    val updatedAt: Long = System.currentTimeMillis()
)
