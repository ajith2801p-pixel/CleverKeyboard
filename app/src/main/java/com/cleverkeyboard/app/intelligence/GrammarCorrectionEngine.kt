package com.cleverkeyboard.app.intelligence

import android.content.Context
import com.cleverkeyboard.app.network.AiService

data class GrammarFix(
    val originalText: String,
    val correctedText: String,
    val originalSentence: String = "",
    val fullCorrectedSentence: String = "",
    val ruleType: String,
    val isConfident: Boolean = true
)

class GrammarCorrectionEngine private constructor(private val context: Context) {

    private val aiService = AiService.getInstance()
    private val settings = IntelligenceSettingsManager.getInstance(context)

    companion object {
        @Volatile
        private var instance: GrammarCorrectionEngine? = null

        fun getInstance(context: Context): GrammarCorrectionEngine {
            return instance ?: synchronized(this) {
                instance ?: GrammarCorrectionEngine(context.applicationContext).also { instance = it }
            }
        }

        // Fast Regex rules for local real-time correction (<1ms execution)
        private val LOCAL_RULES = listOf(
            // Subject-Verb Agreement: He/She/It with plural verbs & don't
            Regex("""(?i)\b(he|she|it)\s+don'?t\b""") to { m: MatchResult -> "${m.groupValues[1]} doesn't" },
            Regex("""(?i)\b(he|she|it)\s+go\b""") to { m: MatchResult -> "${m.groupValues[1]} goes" },
            Regex("""(?i)\b(he|she|it)\s+have\b""") to { m: MatchResult -> "${m.groupValues[1]} has" },
            Regex("""(?i)\b(he|she|it)\s+do\b""") to { m: MatchResult -> "${m.groupValues[1]} does" },
            Regex("""(?i)\b(he|she|it)\s+work\b""") to { m: MatchResult -> "${m.groupValues[1]} works" },
            Regex("""(?i)\b(he|she|it)\s+know\b""") to { m: MatchResult -> "${m.groupValues[1]} knows" },

            // Subject-Verb Agreement: They/We/You/I with incorrect verbs
            Regex("""(?i)\b(they|we|you)\s+is\b""") to { m: MatchResult -> "${m.groupValues[1]} are" },
            Regex("""(?i)\b(they|we|you)\s+does\b""") to { m: MatchResult -> "${m.groupValues[1]} do" },
            Regex("""(?i)\b(they|we|you)\s+has\b""") to { m: MatchResult -> "${m.groupValues[1]} have" },
            Regex("""(?i)\b(i)\s+is\b""") to { _: MatchResult -> "I am" },
            Regex("""(?i)\b(i)\s+has\b""") to { _: MatchResult -> "I have" },
            Regex("""(?i)\b(i)\s+does\b""") to { _: MatchResult -> "I do" },
            Regex("""(?i)\beveryone\s+are\b""") to { _: MatchResult -> "everyone is" },

            // Tenses & Auxiliaries (Yesterday + past tense)
            Regex("""(?i)\b(yesterday\s+(?:i|we|they|he|she)\s+)go\b""") to { m: MatchResult -> "${m.groupValues[1]}went" },
            Regex("""(?i)\b(yesterday\s+(?:i|we|they|he|she)\s+)see\b""") to { m: MatchResult -> "${m.groupValues[1]}saw" },
            Regex("""(?i)\b(yesterday\s+(?:i|we|they|he|she)\s+)come\b""") to { m: MatchResult -> "${m.groupValues[1]}came" },
            Regex("""(?i)\bdid\s+went\b""") to { _: MatchResult -> "did go" },
            Regex("""(?i)\bdid\s+saw\b""") to { _: MatchResult -> "did see" },
            Regex("""(?i)\bdid\s+came\b""") to { _: MatchResult -> "did come" },
            Regex("""(?i)\bdid\s+had\b""") to { _: MatchResult -> "did have" },
            Regex("""(?i)\bhave\s+saw\b""") to { _: MatchResult -> "have seen" },
            Regex("""(?i)\bhas\s+wrote\b""") to { _: MatchResult -> "has written" },
            Regex("""(?i)\bhad\s+broke\b""") to { _: MatchResult -> "had broken" },
            Regex("""(?i)\bam\s+agree\b""") to { _: MatchResult -> "agree" },

            // Common greetings and punctuation
            Regex("""(?i)\b(hello|hi|hey)\s+(how\s+are\s+you)\b""") to { m: MatchResult -> "${m.groupValues[1]}, ${m.groupValues[2]}?" },

            // Article misuse: "an" before consonant
            Regex("""(?i)\ban\s+(car|book|house|dog|phone|computer|man|woman|pen|table)\b""") to { m: MatchResult -> "a ${m.groupValues[1]}" },

            // Article misuse: "a" before vowel sounds
            Regex("""(?i)\ba\s+(apple|egg|hour|honest|orange|umbrella|island|idea|urgent)\b""") to { m: MatchResult -> "an ${m.groupValues[1]}" },

            // Common missing prepositions
            Regex("""(?i)\bgo\s+school\b""") to { _: MatchResult -> "go to school" },
            Regex("""(?i)\blisten\s+music\b""") to { _: MatchResult -> "listen to music" },
            Regex("""(?i)\bwait\s+you\b""") to { _: MatchResult -> "wait for you" }
        )

        fun findGrammarIssue(sentence: String): GrammarFix? {
            if (sentence.isBlank()) return null

            for ((regex, replacer) in LOCAL_RULES) {
                val match = regex.find(sentence)
                if (match != null) {
                    val original = match.value
                    val replacement = replacer(match)
                    if (original.lowercase() != replacement.lowercase()) {
                        val fullCorrected = sentence.replaceRange(match.range, replacement)
                        return GrammarFix(
                            originalText = original,
                            correctedText = replacement,
                            originalSentence = sentence,
                            fullCorrectedSentence = fullCorrected,
                            ruleType = "Grammar",
                            isConfident = true
                        )
                    }
                }
            }

            // Punctuation check: space before comma or period
            val spacingMatch = Regex("""\s+([,.:;?!])""").find(sentence)
            if (spacingMatch != null) {
                val fullCorrected = sentence.replaceRange(spacingMatch.range, spacingMatch.groupValues[1])
                return GrammarFix(
                    originalText = spacingMatch.value,
                    correctedText = spacingMatch.groupValues[1],
                    originalSentence = sentence,
                    fullCorrectedSentence = fullCorrected,
                    ruleType = "Punctuation",
                    isConfident = true
                )
            }

            return null
        }
    }

    /**
     * Checks sentence for local grammar issues in real-time (<5ms).
     * Returns null if no errors detected.
     */
    fun checkLocalGrammar(sentence: String): GrammarFix? {
        if (!settings.isGrammarCheckEnabled || sentence.isBlank()) return null
        return findGrammarIssue(sentence)
    }

    /**
     * Complete AI-driven sentence grammar correction via Google Gemini.
     */
    suspend fun checkCloudGrammar(text: String, apiKey: String): Result<String> {
        if (text.isBlank()) return Result.failure(IllegalArgumentException("Text is empty"))
        return aiService.fixGrammar(text, apiKey)
    }
}
