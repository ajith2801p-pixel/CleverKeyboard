package com.cleverkeyboard.app.data.db.dao

import androidx.room.*
import com.cleverkeyboard.app.data.db.entity.SavedSnippetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedSnippetDao {

    @Query("SELECT * FROM saved_snippets ORDER BY isPinned DESC, timestamp DESC")
    fun getAllSnippetsFlow(): Flow<List<SavedSnippetEntity>>

    @Query("SELECT * FROM saved_snippets ORDER BY isPinned DESC, timestamp DESC LIMIT :limit")
    suspend fun getRecentSnippets(limit: Int = 50): List<SavedSnippetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(snippet: SavedSnippetEntity)

    @Query("UPDATE saved_snippets SET isPinned = :pinned WHERE id = :id")
    suspend fun setPinned(id: String, pinned: Boolean)

    @Query("DELETE FROM saved_snippets WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM saved_snippets WHERE isPinned = 0")
    suspend fun clearUnpinned()

    @Query("DELETE FROM saved_snippets WHERE isSensitive = 1 AND isPinned = 0 AND (:currentTime - timestamp) > :maxAgeMs")
    suspend fun deleteExpiredSensitive(currentTime: Long, maxAgeMs: Long = 3600_000L)
}
