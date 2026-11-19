package com.cleverkeyboard.app.intelligence

import android.content.Context
import android.util.Log
import android.view.inputmethod.EditorInfo
import com.cleverkeyboard.app.data.db.AppDatabase
import com.cleverkeyboard.app.data.db.entity.UserDictionaryEntity
import com.cleverkeyboard.app.security.SensitiveFieldGuard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

class PersonalVocabularyManager private constructor(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val dao = db.userDictionaryDao()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val settings = IntelligenceSettingsManager.getInstance(context)

    // Ultra-fast in-memory frequency cache
    private val memoryCache = ConcurrentHashMap<String, Int>()

    init {
        // Pre-populate frequently used default technical/app words and user vocabulary
        memoryCache["cleverkeyboard"] = 30
        memoryCache["crearemind"] = 30
        memoryCache["ajith"] = 30
        memoryCache["afshal"] = 25
        memoryCache["kubernetes"] = 25
        memoryCache["cybersecurity"] = 25
        memoryCache["aws"] = 25
        memoryCache["linux"] = 25
        memoryCache["cloud computing"] = 20
        memoryCache["machan"] = 20
        memoryCache["bro"] = 25
        memoryCache["gm"] = 20
        memoryCache["btw"] = 20
        memoryCache["idk"] = 20
        memoryCache["gonna"] = 20
        memoryCache["project"] = 15
        memoryCache["development"] = 15

        // Asynchronously load all saved words from Room into in-memory cache
        scope.launch {
            try {
                val words = dao.getWordsForLanguage("EN") + dao.getWordsForLanguage("TANG") + dao.getWordsForLanguage("TA")
                for (w in words) {
                    val key = w.word.lowercase()
                    memoryCache[key] = (memoryCache[key] ?: 0) + w.frequency
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user dictionary into memory", e)
            }
        }
    }

    companion object {
        private const val TAG = "PersonalVocabMgr"

        private val ABBREVIATIONS = mapOf(
            "gm" to "Good morning",
            "gn" to "Good night",
            "btw" to "By the way",
            "idk" to "I don't know",
            "tbh" to "To be honest",
            "omg" to "Oh my god",
            "brb" to "Be right back",
            "imo" to "In my opinion",
            "afaik" to "As far as I know",
            "tysm" to "Thank you so much",
            "np" to "No problem"
        )

        @Volatile
        private var instance: PersonalVocabularyManager? = null

        fun getInstance(context: Context): PersonalVocabularyManager {
            return instance ?: synchronized(this) {
                instance ?: PersonalVocabularyManager(context.applicationContext).also { instance = it }
            }
        }
    }

    /**
     * Learns a typed word or phrase into personal vocabulary with frequency tracking.
     * Guaranteed to skip sensitive/password/credit card/OTP fields.
     */
    fun learnWord(word: String, language: String = "EN", editorInfo: EditorInfo? = null) {
        if (!settings.isPersonalizedLearningEnabled) return

        // Sensitive field guard: Never learn passwords, credit cards, or OTPs
        if (editorInfo != null && SensitiveFieldGuard.isSensitiveField(editorInfo)) {
            Log.d(TAG, "Sensitive field detected. Skipping word learning for privacy.")
            return
        }

        val cleaned = word.trim().replace("""[.,!?;:'"()\[\]{}]""".toRegex(), "")
        if (cleaned.length < 2 || cleaned.all { it.isDigit() }) return

        val key = cleaned.lowercase()
        val updatedFreq = (memoryCache[key] ?: 0) + 1
        memoryCache[key] = updatedFreq

        // Persist to Room database asynchronously
        scope.launch {
            try {
                val existing = dao.findWord(cleaned)
                if (existing != null) {
                    dao.incrementFrequency(cleaned, System.currentTimeMillis())
                } else {
                    dao.insertOrUpdate(
                        UserDictionaryEntity(
                            word = cleaned,
                            frequency = updatedFreq,
                            language = language,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving learned word: $cleaned", e)
            }
        }
    }

    /**
     * Gets a frequency boost factor for ranking suggestions.
     * Returns a multiplier between 1.0f (new/standard) and 3.0f (frequently typed).
     */
    fun getBoostMultiplier(word: String): Float {
        val freq = memoryCache[word.lowercase()] ?: return 1.0f
        return when {
            freq >= 20 -> 2.5f
            freq >= 10 -> 2.0f
            freq >= 5 -> 1.5f
            freq >= 2 -> 1.2f
            else -> 1.0f
        }
    }

    /**
     * Checks if a word is in personal vocabulary (learned or seeded).
     */
    fun isKnownWord(word: String): Boolean {
        return memoryCache.containsKey(word.lowercase().trim())
    }

    /**
     * Returns personal vocabulary matching a prefix for instant autocomplete.
     */
    fun getMatchingWords(prefix: String, limit: Int = 3): List<String> {
        if (prefix.isBlank()) return emptyList()
        val lowerPrefix = prefix.lowercase()
        val results = mutableListOf<String>()

        // 1. Check abbreviation expansions (e.g. "gm" -> "Good morning", "btw" -> "By the way")
        val expansion = ABBREVIATIONS[lowerPrefix]
        if (expansion != null) {
            results.add(expansion)
        }

        // 2. Prefix matches from personal cache
        val matches = memoryCache.entries
            .asSequence()
            .filter { it.key.startsWith(lowerPrefix) && it.key.length >= lowerPrefix.length }
            .sortedByDescending { it.value }
            .take(limit)
            .map { entry ->
                formatPersonalWord(entry.key, prefix)
            }
            .toList()

        for (m in matches) {
            if (!results.contains(m)) {
                results.add(m)
            }
        }

        return results.take(limit)
    }

    private fun formatPersonalWord(word: String, inputPrefix: String): String {
        return when (word) {
            "ajith" -> "Ajith"
            "afshal" -> "Afshal"
            "crearemind" -> "Crearemind"
            "cleverkeyboard" -> "CleverKeyboard"
            "kubernetes" -> "Kubernetes"
            "cybersecurity" -> "Cybersecurity"
            "aws" -> "AWS"
            "linux" -> "Linux"
            "cloud computing" -> "Cloud Computing"
            else -> {
                if (inputPrefix.isNotEmpty() && inputPrefix[0].isUpperCase()) {
                    word.replaceFirstChar { it.uppercase() }
                } else {
                    word
                }
            }
        }
    }

    /**
     * Observes all user dictionary entries for settings / UI management.
     */
    fun getAllWordsFlow(): Flow<List<UserDictionaryEntity>> {
        return dao.getAllWordsFlow()
    }

    /**
     * Deletes a specific word from personal vocabulary.
     */
    suspend fun deleteWord(word: String) = withContext(Dispatchers.IO) {
        memoryCache.remove(word.lowercase())
        dao.deleteWord(word)
    }

    /**
     * Clears all learned words and resets personal vocabulary.
     */
    suspend fun resetVocabulary() = withContext(Dispatchers.IO) {
        memoryCache.clear()
        dao.clearAll()
    }
}
