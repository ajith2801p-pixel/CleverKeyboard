package com.cleverkeyboard.app.data.db.dao

import androidx.room.*
import com.cleverkeyboard.app.data.db.entity.UserDictionaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDictionaryDao {

    @Query("SELECT * FROM user_dictionary ORDER BY frequency DESC, updatedAt DESC")
    fun getAllWordsFlow(): Flow<List<UserDictionaryEntity>>

    @Query("SELECT * FROM user_dictionary WHERE language = :lang ORDER BY frequency DESC")
    suspend fun getWordsForLanguage(lang: String): List<UserDictionaryEntity>

    @Query("SELECT * FROM user_dictionary WHERE word LIKE :prefix || '%' ORDER BY frequency DESC LIMIT :limit")
    suspend fun getSuggestionsWithPrefix(prefix: String, limit: Int = 10): List<UserDictionaryEntity>

    @Query("SELECT * FROM user_dictionary WHERE word = :word LIMIT 1")
    suspend fun findWord(word: String): UserDictionaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: UserDictionaryEntity)

    @Query("UPDATE user_dictionary SET frequency = frequency + 1, updatedAt = :timestamp WHERE word = :word")
    suspend fun incrementFrequency(word: String, timestamp: Long = System.currentTimeMillis()): Int

    @Query("DELETE FROM user_dictionary WHERE word = :word")
    suspend fun deleteWord(word: String)

    @Query("DELETE FROM user_dictionary")
    suspend fun clearAll()
}
