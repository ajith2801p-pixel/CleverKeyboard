package com.cleverkeyboard.app.intelligence

import com.cleverkeyboard.app.autocorrect.AutoCorrectManager

data class TypingContext(
    val fullTextBeforeCursor: String,
    val currentWord: String,
    val previousWord: String,
    val twoWordsBack: String,
    val currentSentence: String,
    val previousSentence: String,
    val semanticKeywords: Set<String>,
    val wordsInSentence: List<String>,
    val isSentenceStart: Boolean,
    val isIncompleteSentence: Boolean,
    val detectedLanguage: String
)

object ContextAnalyzer {

    private val SENTENCE_DELIMITERS = charArrayOf('.', '!', '?', '\n')

    // Common Tanglish cue words for high-accuracy local language detection
    private val TANGLISH_CUES = setOf(
        "naan", "seri", "sari", "enna", "epdi", "eppadi", "unga", "enaku",
        "ungaluku", "romba", "nalla", "sapten", "saapten", "varuva", "panra",
        "iruka", "irukiya", "irukken", "solren", "kelambren", "peslam", "machan",
        "bro", "da", "pa", "ma", "illa", "illaye", "polaama", "mudinjidha"
    )

    fun analyze(textBeforeCursor: CharSequence?, activeLanguage: String = AutoCorrectManager.LANG_EN): TypingContext {
        val rawText = textBeforeCursor?.toString() ?: ""
        if (rawText.isEmpty()) {
            return TypingContext(
                fullTextBeforeCursor = "",
                currentWord = "",
                previousWord = "",
                twoWordsBack = "",
                currentSentence = "",
                previousSentence = "",
                semanticKeywords = emptySet(),
                wordsInSentence = emptyList(),
                isSentenceStart = true,
                isIncompleteSentence = false,
                detectedLanguage = activeLanguage
            )
        }

        // 1. Current sentence and previous sentence extraction
        val lastDelimiterIndex = rawText.indexOfLast { it in SENTENCE_DELIMITERS }
        val currentSentence = if (lastDelimiterIndex >= 0 && lastDelimiterIndex < rawText.length - 1) {
            rawText.substring(lastDelimiterIndex + 1).trimStart()
        } else if (lastDelimiterIndex == -1) {
            rawText.trimStart()
        } else {
            ""
        }

        val previousSentence = if (lastDelimiterIndex > 0) {
            val beforeDelim = rawText.substring(0, lastDelimiterIndex)
            val secondLastDelim = beforeDelim.indexOfLast { it in SENTENCE_DELIMITERS }
            if (secondLastDelim >= 0) {
                beforeDelim.substring(secondLastDelim + 1).trim()
            } else {
                beforeDelim.trim()
            }
        } else {
            ""
        }

        // 2. Semantic Context Extraction across session
        val semanticKeywords = mutableSetOf<String>()
        val lowerRaw = rawText.lowercase()
        if (lowerRaw.contains("hungry") || lowerRaw.contains("starving") || lowerRaw.contains("food") || lowerRaw.contains("dinner") || lowerRaw.contains("lunch")) {
            semanticKeywords.add("HUNGRY")
        }
        if (lowerRaw.contains("exam") || lowerRaw.contains("test") || lowerRaw.contains("quiz") || lowerRaw.contains("semester") || lowerRaw.contains("finals")) {
            semanticKeywords.add("EXAM")
        }
        if (lowerRaw.contains("meeting") || lowerRaw.contains("client") || lowerRaw.contains("office") || lowerRaw.contains("interview") || lowerRaw.contains("presentation")) {
            semanticKeywords.add("MEETING")
        }
        if (lowerRaw.contains("project") || lowerRaw.contains("code") || lowerRaw.contains("app") || lowerRaw.contains("kotlin") || lowerRaw.contains("python") || lowerRaw.contains("aws")) {
            semanticKeywords.add("PROJECT")
        }
        if (lowerRaw.contains("sleep") || lowerRaw.contains("tired") || lowerRaw.contains("bed") || lowerRaw.contains("exhausted")) {
            semanticKeywords.add("TIRED")
        }

        // 3. Tokenize words in sentence
        val words = currentSentence.split("\\s+".toRegex()).filter { it.isNotBlank() }

        // 4. Current uncommitted word & previous words
        val endsWithWhitespace = rawText.isNotEmpty() && rawText.last().isWhitespace()
        val currentWord = if (endsWithWhitespace) "" else words.lastOrNull() ?: ""
        val completedWords = if (endsWithWhitespace) words else words.dropLast(1)
        val previousWord = completedWords.lastOrNull() ?: ""
        val twoWordsBack = if (completedWords.size >= 2) completedWords[completedWords.size - 2] else ""

        // 5. Sentence boundary detection
        val isSentenceStart = words.isEmpty() || (words.size == 1 && !endsWithWhitespace && lastDelimiterIndex >= 0)

        // 6. Incomplete sentence detection
        val isIncompleteSentence = currentSentence.isNotBlank() &&
                words.size >= 2 &&
                !currentSentence.endsWith(".") &&
                !currentSentence.endsWith("?") &&
                !currentSentence.endsWith("!")

        // 7. Language Detection
        val detectedLang = when {
            activeLanguage == AutoCorrectManager.LANG_TA -> AutoCorrectManager.LANG_TA
            rawText.any { it in '\u0B80'..'\u0BFF' } -> AutoCorrectManager.LANG_TA
            words.any { TANGLISH_CUES.contains(it.lowercase()) } -> AutoCorrectManager.LANG_TANG
            else -> activeLanguage
        }

        return TypingContext(
            fullTextBeforeCursor = rawText,
            currentWord = currentWord,
            previousWord = previousWord,
            twoWordsBack = twoWordsBack,
            currentSentence = currentSentence,
            previousSentence = previousSentence,
            semanticKeywords = semanticKeywords,
            wordsInSentence = words,
            isSentenceStart = isSentenceStart,
            isIncompleteSentence = isIncompleteSentence,
            detectedLanguage = detectedLang
        )
    }
}
