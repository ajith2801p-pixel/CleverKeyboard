package com.cleverkeyboard.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_themes")
data class CustomThemeEntity(
    @PrimaryKey
    val themeId: String,
    val name: String,
    val keyboardBgColor: Int,
    val keyBgColor: Int,
    val keyTextColor: Int,
    val accentColor: Int,
    val photoPath: String? = null,
    val photoDarkness: Float = 0.40f,
    val photoBlur: Float = 25f,
    val keyBordersMode: String = "AUTO",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
