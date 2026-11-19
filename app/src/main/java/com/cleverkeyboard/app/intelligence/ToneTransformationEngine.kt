package com.cleverkeyboard.app.intelligence

import android.content.Context
import com.cleverkeyboard.app.network.AiService

data class ToneOption(
    val id: String,
    val displayName: String,
    val description: String,
    val isCore: Boolean = true
)

class ToneTransformationEngine private constructor(private val context: Context) {

    private val aiService = AiService.getInstance()

    companion object {
        @Volatile
        private var instance: ToneTransformationEngine? = null

        fun getInstance(context: Context): ToneTransformationEngine {
            return instance ?: synchronized(this) {
                instance ?: ToneTransformationEngine(context.applicationContext).also { instance = it }
            }
        }

        val AVAILABLE_TONES = listOf(
            // 7 Core Styles
            ToneOption("casual", "Casual", "Friendly, everyday, and relaxed", isCore = true),
            ToneOption("professional", "Professional", "Business-ready, polished, and structured", isCore = true),
            ToneOption("formal", "Formal", "Traditional, respectful, and authoritative", isCore = true),
            ToneOption("friendly", "Friendly", "Warm, welcoming, and cheerful", isCore = true),
            ToneOption("confident", "Confident", "Direct, decisive, and strong", isCore = true),
            ToneOption("polite", "Polite", "Diplomatic, considerate, and gentle", isCore = true),
            ToneOption("short and concise", "Short & Concise", "Clear, brief, with zero fluff", isCore = true),

            // Extended Styles
            ToneOption("persuasive", "Persuasive", "Convincing with compelling rationale", isCore = false),
            ToneOption("empathetic", "Empathetic", "Compassionate and emotionally validating", isCore = false),
            ToneOption("sarcastic", "Sarcastic", "Sharp, witty, and clever banter", isCore = false),
            ToneOption("humorous", "Humorous", "Comical, fun, and entertaining", isCore = false),
            ToneOption("mysterious", "Mysterious", "Subtle, intriguing, and enigmatic", isCore = false),
            ToneOption("motivational", "Motivational", "Uplifting, energetic, and inspiring", isCore = false),
            ToneOption("assertive", "Assertive", "Firm, unapologetic, and straightforward", isCore = false)
        )
    }

    fun getTones(): List<ToneOption> = AVAILABLE_TONES

    /**
     * Rewrites input text using the specified tone via Google Gemini.
     */
    suspend fun rewriteText(text: String, toneId: String, apiKey: String): Result<String> {
        if (text.isBlank()) return Result.failure(IllegalArgumentException("Text to rewrite cannot be empty"))
        return aiService.rewriteWithTone(text, toneId, apiKey)
    }

    /**
     * Generates a context-aware smart reply to input text using the specified tone.
     */
    suspend fun generateReply(messageText: String, toneId: String, apiKey: String): Result<String> {
        if (messageText.isBlank()) return Result.failure(IllegalArgumentException("Message to reply to cannot be empty"))
        return aiService.generateReplyWithTone(messageText, toneId, apiKey)
    }
}
