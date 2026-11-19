package com.cleverkeyboard.app.intelligence

import android.content.Context
import android.content.SharedPreferences

enum class TanglishOutputMode {
    TANGLISH, // Cleaned Tanglish romanized
    ENGLISH,  // Translated to English meaning
    TAMIL     // Transliterated to Tamil script
}

class IntelligenceSettingsManager private constructor(context: Context) {

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "clever_intelligence_prefs"
        private const val KEY_CONTEXT_PREDICTION = "key_context_prediction"
        private const val KEY_PERSONALIZED_LEARNING = "key_personalized_learning"
        private const val KEY_GRAMMAR_CHECK = "key_grammar_check"
        private const val KEY_SENTENCE_COMPLETION = "key_sentence_completion"
        private const val KEY_TANGLISH_OUTPUT_MODE = "key_tanglish_output_mode"

        @Volatile
        private var instance: IntelligenceSettingsManager? = null

        fun getInstance(context: Context): IntelligenceSettingsManager {
            return instance ?: synchronized(this) {
                instance ?: IntelligenceSettingsManager(context).also { instance = it }
            }
        }
    }

    var isContextPredictionEnabled: Boolean
        get() = prefs.getBoolean(KEY_CONTEXT_PREDICTION, true)
        set(value) = prefs.edit().putBoolean(KEY_CONTEXT_PREDICTION, value).apply()

    var isPersonalizedLearningEnabled: Boolean
        get() = prefs.getBoolean(KEY_PERSONALIZED_LEARNING, true)
        set(value) = prefs.edit().putBoolean(KEY_PERSONALIZED_LEARNING, value).apply()

    var isGrammarCheckEnabled: Boolean
        get() = prefs.getBoolean(KEY_GRAMMAR_CHECK, true)
        set(value) = prefs.edit().putBoolean(KEY_GRAMMAR_CHECK, value).apply()

    var isSentenceCompletionEnabled: Boolean
        get() = prefs.getBoolean(KEY_SENTENCE_COMPLETION, true)
        set(value) = prefs.edit().putBoolean(KEY_SENTENCE_COMPLETION, value).apply()

    var tanglishOutputMode: TanglishOutputMode
        get() {
            val name = prefs.getString(KEY_TANGLISH_OUTPUT_MODE, TanglishOutputMode.ENGLISH.name)
            return try {
                TanglishOutputMode.valueOf(name ?: TanglishOutputMode.ENGLISH.name)
            } catch (_: Exception) {
                TanglishOutputMode.ENGLISH
            }
        }
        set(value) = prefs.edit().putString(KEY_TANGLISH_OUTPUT_MODE, value.name).apply()
}
