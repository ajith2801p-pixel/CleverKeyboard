package com.cleverkeyboard.app.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AiService private constructor() {

    private val api: GeminiApiService

    // gemini-3.5-flash-lite is the active, fast model supported on Google AI Studio for new and existing users
    private val candidateModels = listOf(
        "gemini-3.5-flash-lite",
        "gemini-3.1-flash-lite",
        "gemini-flash-lite-latest",
        "gemini-flash-latest",
        "gemini-3.8-flash"
    )

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(GeminiApiService::class.java)
    }

    companion object {
        private const val TAG = "AiService"

        @Volatile
        private var instance: AiService? = null

        fun getInstance(): AiService {
            return instance ?: synchronized(this) {
                instance ?: AiService().also { instance = it }
            }
        }
    }

    private suspend fun executePrompt(promptText: String, apiKey: String): Result<String> {
        return withContext(Dispatchers.IO) {
            if (apiKey.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Gemini API Key is missing. Please set it in Settings."))
            }

            val request = GenerateContentRequest(
                contents = listOf(
                    Content(
                        parts = listOf(Part(text = promptText))
                    )
                ),
                generationConfig = GenerationConfig(temperature = 0.3f)
            )

            var lastError: Exception? = null

            for (model in candidateModels) {
                var attempt = 0
                val maxAttempts = 2
                while (attempt < maxAttempts) {
                    attempt++
                    try {
                        Log.d(TAG, "Attempting generation with model: $model (attempt $attempt)")
                        val response = api.generateContent(model = model, apiKey = apiKey, request = request)

                        if (response.isSuccessful) {
                            val body = response.body()
                            val candidate = body?.candidates?.firstOrNull()
                            val generatedText = candidate?.content?.parts?.firstOrNull()?.text?.trim()

                            if (!generatedText.isNullOrBlank()) {
                                Log.d(TAG, "Generation succeeded with model: $model -> $generatedText")
                                return@withContext Result.success(generatedText)
                            } else {
                                Log.w(TAG, "Model $model returned empty candidates/parts")
                                lastError = Exception("Gemini returned empty response")
                                break
                            }
                        } else {
                            val errorBody = response.errorBody()?.string() ?: response.message()
                            val code = response.code()
                            Log.w(TAG, "Model $model failed with code $code: $errorBody")
                            lastError = Exception("API Error ($code): $errorBody")

                            if (code == 429 || code >= 500) {
                                if (attempt < maxAttempts) {
                                    delay(600L * attempt)
                                    continue
                                }
                            } else if (code == 404) {
                                Log.i(TAG, "Model $model returned 404, falling back to next candidate model")
                                break
                            } else {
                                // Non-retryable error (e.g. invalid API key 400/403)
                                return@withContext Result.failure(lastError)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Network or execution error on model $model (attempt $attempt)", e)
                        lastError = e
                        if (attempt < maxAttempts) {
                            delay(500L * attempt)
                        }
                    }
                }
            }

            Result.failure(lastError ?: Exception("All Gemini model endpoints failed"))
        }
    }

    /**
     * 1. Fix Grammar
     * Prompt: "Fix grammar and spelling, return only corrected text, no explanation: {text}"
     */
    suspend fun fixGrammar(text: String, apiKey: String): Result<String> {
        val prompt = "Fix grammar and spelling, return only corrected text, no explanation: $text"
        return executePrompt(prompt, apiKey).map { cleanResultText(it) }
    }

    /**
     * 2. Tone Change / AI Write
     */
    suspend fun changeTone(text: String, tone: String, apiKey: String): Result<String> {
        return rewriteWithTone(text, tone, apiKey)
    }

    suspend fun rewriteWithTone(text: String, tone: String, apiKey: String): Result<String> {
        val prompt = buildRewritePrompt(text, tone)
        return executePrompt(prompt, apiKey).map { cleanResultText(it) }
    }

    /**
     * 2b. AI Reply with Tone (Normal, Flirty, Funny, Cute, Cool, Formal)
     */
    suspend fun generateReplyWithTone(text: String, tone: String, apiKey: String): Result<String> {
        val prompt = buildReplyPrompt(text, tone)
        return executePrompt(prompt, apiKey).map { cleanResultText(it) }
    }

    private fun buildReplyPrompt(inputText: String, tone: String): String {
        return when (tone.trim().lowercase()) {
            "flirty" -> """
                Reply to this message in a light, playful, flirty tone: "$inputText"
                Rules:
                - Keep it cute, cheeky, and respectful.
                - Strictly NO vulgar, NO double meaning, NO explicit content.
                - Use 1-2 emojis max (e.g. 😏, 😉, 🥺, ❤️).
                - Keep it short - exactly 1 line like a WhatsApp chat message.
                - Language MUST match the user's language: if user wrote in Tanglish, reply in natural conversational Tanglish (e.g. "Hi 😏 Late ah reply panniten nu kovama?", "Innum illa, un message ku wait panniten 🥺"). If English, reply in natural English. If Tamil script, reply in Tamil.
                - Sound like a real human boyfriend/girlfriend texting naturally, NEVER make it cringe, never make it a poem or long essay.
                - Return ONLY the reply message text, with no explanations, no prefix, and no quotes.
            """.trimIndent()

            "funny" -> """
                Reply to this message in a witty, sarcastic, humorous, funny tone: "$inputText"
                Rules:
                - Witty, sarcastic banter with good humor.
                - Keep it short - 1 line like a WhatsApp chat message.
                - Use 1-2 emojis max (e.g. 😂, 💀).
                - Language MUST match the user's language (if Tanglish, reply in Tanglish; if English, in English; if Tamil, in Tamil).
                - Return ONLY the reply message text, no explanations, no quotes.
            """.trimIndent()

            "cute" -> """
                Reply to this message in a soft, sweet, innocent, cute tone: "$inputText"
                Rules:
                - Soft, sweet, and cute.
                - Keep it short - 1 line like a WhatsApp chat message.
                - Use 1-2 cute emojis (e.g. 🥺, 🥰, ✨).
                - Language MUST match the user's language (Tanglish, English, or Tamil).
                - Return ONLY the reply message text, no explanations, no quotes.
            """.trimIndent()

            "cool" -> """
                Reply to this message in a short, chill, attitude, cool tone: "$inputText"
                Rules:
                - Confident, chill, and cool attitude.
                - Keep it very short - 1 line like a WhatsApp chat message.
                - Use 0-1 emojis (e.g. 😎).
                - Language MUST match the user's language (Tanglish, English, or Tamil).
                - Return ONLY the reply message text, no explanations, no quotes.
            """.trimIndent()

            "formal" -> """
                Reply to this message in a professional, polite, formal tone suitable for work or a boss: "$inputText"
                Rules:
                - Professional, courteous, respectful, and concise.
                - Keep it short - 1 line.
                - Strictly no emojis.
                - Language MUST match the user's language.
                - Return ONLY the reply message text, no explanations, no quotes.
            """.trimIndent()

            "persuasive" -> """
                Reply to this message in a convincing, compelling, and persuasive tone: "$inputText"
                Rules:
                - Convincing, articulate, and persuasive with strong, win-over rationale.
                - Keep it short - 1 line like a WhatsApp chat message.
                - Use 0-1 emoji (e.g. 🎯, 👍).
                - Language MUST match user's language (Tanglish, English, or Tamil).
                - Return ONLY the reply message text, no explanations, no quotes.
            """.trimIndent()

            "empathetic", "emphathetic" -> """
                Reply to this message in a deeply understanding, compassionate, warm, and supportive empathetic tone: "$inputText"
                Rules:
                - Warm, compassionate, validating their feelings with care.
                - Keep it short - 1 line like a WhatsApp chat message.
                - Use 1-2 warm emojis (e.g. 🫂, 🤍).
                - Language MUST match user's language: if Tanglish, reply in natural empathetic Tanglish (e.g. "Puriyudhu da, tension aagadha naan irukken 🫂"). If English, in English. If Tamil, in Tamil.
                - Return ONLY the reply message text, no explanations, no quotes.
            """.trimIndent()

            "sarcastic" -> """
                Reply to this message in a sharp, witty, dry, sarcastic tone: "$inputText"
                Rules:
                - Sarcastic banter and dry humor, clever without being hateful.
                - Keep it short - 1 line like a WhatsApp chat message.
                - Use 1-2 sarcastic emojis (e.g. 😏, 🙄, 🙃).
                - Language MUST match user's language: if Tanglish, use fun sarcastic Tanglish (e.g. "Aaha enna oru kandupidippu 😏"). If English, in English. If Tamil, in Tamil.
                - Return ONLY the reply message text, no explanations, no quotes.
            """.trimIndent()

            "humorous", "humourous" -> """
                Reply to this message in a hilarious, comical, and humorous tone: "$inputText"
                Rules:
                - Laugh-out-loud humor, funny twist, lighthearted fun.
                - Keep it short - 1 line like a WhatsApp chat message.
                - Use 1-2 laughing emojis (e.g. 😂, 🤣).
                - Language MUST match user's language (Tanglish, English, or Tamil).
                - Return ONLY the reply message text, no explanations, no quotes.
            """.trimIndent()

            "mysterious" -> """
                Reply to this message in an intriguing, subtle, enigmatic, and mysterious tone: "$inputText"
                Rules:
                - Mysterious, subtle, leaving them curious and guessing.
                - Keep it short - 1 line like a WhatsApp chat message.
                - Use 0-1 emoji (e.g. 🤫, 🕶️, 👁️).
                - Language MUST match user's language: if Tanglish, use mysterious Tanglish (e.g. "Siladhai secret ah vachikitta dhaan interesting 🤫"). If English, in English. If Tamil, in Tamil.
                - Return ONLY the reply message text, no explanations, no quotes.
            """.trimIndent()

            "motivational" -> """
                Reply to this message in an inspiring, uplifting, high-energy motivational tone: "$inputText"
                Rules:
                - High-energy, encouraging, confidence-boosting, and inspiring.
                - Keep it short - 1-2 lines like a WhatsApp chat message.
                - Use 1-2 energetic emojis (e.g. 🔥, 💪, 🚀).
                - Language MUST match user's language: if Tanglish, use energetic motivational Tanglish (e.g. "Nee mass pannuva, tension illama kalakku! 🔥💪"). If English, in English. If Tamil, in Tamil.
                - Return ONLY the reply message text, no explanations, no quotes.
            """.trimIndent()

            "assertive" -> """
                Reply to this message in a direct, firm, confident, and assertive tone: "$inputText"
                Rules:
                - Confident, firm, clear, stands ground respectfully without being rude or aggressive.
                - Keep it short - 1 line like a WhatsApp chat message.
                - Use 0-1 emoji (e.g. 🤝, ✔️).
                - Language MUST match user's language (Tanglish, English, or Tamil).
                - Return ONLY the reply message text, no explanations, no quotes.
            """.trimIndent()

            else -> """
                Reply to this message in a natural, friendly, direct everyday conversational tone: "$inputText"
                Rules:
                - Natural, friendly, casual direct reply.
                - Keep it short - 1 line like a WhatsApp chat message.
                - Language MUST match the user's language: if Tanglish, reply in Tanglish; if English, in English; if Tamil, in Tamil.
                - Return ONLY the reply message text, no explanations, no quotes.
            """.trimIndent()
        }
    }

    private fun buildRewritePrompt(inputText: String, tone: String): String {
        return when (tone.trim().lowercase()) {
            "flirty" -> """
                Rewrite this text in a light, playful, flirty tone: "$inputText"
                Rules:
                - Keep it cute, cheeky, and respectful.
                - Strictly NO vulgar, NO double meaning, NO explicit content.
                - Use 1-2 emojis max (e.g. 😏, 😉, 🥺, ❤️).
                - Keep it short - exactly 1 line like a WhatsApp chat message.
                - Language MUST match user's language: if Tanglish, rewrite in natural Tanglish. If English, in English. If Tamil, in Tamil.
                - Sound like a real human boyfriend/girlfriend texting naturally, NEVER cringe, never a poem.
                - Return ONLY the rewritten text, no explanations, no quotes.
            """.trimIndent()

            "funny" -> """
                Rewrite this text in a witty, sarcastic, funny tone: "$inputText"
                Rules:
                - Sarcastic, funny, and humorous.
                - Short 1 line.
                - Language MUST match user's language (English/Tanglish/Tamil).
                - Use 1-2 emojis.
                - Return ONLY the rewritten text, no explanations, no quotes.
            """.trimIndent()

            "cute" -> """
                Rewrite this text in a soft, sweet, innocent, cute tone: "$inputText"
                Rules:
                - Soft, adorable, and sweet.
                - Short 1 line.
                - Language MUST match user's language (English/Tanglish/Tamil).
                - Use 1-2 cute emojis.
                - Return ONLY the rewritten text, no explanations, no quotes.
            """.trimIndent()

            "cool" -> """
                Rewrite this text in a short, chill, attitude, cool tone: "$inputText"
                Rules:
                - Confident, chill, and cool.
                - Very short 1 line.
                - Language MUST match user's language (English/Tanglish/Tamil).
                - Return ONLY the rewritten text, no explanations, no quotes.
            """.trimIndent()

            "formal" -> """
                Rewrite this text in a professional, polite, formal tone suitable for work or a boss: "$inputText"
                Rules:
                - Professional, courteous, polished, and concise.
                - Language MUST match user's language (English/Tamil).
                - Strictly no emojis.
                - Return ONLY the rewritten text, no explanations, no quotes.
            """.trimIndent()

            "persuasive" -> """
                Rewrite this text in a convincing, compelling, and persuasive tone: "$inputText"
                Rules:
                - Persuasive, strong rationale, convincing and influential.
                - Short 1 line.
                - Language MUST match user's language (English/Tanglish/Tamil).
                - Return ONLY the rewritten text, no explanations, no quotes.
            """.trimIndent()

            "empathetic", "emphathetic" -> """
                Rewrite this text in a deeply understanding, compassionate, warm, and supportive empathetic tone: "$inputText"
                Rules:
                - Warm, compassionate, understanding, and kind.
                - Short 1 line.
                - Language MUST match user's language (English/Tanglish/Tamil).
                - Use 1 warm emoji (e.g. 🫂, 🤍).
                - Return ONLY the rewritten text, no explanations, no quotes.
            """.trimIndent()

            "sarcastic" -> """
                Rewrite this text in a sharp, witty, dry, sarcastic tone: "$inputText"
                Rules:
                - Sarcastic, witty, and clever banter.
                - Short 1 line.
                - Language MUST match user's language (English/Tanglish/Tamil).
                - Use 1-2 sarcastic emojis (e.g. 😏, 🙄).
                - Return ONLY the rewritten text, no explanations, no quotes.
            """.trimIndent()

            "humorous", "humourous" -> """
                Rewrite this text in a lighthearted, comical, and humorous tone: "$inputText"
                Rules:
                - Funny, humorous, and entertaining.
                - Short 1 line.
                - Language MUST match user's language (English/Tanglish/Tamil).
                - Use 1-2 emojis (e.g. 😂, 🤣).
                - Return ONLY the rewritten text, no explanations, no quotes.
            """.trimIndent()

            "mysterious" -> """
                Rewrite this text in an intriguing, subtle, enigmatic, and mysterious tone: "$inputText"
                Rules:
                - Subtle, intriguing, and mysterious.
                - Short 1 line.
                - Language MUST match user's language (English/Tanglish/Tamil).
                - Use 0-1 emoji (e.g. 🤫, 🕶️).
                - Return ONLY the rewritten text, no explanations, no quotes.
            """.trimIndent()

            "motivational" -> """
                Rewrite this text in an inspiring, empowering, and motivational tone: "$inputText"
                Rules:
                - Uplifting, inspiring, full of positive energy and drive.
                - Short 1 line.
                - Language MUST match user's language (English/Tanglish/Tamil).
                - Use 1-2 energetic emojis (e.g. 🔥, 💪).
                - Return ONLY the rewritten text, no explanations, no quotes.
            """.trimIndent()

            "assertive" -> """
                Rewrite this text in a confident, direct, firm, and assertive tone: "$inputText"
                Rules:
                - Confident, firm, clear, and direct without hesitation or rudeness.
                - Short 1 line.
                - Language MUST match user's language (English/Tanglish/Tamil).
                - Return ONLY the rewritten text, no explanations, no quotes.
            """.trimIndent()

            else -> """
                Rewrite this text in a clear, friendly, natural, and polished tone: "$inputText"
                Rules:
                - Clear, natural, and friendly.
                - 1 line.
                - Language MUST match user's language.
                - Return ONLY the rewritten text, no explanations, no quotes.
            """.trimIndent()
        }
    }

    /**
     * 3. Translate
     * Prompt: "Translate this to {targetLang}, return only translation: {text}"
     */
    suspend fun translate(text: String, targetLang: String, apiKey: String): Result<String> {
        val prompt = "Translate this to $targetLang, return only translation: $text"
        return executePrompt(prompt, apiKey).map { cleanResultText(it) }
    }

    /**
     * 4. Smart Reply
     * Prompt: "Given this incoming message: {text}, generate 3 short, natural replies, return as JSON array"
     */
    suspend fun smartReply(text: String, apiKey: String): Result<List<String>> {
        val prompt = "Given this incoming message: $text, generate 3 short, natural replies, return as JSON array"
        val rawResult = executePrompt(prompt, apiKey)

        return rawResult.mapCatching { rawText ->
            parseJsonArrayReplies(rawText)
        }
    }

    /**
     * 5. Make Short / Make Long
     * Prompt: "Make this text shorter / longer while keeping meaning: {text}"
     */
    suspend fun changeLength(text: String, makeLonger: Boolean, apiKey: String): Result<String> {
        val option = if (makeLonger) "longer" else "shorter"
        val prompt = "Make this text $option while keeping meaning: $text"
        return executePrompt(prompt, apiKey).map { cleanResultText(it) }
    }

    /**
     * 5b. Summarize Text
     * Prompt: "Summarize this text clearly and concisely in 1-2 sentences: {text}"
     */
    suspend fun summarize(text: String, apiKey: String): Result<String> {
        val prompt = """
            Summarize this text clearly, concisely, and accurately in 1-2 sentences: "$text"
            Rules:
            - Keep meaning identical.
            - Match user's language (English, Tanglish, or Tamil).
            - Return ONLY the summary, no explanations, no quotes.
        """.trimIndent()
        return executePrompt(prompt, apiKey).map { cleanResultText(it) }
    }

    /**
     * 5c. Explain Text Simply
     * Prompt: "Explain this text simply and clearly in everyday language: {text}"
     */
    suspend fun explain(text: String, apiKey: String): Result<String> {
        val prompt = """
            Explain this text simply and clearly in everyday language: "$text"
            Rules:
            - Clear, easy to understand.
            - Short 2 lines maximum.
            - Match user's language (English, Tanglish, or Tamil).
            - Return ONLY the explanation, no explanations, no quotes.
        """.trimIndent()
        return executePrompt(prompt, apiKey).map { cleanResultText(it) }
    }

    /**
     * 5d. Email Assistant
     */
    suspend fun emailAssistant(points: String, tone: String, apiKey: String): Result<String> {
        val prompt = """
            Compose a clean, professional email based on these points: "$points"
            Tone: $tone
            Rules:
            - Include Subject: and Body.
            - Concise and well-formatted.
            - Return ONLY the email, no meta-chatter.
        """.trimIndent()
        return executePrompt(prompt, apiKey).map { cleanResultText(it) }
    }

    /**
     * 6. Voice Typing: Tanglish to English & Tamil conversion
     * Prompt: "Convert this Tanglish speech to proper English and Tamil: {text}"
     */
    suspend fun convertTanglish(speechText: String, apiKey: String): Result<TanglishConversionResult> {
        val prompt = "Convert this Tanglish speech to proper English and Tamil: $speechText. Return JSON object with keys english and tamil, only valid JSON, no explanation"
        val rawResult = executePrompt(prompt, apiKey)
        return rawResult.mapCatching { raw ->
            parseTanglishResult(raw, speechText)
        }
    }

    private fun cleanResultText(raw: String): String {
        var cleaned = raw.trim()
        while (
            (cleaned.startsWith("**") && cleaned.endsWith("**") && cleaned.length >= 4) ||
            (cleaned.startsWith("*") && cleaned.endsWith("*") && cleaned.length >= 2) ||
            (cleaned.startsWith("\"") && cleaned.endsWith("\"") && cleaned.length >= 2) ||
            (cleaned.startsWith("'") && cleaned.endsWith("'") && cleaned.length >= 2)
        ) {
            if (cleaned.startsWith("**") && cleaned.endsWith("**") && cleaned.length >= 4) {
                cleaned = cleaned.substring(2, cleaned.length - 2).trim()
            } else if (cleaned.startsWith("*") && cleaned.endsWith("*") && cleaned.length >= 2) {
                cleaned = cleaned.substring(1, cleaned.length - 1).trim()
            } else if (cleaned.startsWith("\"") && cleaned.endsWith("\"") && cleaned.length >= 2) {
                cleaned = cleaned.substring(1, cleaned.length - 1).trim()
            } else if (cleaned.startsWith("'") && cleaned.endsWith("'") && cleaned.length >= 2) {
                cleaned = cleaned.substring(1, cleaned.length - 1).trim()
            }
        }
        return cleaned.trim()
    }

    private fun parseJsonArrayReplies(rawText: String): List<String> {
        var clean = rawText.trim()
        // Strip markdown code fences e.g. ```json [...] ```
        if (clean.startsWith("```")) {
            val lines = clean.lines()
            clean = lines.filterNot { it.trim().startsWith("```") }.joinToString("\n").trim()
        }

        // Try extracting array via JSON
        val startIndex = clean.indexOf('[')
        val endIndex = clean.lastIndexOf(']')
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            val jsonSub = clean.substring(startIndex, endIndex + 1)
            try {
                val jsonArray = JSONArray(jsonSub)
                val list = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.optString(i, "")
                    if (item.isNotBlank()) {
                        list.add(cleanResultText(item))
                    }
                }
                if (list.isNotEmpty()) {
                    return list
                }
            } catch (e: Exception) {
                Log.w(TAG, "JSONArray parsing failed, falling back to line parsing", e)
            }
        }

        // Fallback: Split by lines, remove numbering (1., 2., 3. or -)
        return clean.lines()
            .map { it.trim().replace(Regex("^(\\d+[.)]|[-*•])\\s*"), "") }
            .filter { it.isNotBlank() }
            .take(3)
    }

    private fun parseTanglishResult(rawText: String, originalSpoken: String): TanglishConversionResult {
        var clean = rawText.trim()
        if (clean.startsWith("```")) {
            val lines = clean.lines()
            clean = lines.filterNot { it.trim().startsWith("```") }.joinToString("\n").trim()
        }

        val start = clean.indexOf('{')
        val end = clean.lastIndexOf('}')
        if (start != -1 && end != -1 && end > start) {
            val jsonStr = clean.substring(start, end + 1)
            try {
                val json = org.json.JSONObject(jsonStr)
                val en = json.optString("english", "").trim()
                val ta = json.optString("tamil", "").trim()
                if (en.isNotBlank() || ta.isNotBlank()) {
                    return TanglishConversionResult(
                        english = cleanResultText(if (en.isNotBlank()) en else originalSpoken),
                        tamil = cleanResultText(if (ta.isNotBlank()) ta else originalSpoken),
                        originalSpoken = originalSpoken
                    )
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed parsing tanglish JSON", e)
            }
        }

        return TanglishConversionResult(
            english = originalSpoken,
            tamil = originalSpoken,
            originalSpoken = originalSpoken
        )
    }
}

data class TanglishConversionResult(
    val english: String,
    val tamil: String,
    val originalSpoken: String
)
