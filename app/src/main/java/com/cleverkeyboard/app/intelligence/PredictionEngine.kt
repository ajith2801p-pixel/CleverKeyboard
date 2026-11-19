package com.cleverkeyboard.app.intelligence

import android.content.Context
import com.cleverkeyboard.app.autocorrect.AutoCorrectManager
import com.cleverkeyboard.app.autocorrect.SuggestionItem

class PredictionEngine private constructor(private val context: Context) {

    private val personalVocab = PersonalVocabularyManager.getInstance(context)
    private val grammarEngine = GrammarCorrectionEngine.getInstance(context)
    private val sentenceEngine = SentenceCompletionEngine.getInstance(context)
    private val settings = IntelligenceSettingsManager.getInstance(context)

    companion object {
        @Volatile
        private var instance: PredictionEngine? = null

        fun getInstance(context: Context): PredictionEngine {
            return instance ?: synchronized(this) {
                instance ?: PredictionEngine(context.applicationContext).also { instance = it }
            }
        }

        // Contextual Trigrams (Two previous words -> list of next words)
        private val ENGLISH_TRIGRAMS = mapOf(
            "thank you" to listOf("so much", "for your help", "very much", "for the update", "for everything"),
            "thanks for" to listOf("the update", "letting me know", "your help", "reaching out", "sharing"),
            "how are" to listOf("you", "you doing", "things", "you today", "your projects"),
            "what are" to listOf("you doing", "the plans", "the updates", "your thoughts", "the requirements"),
            "where are" to listOf("you", "we heading", "the files", "you going", "the keys"),
            "let me" to listOf("know", "check", "see", "call you", "update you"),
            "could you" to listOf("please", "share", "help me", "send", "check"),
            "can you" to listOf("please", "send", "help", "call me", "check this"),
            "looking forward" to listOf("to hearing", "to seeing you", "to working with you", "to your reply", "to it"),
            "i would" to listOf("like to", "love to", "appreciate", "prefer", "suggest"),
            "i will" to listOf("send you", "call you", "be there", "let you know", "check and confirm"),
            "hope you" to listOf("are doing well", "had a great day", "are fine", "like it", "feel better")
        )

        private val FALLBACK_WORDS = listOf("the", "to", "and", "a", "in", "is", "it", "you", "that")
    }

    /**
     * Generates 3 to 5 ranked, context-aware suggestions for the keyboard suggestion bar.
     * Execution time: < 10ms.
     */
    fun getContextualSuggestions(
        typingContext: TypingContext,
        maxSuggestions: Int = 5
    ): List<SuggestionItem> {
        val results = mutableListOf<SuggestionItem>()
        val seen = mutableSetOf<String>()

        fun addSuggestion(text: String, isCorrection: Boolean = false, isPrimary: Boolean = false) {
            val normalized = text.trim()
            if (normalized.isNotBlank() && !seen.contains(normalized.lowercase())) {
                seen.add(normalized.lowercase())
                results.add(SuggestionItem(text = normalized, isCorrection = isCorrection, isPrimary = isPrimary))
            }
        }

        // 1. Real-Time Grammar Correction (Module 3)
        if (settings.isGrammarCheckEnabled && typingContext.currentSentence.isNotBlank()) {
            val grammarFix = grammarEngine.checkLocalGrammar(typingContext.currentSentence)
            if (grammarFix != null && grammarFix.correctedText.isNotBlank()) {
                val displayFix = if (grammarFix.fullCorrectedSentence.length > 25 && grammarFix.correctedText.length < 15) {
                    grammarFix.correctedText
                } else if (grammarFix.ruleType == "Punctuation" || grammarFix.ruleType == "Capitalization") {
                    grammarFix.fullCorrectedSentence
                } else {
                    grammarFix.correctedText
                }
                addSuggestion(displayFix, isCorrection = true, isPrimary = true)
            }
        }

        val currWord = typingContext.currentWord
        val prevWord = typingContext.previousWord.lowercase()
        val twoBack = typingContext.twoWordsBack.lowercase()

        // 2. Active Typing Mode (user is typing a word)
        if (currWord.isNotEmpty()) {
            // A. Check Personal Vocabulary first (Module 2: frequency-boosted personal words & abbreviations)
            if (settings.isPersonalizedLearningEnabled) {
                val personalMatches = personalVocab.getMatchingWords(currWord, limit = 2)
                for (p in personalMatches) {
                    addSuggestion(p, isCorrection = false, isPrimary = results.isEmpty())
                }
            }

            // B. Fetch Dynamic AutoCorrect & Prefix suggestions from AutoCorrectManager
            val autoCorrectList = AutoCorrectManager.getDynamicSuggestions(context, currWord)
            for (item in autoCorrectList) {
                if (results.size >= maxSuggestions) break
                addSuggestion(item.text, isCorrection = item.isCorrection, isPrimary = item.isPrimary && results.isEmpty())
            }
        }
        // 3. Next-Word Prediction Mode (user finished word or typed space)
        else {
            // A. Check Sentence Completion Engine (Module 4: full phrases & sentence endings)
            if (settings.isSentenceCompletionEnabled && typingContext.currentSentence.isNotBlank()) {
                val sentenceCompletions = sentenceEngine.findSentenceCompletions(typingContext.currentSentence)
                for (completion in sentenceCompletions) {
                    if (results.size >= maxSuggestions) break
                    addSuggestion(completion, isCorrection = false, isPrimary = results.isEmpty())
                }
            }

            // B. Context-Aware Word Prediction (Module 1: Semantic understanding, Trigrams & Bigrams)
            if (settings.isContextPredictionEnabled && results.size < maxSuggestions) {
                val cleanSent = typingContext.currentSentence.lowercase().trim()
                    .replace(Regex("""[,:;?!]+"""), " ")
                    .replace(Regex("""\s+"""), " ")

                // 1) Contextual semantic patterns
                val semanticSuggestions = when {
                    typingContext.semanticKeywords.contains("HUNGRY") &&
                            (cleanSent.endsWith("something to") || cleanSent.endsWith("need something to") || cleanSent.endsWith("want to")) ->
                        listOf("eat", "drink", "cook")

                    typingContext.semanticKeywords.contains("EXAM") &&
                            (cleanSent.endsWith("need to") || cleanSent.endsWith("have to") || cleanSent.endsWith("want to")) ->
                        listOf("study", "prepare", "revise")

                    typingContext.semanticKeywords.contains("TIRED") &&
                            (cleanSent.endsWith("want to") || cleanSent.endsWith("need to") || cleanSent.endsWith("going to")) ->
                        listOf("sleep", "rest", "relax")

                    cleanSent.endsWith("going to") || cleanSent.endsWith("am going to") ->
                        listOf("the", "college", "sleep", "work", "home")

                    cleanSent.endsWith("an important") || cleanSent.endsWith("important") ->
                        listOf("meeting", "exam", "project", "presentation")

                    cleanSent.endsWith("i am feeling") || cleanSent.endsWith("feeling") ->
                        listOf("hungry", "tired", "good", "happy", "sick")

                    cleanSent.endsWith("need to") ->
                        listOf("talk", "check", "know", "go", "prepare")

                    cleanSent.endsWith("want to") ->
                        listOf("go", "know", "see", "talk", "try")

                    cleanSent.endsWith("how are") ->
                        listOf("you", "things", "you doing", "your projects")

                    cleanSent.endsWith("what are") ->
                        listOf("you doing", "the plans", "your thoughts")

                    cleanSent.endsWith("where are") ->
                        listOf("you", "we heading", "the files", "you going")

                    cleanSent.endsWith("let me") ->
                        listOf("know", "check", "see", "call you")

                    cleanSent.endsWith("can you") || cleanSent.endsWith("could you") ->
                        listOf("please", "send", "help", "call me")

                    cleanSent.endsWith("looking forward to") ->
                        listOf("hearing from you", "seeing you", "working with you")

                    cleanSent.endsWith("i would") ->
                        listOf("like to", "love to", "prefer", "suggest")

                    cleanSent.endsWith("i will") ->
                        listOf("call you", "send you", "be there", "let you know")

                    cleanSent.endsWith("hope you") ->
                        listOf("are doing well", "are fine", "had a great day")

                    cleanSent.endsWith("thank you") ->
                        listOf("so much", "for your help", "very much")

                    else -> null
                }

                if (semanticSuggestions != null) {
                    for (s in semanticSuggestions) {
                        if (results.size >= maxSuggestions) break
                        addSuggestion(s, isCorrection = false, isPrimary = results.isEmpty())
                    }
                }

                // 2) Trigram predictions (e.g. "thank you" -> "so much", "for your help")
                if (results.size < maxSuggestions && twoBack.isNotEmpty() && prevWord.isNotEmpty()) {
                    val trigramKey = "$twoBack $prevWord"
                    val trigramMatches = ENGLISH_TRIGRAMS[trigramKey]
                    if (trigramMatches != null) {
                        for (m in trigramMatches) {
                            if (results.size >= maxSuggestions) break
                            addSuggestion(m, isCorrection = false, isPrimary = results.isEmpty())
                        }
                    }
                }

                // 3) Bigram predictions (e.g. "how" -> "are", "are you", "do")
                if (results.size < maxSuggestions && prevWord.isNotEmpty()) {
                    val bigramMatches = AutoCorrectManager.getBigramPredictions(prevWord, typingContext.detectedLanguage)
                    for (b in bigramMatches) {
                        if (results.size >= maxSuggestions) break
                        addSuggestion(b, isCorrection = false, isPrimary = results.isEmpty())
                    }
                }
            }

            // Fallback: If still under 3 suggestions, provide common words
            if (results.size < 3) {
                for (fallback in FALLBACK_WORDS) {
                    if (results.size >= 3) break
                    addSuggestion(fallback, isCorrection = false, isPrimary = false)
                }
            }
        }

        return results.take(maxSuggestions)
    }
}
