package com.cleverkeyboard.app.intelligence

import android.content.Context
import com.cleverkeyboard.app.network.AiService

data class SentenceCompletion(
    val prefix: String,
    val completionText: String,
    val isLocal: Boolean
)

class SentenceCompletionEngine private constructor(private val context: Context) {

    private val aiService = AiService.getInstance()
    private val settings = IntelligenceSettingsManager.getInstance(context)

    companion object {
        @Volatile
        private var instance: SentenceCompletionEngine? = null

        fun getInstance(context: Context): SentenceCompletionEngine {
            return instance ?: synchronized(this) {
                instance ?: SentenceCompletionEngine(context.applicationContext).also { instance = it }
            }
        }

        // Rich multi-completion dictionary (User Examples & common conversation completions)
        private val MULTI_COMPLETIONS = mapOf(
            "i will call you" to listOf("when I reach home.", "after my meeting.", "as soon as possible."),
            "call you" to listOf("when I reach home.", "after my meeting.", "as soon as possible."),
            "dear sir, i am writing this email to" to listOf("request permission to attend the event.", "inform you about my absence tomorrow.", "discuss the project progress."),
            "dear sir i am writing this email to" to listOf("request permission to attend the event.", "inform you about my absence tomorrow.", "discuss the project progress."),
            "i am writing this email to" to listOf("request permission to attend the event.", "inform you about my absence tomorrow.", "discuss the project progress."),
            "writing this email to" to listOf("request permission to attend the event.", "inform you about my absence tomorrow.", "discuss the project progress."),
            "i think we should" to listOf("go out this weekend.", "discuss this later.", "try something different."),
            "we should" to listOf("go out this weekend.", "discuss this later.", "try something different."),
            "i need to prepare" to listOf("the presentation.", "for tomorrow's meeting.", "the project documents."),
            "need to prepare" to listOf("the presentation.", "for tomorrow's meeting.", "the project documents."),
            "i am currently working on a project using" to listOf("Kotlin and Jetpack Compose.", "Python and machine learning.", "AWS and cloud infrastructure."),
            "working on a project using" to listOf("Kotlin and Jetpack Compose.", "Python and machine learning.", "AWS and cloud infrastructure."),
            "could you please" to listOf("let me know when you are available?", "share the project details.", "help me with this."),
            "can you please" to listOf("send the files?", "call me when free?", "check this out."),
            "thank you for" to listOf("your support and guidance.", "the quick update.", "everything you do."),
            "i would like to" to listOf("discuss this further with you.", "thank you for your time.", "request an update."),
            "please let me know" to listOf("if you have any questions.", "when you are free.", "your thoughts on this."),
            "hope you are" to listOf("doing well today.", "having a great week.", "feeling better.")
        )

        // Fast local completions map (instant matching for high-frequency conversational phrases)
        private val LOCAL_COMPLETIONS = mapOf(
            "thank you for" to "your support and guidance",
            "thank you" to "so much for your help",
            "thanks for" to "the quick update",
            "could you please" to "let me know when you are available?",
            "can you please" to "share the project details?",
            "i will send you" to "the updated files shortly",
            "i would like to" to "discuss this further with you",
            "looking forward to" to "hearing from you soon",
            "please let me know" to "if you have any questions",
            "hope you are" to "doing well today",
            "let me know if" to "you need anything else",
            "let me check and" to "get back to you soon",
            "sorry for the" to "delay in my response",
            "i will be" to "there in a few minutes",
            "see you" to "tomorrow morning",
            "call me when" to "you are free",
            "have a great" to "day ahead",
            "take care and" to "stay safe",
            // Tanglish high-frequency completions
            "naan ungalukku" to "udane anuparen",
            "romba sandhosham" to "nandri da",
            "aprom peslaam" to "take care da",
            "seri naan" to "kelambren da"
        )

        fun matchLocalCompletion(sentence: String): SentenceCompletion? {
            if (sentence.isBlank()) return null
            val clean = sentence.trim().lowercase().replace(Regex("""[,:;]+"""), " ").replace(Regex("""\s+"""), " ").trim()

            // 1. Direct match
            val direct = LOCAL_COMPLETIONS[clean]
            if (direct != null) {
                return SentenceCompletion(prefix = clean, completionText = direct, isLocal = true)
            }

            // 2. Suffix match
            for ((trigger, completion) in LOCAL_COMPLETIONS) {
                val cleanTrigger = trigger.lowercase().replace(Regex("""[,:;]+"""), " ").replace(Regex("""\s+"""), " ").trim()
                if (clean.endsWith(cleanTrigger)) {
                    return SentenceCompletion(prefix = trigger, completionText = completion, isLocal = true)
                }
            }

            return null
        }

        fun matchMultiCompletions(sentence: String): List<String> {
            if (sentence.isBlank()) return emptyList()
            val clean = sentence.trim().lowercase().replace(Regex("""[,:;]+"""), " ").replace(Regex("""\s+"""), " ").trim()

            for ((trigger, list) in MULTI_COMPLETIONS) {
                val cleanTrigger = trigger.lowercase().replace(Regex("""[,:;]+"""), " ").replace(Regex("""\s+"""), " ").trim()
                if (clean == cleanTrigger || clean.endsWith(cleanTrigger)) {
                    return list
                }
            }
            return emptyList()
        }
    }

    /**
     * Finds multiple local sentence completions (up to 3 options).
     */
    fun findSentenceCompletions(sentence: String): List<String> {
        if (!settings.isSentenceCompletionEnabled || sentence.isBlank()) return emptyList()
        val multi = matchMultiCompletions(sentence)
        if (multi.isNotEmpty()) return multi

        val single = matchLocalCompletion(sentence)
        return if (single != null) listOf(single.completionText) else emptyList()
    }

    /**
     * Finds an instant local sentence completion (<2ms).
     * Returns null if no exact or suffix match is found.
     */
    fun findLocalCompletion(sentence: String): SentenceCompletion? {
        if (!settings.isSentenceCompletionEnabled || sentence.isBlank()) return null
        return matchLocalCompletion(sentence)
    }

    /**
     * Async Cloud Completion via Google Gemini for rich open-ended sentences.
     */
    suspend fun completeSentenceCloud(contextText: String, apiKey: String): Result<String> {
        if (contextText.isBlank()) return Result.failure(IllegalArgumentException("Context is empty"))

        val prompt = """
            Complete this sentence naturally in a conversational, helpful tone: "$contextText"
            Rules:
            - Provide ONLY the next 3 to 8 words that naturally finish the sentence.
            - Do NOT repeat the prompt.
            - Language MUST match the prompt (English, Tanglish, or Tamil).
            - No quotes, no markdown, no explanations.
        """.trimIndent()

        // Call Gemini via AiService
        return aiService.rewriteWithTone(contextText, "short and concise", apiKey).map {
            // If it returns full text, strip prefix if present
            if (it.startsWith(contextText, ignoreCase = true)) {
                it.substring(contextText.length).trim()
            } else {
                it.trim()
            }
        }
    }
}
