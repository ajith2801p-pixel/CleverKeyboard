package com.cleverkeyboard.app.data.db.dao

import androidx.room.*
import com.cleverkeyboard.app.data.db.entity.CustomThemeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomThemeDao {

    @Query("SELECT * FROM custom_themes ORDER BY createdAt DESC")
    fun getAllThemesFlow(): Flow<List<CustomThemeEntity>>

    @Query("SELECT * FROM custom_themes WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteThemesFlow(): Flow<List<CustomThemeEntity>>

    @Query("SELECT * FROM custom_themes WHERE themeId = :id LIMIT 1")
    suspend fun getThemeById(id: String): CustomThemeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTheme(theme: CustomThemeEntity)

    @Query("DELETE FROM custom_themes WHERE themeId = :id")
    suspend fun deleteThemeById(id: String)
}
