package com.cleverkeyboard.app.data.db.dao

import androidx.room.*
import com.cleverkeyboard.app.data.db.entity.WordShortcutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordShortcutDao {

    @Query("SELECT * FROM word_shortcuts ORDER BY shortcut ASC")
    fun getAllShortcutsFlow(): Flow<List<WordShortcutEntity>>

    @Query("SELECT * FROM word_shortcuts WHERE shortcut = :shortcut LIMIT 1")
    suspend fun findShortcut(shortcut: String): WordShortcutEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShortcut(shortcut: WordShortcutEntity)

    @Delete
    suspend fun deleteShortcut(shortcut: WordShortcutEntity)

    @Query("DELETE FROM word_shortcuts WHERE shortcut = :shortcut")
    suspend fun deleteByShortcut(shortcut: String)
}
