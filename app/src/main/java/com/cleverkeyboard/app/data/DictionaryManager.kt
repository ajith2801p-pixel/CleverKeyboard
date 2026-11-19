package com.cleverkeyboard.app.data

import android.content.Context
import android.util.Log
import com.cleverkeyboard.app.data.db.AppDatabase
import com.cleverkeyboard.app.data.db.entity.UserDictionaryEntity
import com.cleverkeyboard.app.network.AiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class WordDefinition(
    val word: String,
    val partOfSpeech: String,
    val definition: String,
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList(),
    val example: String = ""
)

object DictionaryManager {
    private const val TAG = "DictionaryManager"

    // Curated educational Word of the Day catalog
    private val WORDS_OF_THE_DAY = listOf(
        WordDefinition(
            word = "Serendipity",
            partOfSpeech = "noun",
            definition = "The occurrence of valuable or agreeable events by chance in a happy or beneficial way.",
            synonyms = listOf("fluke", "chance", "coincidence", "good fortune"),
            antonyms = listOf("misfortune", "bad luck"),
            example = "Meeting an old friend on the train was a moment of pure serendipity."
        ),
        WordDefinition(
            word = "Resilience",
            partOfSpeech = "noun",
            definition = "The capacity to recover quickly from difficulties; toughness and elasticity.",
            synonyms = listOf("tenacity", "fortitude", "toughness", "adaptability"),
            antonyms = listOf("fragility", "vulnerability"),
            example = "Her remarkable resilience helped the team overcome every hurdle."
        ),
        WordDefinition(
            word = "Eloquent",
            partOfSpeech = "adjective",
            definition = "Fluent or persuasive in speaking or writing; clearly expressing feelings or meaning.",
            synonyms = listOf("articulate", "expressive", "fluent", "persuasive"),
            antonyms = listOf("inarticulate", "hesitant"),
            example = "He gave an eloquent speech that moved the entire audience."
        ),
        WordDefinition(
            word = "Tenacity",
            partOfSpeech = "noun",
            definition = "The quality or fact of being able to grip something firmly; determination.",
            synonyms = listOf("perseverance", "persistence", "grit", "resolve"),
            antonyms = listOf("weakness", "irresolution"),
            example = "His tenacity in solving complex coding bugs made him an indispensable developer."
        ),
        WordDefinition(
            word = "Ephemeral",
            partOfSpeech = "adjective",
            definition = "Lasting for a very short time; fleeting and transient.",
            synonyms = listOf("transient", "fleeting", "short-lived", "momentary"),
            antonyms = listOf("permanent", "eternal", "lasting"),
            example = "Fame in the digital era can be surprisingly ephemeral."
        ),
        WordDefinition(
            word = "Innovation",
            partOfSpeech = "noun",
            definition = "A new method, idea, product, or creative solution.",
            synonyms = listOf("invention", "novelty", "breakthrough", "advancement"),
            antonyms = listOf("stagnation", "tradition"),
            example = "CleverKeyboard brings state-of-the-art innovation to mobile typing."
        ),
        WordDefinition(
            word = "Empirical",
            partOfSpeech = "adjective",
            definition = "Based on, concerned with, or verifiable by observation or experience rather than theory.",
            synonyms = listOf("observational", "experimental", "practical", "factual"),
            antonyms = listOf("theoretical", "hypothetical"),
            example = "They gathered empirical data to test the performance improvements."
        )
    )

    // Curated common dictionary lookup map
    private val LOCAL_DICTIONARY = mapOf(
        "clever" to WordDefinition("clever", "adjective", "Quick to understand, learn, and devise or apply ideas; intelligent.", listOf("smart", "intelligent", "sharp", "ingenious"), listOf("foolish", "clumsy"), "CleverKeyboard is designed for modern smart typing."),
        "keyboard" to WordDefinition("keyboard", "noun", "A panel of keys that operate a computer, smartphone, or typewriter.", listOf("keypad", "input device"), emptyList(), "Typing on this keyboard is fast and accurate."),
        "intelligent" to WordDefinition("intelligent", "adjective", "Having or showing intelligence, especially of a high level.", listOf("smart", "clever", "bright"), listOf("stupid", "ignorant"), "The app provides intelligent autocorrect and next-word suggestions."),
        "technology" to WordDefinition("technology", "noun", "The application of scientific knowledge for practical purposes.", listOf("engineering", "computing", "machinery"), emptyList(), "Modern AI technology enhances communication."),
        "development" to WordDefinition("development", "noun", "The process of developing or being developed.", listOf("growth", "progress", "evolution"), listOf("decline"), "Active software development keeps the app bug-free."),
        "project" to WordDefinition("project", "noun", "An individual or collaborative enterprise planned to achieve a particular aim.", listOf("undertaking", "venture", "plan"), emptyList(), "The project target is CleverKeyboard 3.0."),
        "communication" to WordDefinition("communication", "noun", "The imparting or exchanging of information by speaking, writing, or other media.", listOf("dialogue", "conversation", "messaging"), emptyList(), "Clear communication avoids misunderstandings."),
        "beautiful" to WordDefinition("beautiful", "adjective", "Pleasing the senses or mind aesthetically.", listOf("gorgeous", "attractive", "lovely"), listOf("ugly"), "The keyboard features beautiful neon and glass themes."),
        "privacy" to WordDefinition("privacy", "noun", "The state or condition of being free from being observed or disturbed by other people.", listOf("secrecy", "confidentiality", "isolation"), listOf("publicity"), "CleverKeyboard guards your privacy with offline-first design.")
    )

    fun getWordOfTheDay(): WordDefinition {
        val dayIndex = (System.currentTimeMillis() / (1000 * 60 * 60 * 24)).toInt()
        val index = Math.abs(dayIndex) % WORDS_OF_THE_DAY.size
        return WORDS_OF_THE_DAY[index]
    }

    suspend fun lookupWord(word: String, apiKey: String? = null): WordDefinition {
        val clean = word.lowercase().trim()
        if (clean.isBlank()) return getWordOfTheDay()

        // 1. Check local curated dictionary
        LOCAL_DICTIONARY[clean]?.let { return it }

        // 2. Query Gemini AI when apiKey is present
        if (!apiKey.isNullOrBlank()) {
            try {
                val prompt = """
                    Define the word "$clean" precisely:
                    Return ONLY a JSON object with:
                    - "word": "$clean"
                    - "partOfSpeech": "noun/verb/adjective/etc"
                    - "definition": "concise 1-sentence definition"
                    - "synonyms": ["syn1", "syn2", "syn3"]
                    - "antonyms": ["ant1", "ant2"]
                    - "example": "1 natural example sentence"
                    No code fences, no explanations.
                """.trimIndent()

                val result = AiService.getInstance().rewriteWithTone(clean, "definition: $prompt", apiKey)
                val raw = result.getOrNull()
                if (!raw.isNullOrBlank()) {
                    val parsed = parseDefinitionJson(raw, clean)
                    if (parsed != null) return parsed
                }
            } catch (e: Exception) {
                Log.w(TAG, "Gemini dictionary query failed", e)
            }
        }

        // 3. Fallback generic definition
        return WordDefinition(
            word = word,
            partOfSpeech = "term",
            definition = "Vocabulary entry for \"$word\".",
            synonyms = listOf("expression", "phrase"),
            example = "The word \"$word\" was looked up in CleverKeyboard dictionary."
        )
    }

    private fun parseDefinitionJson(raw: String, originalWord: String): WordDefinition? {
        try {
            var text = raw.trim()
            if (text.startsWith("```")) {
                text = text.lines().filterNot { it.trim().startsWith("```") }.joinToString("\n").trim()
            }
            val start = text.indexOf('{')
            val end = text.lastIndexOf('}')
            if (start != -1 && end != -1 && end > start) {
                val json = JSONObject(text.substring(start, end + 1))
                val w = json.optString("word", originalWord)
                val pos = json.optString("partOfSpeech", "term")
                val def = json.optString("definition", "")
                val ex = json.optString("example", "")

                val synArray = json.optJSONArray("synonyms")
                val syns = mutableListOf<String>()
                if (synArray != null) {
                    for (i in 0 until synArray.length()) {
                        val s = synArray.optString(i, "")
                        if (s.isNotBlank()) syns.add(s)
                    }
                }

                val antArray = json.optJSONArray("antonyms")
                val ants = mutableListOf<String>()
                if (antArray != null) {
                    for (i in 0 until antArray.length()) {
                        val a = antArray.optString(i, "")
                        if (a.isNotBlank()) ants.add(a)
                    }
                }

                if (def.isNotBlank()) {
                    return WordDefinition(w, pos, def, syns, ants, ex)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse dictionary JSON", e)
        }
        return null
    }

    // Room DB Personal Dictionary Helpers
    fun getPersonalWordsFlow(context: Context): Flow<List<UserDictionaryEntity>> {
        return AppDatabase.getInstance(context).userDictionaryDao().getAllWordsFlow()
    }

    suspend fun addPersonalWord(context: Context, word: String, lang: String = "EN") {
        withContext(Dispatchers.IO) {
            val dao = AppDatabase.getInstance(context).userDictionaryDao()
            val existing = dao.findWord(word.lowercase().trim())
            if (existing != null) {
                dao.incrementFrequency(word.lowercase().trim())
            } else {
                dao.insertOrUpdate(
                    UserDictionaryEntity(
                        word = word.lowercase().trim(),
                        frequency = 1,
                        language = lang
                    )
                )
            }
        }
    }

    suspend fun deletePersonalWord(context: Context, word: String) {
        withContext(Dispatchers.IO) {
            AppDatabase.getInstance(context).userDictionaryDao().deleteWord(word.lowercase().trim())
        }
    }

    suspend fun clearPersonalDictionary(context: Context) {
        withContext(Dispatchers.IO) {
            AppDatabase.getInstance(context).userDictionaryDao().clearAll()
        }
    }
}
