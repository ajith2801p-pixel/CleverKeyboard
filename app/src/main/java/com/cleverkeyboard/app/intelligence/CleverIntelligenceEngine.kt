package com.cleverkeyboard.app.intelligence

import android.content.Context
import android.view.inputmethod.EditorInfo
import com.cleverkeyboard.app.autocorrect.SuggestionItem

/**
 * Unified CleverKeyboard AI Typing Intelligence Engine.
 * Orchestrates:
 * 1. Context-Aware Word Prediction
 * 2. Personalized Vocabulary Learning
 * 3. Real-Time Grammar Correction
 * 4. AI Sentence Completion
 * 5. AI Tone Conversion
 * 6. Tanglish-to-English Intelligence
 */
class CleverIntelligenceEngine private constructor(private val context: Context) {

    val settings: IntelligenceSettingsManager = IntelligenceSettingsManager.getInstance(context)
    val vocabularyManager: PersonalVocabularyManager = PersonalVocabularyManager.getInstance(context)
    val grammarEngine: GrammarCorrectionEngine = GrammarCorrectionEngine.getInstance(context)
    val sentenceCompletionEngine: SentenceCompletionEngine = SentenceCompletionEngine.getInstance(context)
    val toneEngine: ToneTransformationEngine = ToneTransformationEngine.getInstance(context)
    val tanglishEngine: TanglishLanguageEngine = TanglishLanguageEngine.getInstance(context)
    val predictionEngine: PredictionEngine = PredictionEngine.getInstance(context)

    companion object {
        private const val TAG = "CleverIntelligence"

        @Volatile
        private var instance: CleverIntelligenceEngine? = null

        fun getInstance(context: Context): CleverIntelligenceEngine {
            return instance ?: synchronized(this) {
                instance ?: CleverIntelligenceEngine(context.applicationContext).also { instance = it }
            }
        }
    }

    /**
     * Extracts full contextual analysis from cursor context.
     */
    fun analyzeContext(textBeforeCursor: CharSequence?, activeLanguage: String): TypingContext {
        return ContextAnalyzer.analyze(textBeforeCursor, activeLanguage)
    }

    /**
     * Module 1: Generates up to [maxSuggestions] ranked, context-aware suggestions.
     */
    fun getSuggestions(typingContext: TypingContext, maxSuggestions: Int = 5): List<SuggestionItem> {
        return predictionEngine.getContextualSuggestions(typingContext, maxSuggestions)
    }

    /**
     * Module 2: Learns a committed word into user dictionary (respects sensitive fields).
     */
    fun onWordCommitted(word: String, language: String, editorInfo: EditorInfo?) {
        vocabularyManager.learnWord(word, language, editorInfo)
    }

    /**
     * Module 3: Real-time local grammar check.
     */
    fun checkLocalGrammar(sentence: String): GrammarFix? {
        return grammarEngine.checkLocalGrammar(sentence)
    }

    /**
     * Module 3: Cloud grammar fix via Gemini.
     */
    suspend fun fixGrammarCloud(text: String, apiKey: String): Result<String> {
        return grammarEngine.checkCloudGrammar(text, apiKey)
    }

    /**
     * Module 4: Local or cloud sentence completion.
     */
    fun getLocalSentenceCompletion(sentence: String): SentenceCompletion? {
        return sentenceCompletionEngine.findLocalCompletion(sentence)
    }

    suspend fun getCloudSentenceCompletion(contextText: String, apiKey: String): Result<String> {
        return sentenceCompletionEngine.completeSentenceCloud(contextText, apiKey)
    }

    /**
     * Module 5: Rewrites text in one of the 7 core or extended tones.
     */
    suspend fun rewriteTone(text: String, toneId: String, apiKey: String): Result<String> {
        return toneEngine.rewriteText(text, toneId, apiKey)
    }

    /**
     * Module 6: Converts Tanglish to Tanglish, English, or Tamil script.
     */
    fun convertTanglishLocal(text: String): TanglishConversion? {
        return tanglishEngine.convertLocal(text)
    }

    suspend fun convertTanglishCloud(text: String, apiKey: String): Result<TanglishConversion> {
        return tanglishEngine.convertCloud(text, apiKey)
    }
}
