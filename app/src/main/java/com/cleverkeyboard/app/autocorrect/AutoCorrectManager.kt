package com.cleverkeyboard.app.autocorrect

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.inputmethod.EditorInfo
import com.cleverkeyboard.app.data.db.AppDatabase
import com.cleverkeyboard.app.data.db.entity.UserDictionaryEntity
import com.cleverkeyboard.app.language.TamilTransliterationEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.ConcurrentHashMap

data class SuggestionItem(
    val text: String,
    val isCorrection: Boolean = false,
    val isPrimary: Boolean = false
)

object AutoCorrectManager {
    private const val TAG = "AutoCorrectManager"
    private const val PREFS_NAME = "clever_keyboard_autocorrect_prefs"
    private const val KEY_AUTO_CORRECT_ENABLED = "auto_correct_enabled"
    private const val KEY_ACTIVE_LANGUAGE = "autocorrect_active_language"
    private const val KEY_NEXT_WORD_PREDICTION = "next_word_prediction"
    private const val KEY_PERSONALIZED_SUGGESTIONS = "personalized_suggestions"
    private const val KEY_SWIPE_TYPING = "swipe_typing_enabled"
    private const val KEY_SMART_PUNCTUATION = "smart_punctuation_enabled"
    private const val PREFS_PERSONAL_DICT = "clever_personal_dict_prefs"

    const val LANG_EN = "EN"
    const val LANG_TA = "TA"
    const val LANG_TANG = "TANG"

    private var sharedPreferences: SharedPreferences? = null
    private var personalPrefs: SharedPreferences? = null

    // In-memory fast cache for personal vocabulary & frequently typed words
    private val frequentWordsMap = ConcurrentHashMap<String, Int>()

    init {
        // Seed frequent words and personalized vocabulary into fast in-memory map
        frequentWordsMap["cleverkeyboard"] = 25
        frequentWordsMap["crearemind"] = 25
        frequentWordsMap["ajith"] = 25
        frequentWordsMap["afshal"] = 25
        frequentWordsMap["kubernetes"] = 20
        frequentWordsMap["cybersecurity"] = 20
        frequentWordsMap["aws"] = 20
        frequentWordsMap["linux"] = 20
        frequentWordsMap["cloud computing"] = 20
        frequentWordsMap["gm"] = 20
        frequentWordsMap["bro"] = 20
        frequentWordsMap["btw"] = 20
        frequentWordsMap["idk"] = 20
        frequentWordsMap["gonna"] = 20
        frequentWordsMap["development"] = 15
        frequentWordsMap["project"] = 15
        frequentWordsMap["technology"] = 15
    }

    // In-memory cached dictionary maps
    private var englishDict: Map<String, String>? = null
    private var tanglishDict: Map<String, String>? = null
    private var tamilDict: Map<String, String>? = null

    // Next-Word Predictions Bigram Map (5 predictions each)
    private val ENGLISH_BIGRAMS = mapOf(
        "how" to listOf("are", "are you", "do", "can", "is"),
        "i" to listOf("am", "will", "have", "can", "think"),
        "i am" to listOf("going", "fine", "good", "ready", "here"),
        "im" to listOf("going", "fine", "good", "ready", "here"),
        "i'm" to listOf("going", "fine", "good", "ready", "here"),
        "what" to listOf("are", "is", "about", "do", "time"),
        "where" to listOf("are", "is", "have", "did", "were"),
        "who" to listOf("is", "are", "was", "will", "can"),
        "why" to listOf("are", "did", "not", "is", "would"),
        "thank" to listOf("you", "you so much", "god", "everyone", "all"),
        "thanks" to listOf("a lot", "bro", "so much", "for the update", "again"),
        "good" to listOf("morning", "night", "afternoon", "evening", "job"),
        "can" to listOf("you", "i", "we", "they", "this"),
        "let" to listOf("me", "us", "know", "them", "it"),
        "see" to listOf("you", "you soon", "later", "tomorrow", "this"),
        "call" to listOf("me", "you", "back", "him", "later"),
        "do" to listOf("you", "not", "it", "this", "that"),
        "are" to listOf("you", "there", "ready", "we", "they"),
        "will" to listOf("be", "do", "call", "come", "send"),
        "have" to listOf("a", "to", "you", "done", "been"),
        "nice" to listOf("to meet you", "one", "day", "work", "job"),
        "happy" to listOf("birthday", "to you", "anniversary", "diwali", "new year"),
        "clever" to listOf("keyboard", "type", "ai", "app", "tech"),
        "crearemind" to listOf("technologies", "team", "project", "app", "dev"),
        "project" to listOf("details", "update", "status", "deadline", "work"),
        "development" to listOf("team", "project", "work", "process", "app"),
        "technology" to listOf("stack", "solutions", "trends", "company", "hub")
    )

    private val TANGLISH_BIGRAMS = mapOf(
        "eppadi" to listOf("iruka", "irukiya", "irukinga", "sollu", "irundhadhu"),
        "epdi" to listOf("iruka", "irukiya", "irukinga", "sollu", "irundhadhu"),
        "enna" to listOf("panra", "da", "achu", "vishayam", "solreenga"),
        "illa" to listOf("da", "ma", "illaye", "pa", "bro"),
        "sapadu" to listOf("polaama", "mudinjidha", "va", "saapten", "edhuvum"),
        "saapadu" to listOf("polaama", "mudinjidha", "va", "saapten", "edhuvum"),
        "romba" to listOf("nalla", "thanks", "kashtam", "happy", "super"),
        "naan" to listOf("varen", "kelambren", "solren", "saapten", "irukken"),
        "enga" to listOf("iruka", "pora", "irukinga", "irukanga", "irundha"),
        "seri" to listOf("da", "ma", "pa", "bro", "ok"),
        "apdiya" to listOf("seri", "super", "ok", "appo", "apdiye"),
        "vaanga" to listOf("polam", "sapdalama", "pesalam", "paakalam", "seekiram"),
        "seekiram" to listOf("va", "varen", "mudikanum", "kelambu", "polaam"),
        "iniku" to listOf("enna", "plan", "meet", "leave", "office"),
        "naalaiki" to listOf("paakalam", "varen", "pesalam", "meet", "panna"),
        "thalaiva" to listOf("epdi", "super", "vanakkam", "va", "solra"),
        "machi" to listOf("epdi", "va", "da", "enna", "seri"),
        "super" to listOf("thala", "bro", "machan", "matter", "nalla")
    )

    fun getBigramPredictions(word: String, lang: String = LANG_EN): List<String> {
        val clean = word.trim().lowercase()
        val list = when (lang) {
            LANG_TANG -> TANGLISH_BIGRAMS[clean] ?: ENGLISH_BIGRAMS[clean]
            else -> ENGLISH_BIGRAMS[clean] ?: TANGLISH_BIGRAMS[clean]
        }
        return list ?: emptyList()
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return sharedPreferences ?: context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).also {
            sharedPreferences = it
        }
    }

    private fun getPersonalPrefs(context: Context): SharedPreferences {
        return personalPrefs ?: context.applicationContext.getSharedPreferences(PREFS_PERSONAL_DICT, Context.MODE_PRIVATE).also {
            personalPrefs = it
        }
    }

    fun isAutoCorrectEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_AUTO_CORRECT_ENABLED, true)
    }

    fun setAutoCorrectEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_AUTO_CORRECT_ENABLED, enabled).apply()
    }

    fun isNextWordPredictionEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_NEXT_WORD_PREDICTION, true)
    }

    fun setNextWordPredictionEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_NEXT_WORD_PREDICTION, enabled).apply()
    }

    fun isPersonalizedSuggestionsEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_PERSONALIZED_SUGGESTIONS, true)
    }

    fun setPersonalizedSuggestionsEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_PERSONALIZED_SUGGESTIONS, enabled).apply()
    }

    fun getActiveLanguage(context: Context): String {
        return getPrefs(context).getString(KEY_ACTIVE_LANGUAGE, LANG_EN) ?: LANG_EN
    }

    fun setActiveLanguage(context: Context, lang: String) {
        getPrefs(context).edit().putString(KEY_ACTIVE_LANGUAGE, lang).apply()
    }

    fun isAutoCorrectAllowed(info: EditorInfo?): Boolean {
        if (!com.cleverkeyboard.app.security.SensitiveFieldGuard.isSuggestionsAllowed(info)) {
            return false
        }
        if (info == null) return false
        val inputType = info.inputType
        val classType = inputType and EditorInfo.TYPE_MASK_CLASS
        val variation = inputType and EditorInfo.TYPE_MASK_VARIATION

        if (classType != EditorInfo.TYPE_CLASS_TEXT) {
            return false
        }

        if (variation == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD ||
            variation == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
            variation == EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD) {
            return false
        }

        if (variation == EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS ||
            variation == EditorInfo.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS) {
            return false
        }

        if (variation == EditorInfo.TYPE_TEXT_VARIATION_URI ||
            variation == EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME) {
            return false
        }

        if ((inputType and EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS) != 0) {
            return false
        }

        return true
    }

    private fun loadJsonMap(context: Context, filename: String): Map<String, String> {
        val map = LinkedHashMap<String, String>()
        try {
            context.assets.open(filename).use { stream ->
                BufferedReader(InputStreamReader(stream)).use { reader ->
                    val content = reader.readText()
                    val json = JSONObject(content)
                    val keys = json.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        val value = json.optString(key)
                        if (!value.isNullOrBlank()) {
                            map[key.lowercase().trim()] = value.trim()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading dictionary $filename", e)
        }
        return map
    }

    private var wordsList: List<String>? = null
    private val wordsLock = Any()

    /**
     * Preloads dictionaries in background on keyboard start.
     */
    fun preload(context: Context) {
        getEnglishMap(context)
        getTanglishMap(context)
        getTamilMap(context)
        getWordsList(context)

        // Load learned user vocabulary from Room DB into fast in-memory map
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val words = db.userDictionaryDao().getWordsForLanguage("EN") +
                        db.userDictionaryDao().getWordsForLanguage("TA") +
                        db.userDictionaryDao().getWordsForLanguage("TANG")
                for (entry in words) {
                    val w = entry.word.lowercase().trim()
                    if (w.isNotEmpty()) {
                        frequentWordsMap[w] = maxOf(frequentWordsMap[w] ?: 0, entry.frequency)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error loading initial Room dictionary", e)
            }
        }
    }

    @Synchronized
    fun getWordsList(context: Context): List<String> {
        wordsList?.let { return it }
        val list = mutableListOf<String>()
        try {
            context.assets.open("words.txt").bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val w = line.trim().lowercase()
                    if (w.isNotEmpty()) {
                        list.add(w)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading words.txt", e)
        }
        wordsList = list
        return list
    }

    internal fun setTestWordsList(words: List<String>) {
        wordsList = words
    }

    @Synchronized
    private fun getEnglishMap(context: Context): Map<String, String> {
        return englishDict ?: loadJsonMap(context, "english_dict.json").also {
            englishDict = it
        }
    }

    @Synchronized
    private fun getTanglishMap(context: Context): Map<String, String> {
        return tanglishDict ?: loadJsonMap(context, "tanglish_dict.json").also {
            tanglishDict = it
        }
    }

    @Synchronized
    private fun getTamilMap(context: Context): Map<String, String> {
        return tamilDict ?: loadJsonMap(context, "tamil_dict.json").also {
            tamilDict = it
        }
    }

    /**
     * Records when user taps/selects a suggestion.
     * Updates fast in-memory map and persists to Room Database asynchronously.
     * Strictly blocked on password and sensitive fields.
     */
    fun recordWordSelection(context: Context, word: String, info: EditorInfo? = null) {
        if (!com.cleverkeyboard.app.security.SensitiveFieldGuard.isLearningAllowed(info)) {
            return
        }
        val clean = word.lowercase().trim()
        if (clean.length < 2) return

        val newFreq = frequentWordsMap.compute(clean) { _, v -> (v ?: 0) + 1 } ?: 1

        val prefs = getPersonalPrefs(context)
        val count = prefs.getInt(clean, 0) + 1
        prefs.edit().putInt(clean, count).apply()

        // Persist to Room UserDictionaryEntity
        val lang = getActiveLanguage(context)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val dao = db.userDictionaryDao()
                val updated = dao.incrementFrequency(clean)
                if (updated == 0) {
                    dao.insertOrUpdate(
                        UserDictionaryEntity(
                            word = clean,
                            frequency = newFreq,
                            language = lang
                        )
                    )
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error persisting word to UserDictionary Room DB", e)
            }
        }
    }

    fun isPersonalWord(word: String): Boolean {
        val lower = word.lowercase().trim()
        return (frequentWordsMap[lower] ?: 0) >= 1
    }

    fun getPersonalWordsInMemory(prefix: String): List<String> {
        val list = mutableListOf<String>()
        for ((w, freq) in frequentWordsMap) {
            if (w.startsWith(prefix) && freq >= 1) {
                list.add(w)
            }
        }
        return list.sortedByDescending { frequentWordsMap[it] ?: 0 }
    }

    private fun getPersonalWords(context: Context, prefix: String): List<String> {
        val list = mutableListOf<String>()
        // From fast in-memory cache
        for ((w, freq) in frequentWordsMap) {
            if (w.startsWith(prefix) && freq >= 1) {
                list.add(w)
            }
        }
        // From shared prefs fallback
        val prefs = getPersonalPrefs(context)
        for ((word, count) in prefs.all) {
            if (word.startsWith(prefix) && (count as? Int ?: 0) >= 2 && !list.contains(word)) {
                list.add(word)
            }
        }
        return list.sortedByDescending { frequentWordsMap[it] ?: prefs.getInt(it, 0) }
    }

    // QWERTY physical key neighbors mapping for touch-typo scoring
    private val QWERTY_NEIGHBORS = mapOf(
        'q' to listOf('w', 'a', 's'),
        'w' to listOf('q', 'e', 'a', 's', 'd'),
        'e' to listOf('w', 'r', 's', 'd', 'f'),
        'r' to listOf('e', 't', 'd', 'f', 'g'),
        't' to listOf('r', 'y', 'f', 'g', 'h'),
        'y' to listOf('t', 'u', 'g', 'h', 'j'),
        'u' to listOf('y', 'i', 'h', 'j', 'k'),
        'i' to listOf('u', 'o', 'j', 'k', 'l'),
        'o' to listOf('i', 'p', 'k', 'l'),
        'p' to listOf('o', 'l'),
        'a' to listOf('q', 'w', 's', 'z'),
        's' to listOf('a', 'd', 'w', 'e', 'z', 'x'),
        'd' to listOf('s', 'f', 'e', 'r', 'x', 'c'),
        'f' to listOf('d', 'g', 'r', 't', 'c', 'v'),
        'g' to listOf('f', 'h', 't', 'y', 'v', 'b'),
        'h' to listOf('g', 'j', 'y', 'u', 'b', 'n'),
        'j' to listOf('h', 'k', 'u', 'i', 'n', 'm'),
        'k' to listOf('j', 'l', 'i', 'o', 'm'),
        'l' to listOf('k', 'o', 'p'),
        'z' to listOf('a', 's', 'x'),
        'x' to listOf('z', 's', 'd', 'c'),
        'c' to listOf('x', 'd', 'f', 'v'),
        'v' to listOf('c', 'f', 'g', 'b'),
        'b' to listOf('v', 'g', 'h', 'n'),
        'n' to listOf('b', 'h', 'j', 'm'),
        'm' to listOf('n', 'j', 'k')
    )

    /**
     * Looks up correction for a word for Space press auto-replace.
     */
    fun getCorrection(context: Context? = null, rawWord: String): String? {
        if (context != null && !isAutoCorrectEnabled(context)) return null
        if (rawWord.length < 2) return null
        val lower = rawWord.lowercase().trim()

        // Never autocorrect user-learned personal words, company names, or custom terminology
        if (isPersonalWord(lower)) return null

        val lang = if (context != null) getActiveLanguage(context) else LANG_EN

        // 1. Comprehensive curated primary corrections
        when (lower) {
            "teh" -> return matchCase(rawWord, "the")
            "recieve" -> return matchCase(rawWord, "receive")
            "becuase" -> return matchCase(rawWord, "because")
            "thier" -> return matchCase(rawWord, "their")
            "helllo" -> return matchCase(rawWord, "hello")
            "helo" -> return matchCase(rawWord, "hello")
            "wierd" -> return matchCase(rawWord, "weird")
            "wrld" -> return matchCase(rawWord, "world")
            "goverment" -> return matchCase(rawWord, "government")
            "goin" -> return matchCase(rawWord, "going")
            "wats" -> return matchCase(rawWord, "whats")
            "corrcg" -> return matchCase(rawWord, "correct")
            "sapadu" -> return matchCase(rawWord, "saapadu")
            "epdi" -> return matchCase(rawWord, "eppadi")
            "seperate" -> return matchCase(rawWord, "separate")
            "definately" -> return matchCase(rawWord, "definitely")
            "untill" -> return matchCase(rawWord, "until")
            "truely" -> return matchCase(rawWord, "truly")
            "dont" -> return matchCase(rawWord, "don't")
            "cant" -> return matchCase(rawWord, "can't")
            "wont" -> return matchCase(rawWord, "won't")
            "im" -> return matchCase(rawWord, "I'm")
            "youre" -> return matchCase(rawWord, "you're")
            "doesnt" -> return matchCase(rawWord, "doesn't")
            "didnt" -> return matchCase(rawWord, "didn't")
            "isnt" -> return matchCase(rawWord, "isn't")
            "arent" -> return matchCase(rawWord, "aren't")
            "wasnt" -> return matchCase(rawWord, "wasn't")
            "neccessary" -> return matchCase(rawWord, "necessary")
            "tommorrow" -> return matchCase(rawWord, "tomorrow")
            "accross" -> return matchCase(rawWord, "across")
            "beleive" -> return matchCase(rawWord, "believe")
            "suprise" -> return matchCase(rawWord, "surprise")
            "grammer" -> return matchCase(rawWord, "grammar")
            "goodmorning" -> return matchCase(rawWord, "good morning")
            "thankyou" -> return matchCase(rawWord, "thank you")
            "kinf" -> return matchCase(rawWord, "king")
            "wprk" -> return matchCase(rawWord, "work")
        }

        if (lang == LANG_TANG) {
            val translit = TamilTransliterationEngine.transliterateWord(lower)
            if (translit.isNotEmpty() && !translit.equals(lower, ignoreCase = true)) {
                return translit
            }
        }

        val target = if (context != null) {
            when (lang) {
                LANG_TA -> getTamilMap(context)[lower]
                LANG_TANG -> getTanglishMap(context)[lower] ?: getEnglishMap(context)[lower]
                else -> getEnglishMap(context)[lower] ?: getTanglishMap(context)[lower]
            }
        } else {
            englishDict?.get(lower) ?: tanglishDict?.get(lower)
        }

        if (target != null && !target.equals(lower, ignoreCase = true)) {
            return matchCase(rawWord, target)
        }

        // 2. Intelligent Dictionary & Levenshtein + QWERTY Proximity Auto-Correction
        if (context != null && lang == LANG_EN) {
            val allWords = getWordsList(context)
            // If already a valid word in dictionary, do not autocorrect
            if (allWords.contains(lower)) {
                return null
            }

            val qLen = lower.length
            var bestCandidate: String? = null
            var bestScore = -1

            val searchLimit = 5000 // Scan primary common vocabulary in words.txt
            var count = 0
            for (w in allWords) {
                count++
                if (count > searchLimit && bestCandidate != null) break

                val lenDiff = Math.abs(w.length - qLen)
                if (lenDiff > 1 && (qLen < 5 || lenDiff > 2)) continue

                val dist = levenshteinDistance(lower, w)
                if (dist == 1 || (dist == 2 && qLen >= 5)) {
                    var score = if (dist == 1) 100 else 40
                    if (w[0] == lower[0]) score += 30
                    if (w.last() == lower.last()) score += 15

                    // Frequency rank bonus (earlier in words.txt = more common)
                    val freqBonus = maxOf(0, 50 - (count / 100))
                    score += freqBonus

                    // QWERTY physical key proximity bonus
                    if (dist == 1 && qLen == w.length) {
                        for (idx in 0 until qLen) {
                            if (lower[idx] != w[idx]) {
                                val c1 = lower[idx]
                                val c2 = w[idx]
                                if (QWERTY_NEIGHBORS[c1]?.contains(c2) == true) {
                                    score += 60 // High confidence adjacent touch typo!
                                }
                                break
                            }
                        }
                    }

                    if (score > bestScore) {
                        bestScore = score
                        bestCandidate = w
                    }
                }
            }

            if (bestCandidate != null && bestScore >= 110) {
                return matchCase(rawWord, bestCandidate)
            }
        }

        return null
    }

    /**
     * Fast Levenshtein Edit Distance calculation.
     */
    fun levenshteinDistance(s1: String, s2: String): Int {
        if (s1 == s2) return 0
        val len1 = s1.length
        val len2 = s2.length
        if (len1 == 0) return len2
        if (len2 == 0) return len1
        if (Math.abs(len1 - len2) > 2) return 99

        var prev = IntArray(len2 + 1) { it }
        var curr = IntArray(len2 + 1)

        for (i in 0 until len1) {
            curr[0] = i + 1
            val c1 = s1[i]
            for (j in 0 until len2) {
                val c2 = s2[j]
                val cost = if (c1 == c2) 0 else 1
                curr[j + 1] = minOf(
                    curr[j] + 1,
                    prev[j + 1] + 1,
                    prev[j] + cost
                )
            }
            val temp = prev
            prev = curr
            curr = temp
        }
        return prev[len2]
    }

    private fun getCuratedTypoSuggestions(query: String): List<String>? {
        return when (query) {
            "wrld" -> listOf("world", "word", "would", "worth", "work")
            "helo" -> listOf("hello", "help", "held", "hero", "helm")
            "helllo" -> listOf("hello", "help", "held", "hell", "helicopter")
            "goverment" -> listOf("government", "governments", "governing", "governor", "govern")
            "teh" -> listOf("the", "they", "them", "then", "their")
            "recieve" -> listOf("receive", "received", "receiver", "receiving", "receives")
            "becuase" -> listOf("because", "become", "became", "causes", "cause")
            "thier" -> listOf("their", "there", "they're", "theirs", "them")
            "wierd" -> listOf("weird", "wired", "wider", "wild", "wind")
            "goin" -> listOf("going", "gone", "goes", "good", "got")
            "wats" -> listOf("whats", "what", "watch", "water", "wait")
            "corrcg" -> listOf("correct", "corrected", "correcting", "corrections", "corrector")
            "sapadu" -> listOf("saapadu", "sapten", "saptiya", "seri", "polam")
            "epdi" -> listOf("eppadi", "eppo", "epdiya", "enna", "enga")
            "seperate" -> listOf("separate", "separated", "separates", "separation", "separating")
            "definately" -> listOf("definitely", "definite", "definition", "definitive", "defined")
            "untill" -> listOf("until", "unit", "united", "unite", "unity")
            "truely" -> listOf("truly", "true", "trust", "truth", "trusted")
            "dont" -> listOf("don't", "done", "down", "doing", "does")
            "cant" -> listOf("can't", "can", "cannot", "came", "call")
            "wont" -> listOf("won't", "want", "went", "work", "word")
            "im" -> listOf("I'm", "in", "is", "it", "into")
            "youre" -> listOf("you're", "your", "yours", "you", "young")
            "doesnt" -> listOf("doesn't", "does", "done", "do", "down")
            "didnt" -> listOf("didn't", "did", "do", "doing", "done")
            "isnt" -> listOf("isn't", "is", "it", "in", "into")
            "goodmorning" -> listOf("good morning", "morning", "good", "great", "day")
            "thankyou" -> listOf("thank you", "thanks", "thank", "you", "great")
            "vaazhkai" -> listOf("vaazhkai", "vaazhga", "vaazhvom", "vaazha", "vaazhndhu")
            else -> null
        }
    }

    /**
     * Dynamic Smart Suggestions on EVERY keypress!
     * Returns exactly 5 distinct items: [s1, s2, s3 (Center Primary), s4, s5]
     * Never duplicates!
     */
    fun getDynamicSuggestions(
        context: Context? = null,
        textBeforeCursor: String
    ): List<SuggestionItem> {
        val lang = if (context != null) getActiveLanguage(context) else LANG_EN

        // 1. Check if user is in middle of typing a word
        val currentWordMatch = Regex("[\\p{L}\\p{M}\\p{N}']+$").find(textBeforeCursor)
        val currentWord = currentWordMatch?.value ?: ""

        // Case A: No active word typing (blank field or space) -> Next-Word Prediction (5 items)
        if (currentWord.isEmpty()) {
            if (context == null || isNextWordPredictionEnabled(context)) {
                val wordsBefore = textBeforeCursor.trim().split(Regex("\\s+"))
                val lastWord = wordsBefore.lastOrNull()?.lowercase() ?: ""
                val lastTwo = if (wordsBefore.size >= 2) {
                    "${wordsBefore[wordsBefore.size - 2].lowercase()} $lastWord"
                } else null

                val predictions = when (lang) {
                    LANG_TANG -> {
                        (lastTwo?.let { TANGLISH_BIGRAMS[it] } ?: TANGLISH_BIGRAMS[lastWord])
                            ?: (lastTwo?.let { ENGLISH_BIGRAMS[it] } ?: ENGLISH_BIGRAMS[lastWord])
                    }
                    else -> {
                        (lastTwo?.let { ENGLISH_BIGRAMS[it] } ?: ENGLISH_BIGRAMS[lastWord])
                            ?: (lastTwo?.let { TANGLISH_BIGRAMS[it] } ?: TANGLISH_BIGRAMS[lastWord])
                    }
                }

                if (!predictions.isNullOrEmpty()) {
                    val distinctPreds = mutableListOf<String>()
                    for (p in predictions) {
                        if (p.isNotBlank() && !distinctPreds.any { it.equals(p, ignoreCase = true) }) {
                            distinctPreds.add(p)
                        }
                        if (distinctPreds.size >= 5) break
                    }

                    // Fallbacks if fewer than 5
                    val defaultFallbacks = when (lang) {
                        LANG_TA -> listOf("நான்", "நீங்கள்", "என்ன", "ஆமாம்", "இல்லை", "எப்படி", "நன்றி")
                        LANG_TANG -> listOf("naan", "enna", "eppadi", "seri", "apdiya", "super", "vaanga")
                        else -> listOf("I", "you", "the", "and", "to", "in", "it", "for")
                    }
                    for (fb in defaultFallbacks) {
                        if (distinctPreds.size >= 5) break
                        if (!distinctPreds.any { it.equals(fb, ignoreCase = true) }) {
                            distinctPreds.add(fb)
                        }
                    }

                    if (distinctPreds.size >= 5) {
                        // Layout: [s1, s2, s3 (Primary center), s4, s5]
                        // Map top 5 predictions to [1, 2, 0, 3, 4] so top prediction is centered
                        val center = distinctPreds[0]
                        val s1 = distinctPreds[1]
                        val s2 = distinctPreds[2]
                        val s4 = distinctPreds[3]
                        var s5 = distinctPreds[4]

                        // Emoji suggestion for the completed word
                        val emojiMatches = com.cleverkeyboard.app.emoji.EmojiManager.searchEmojis(lastWord)
                        if (emojiMatches.isNotEmpty()) {
                            s5 = emojiMatches[0]
                        }

                        return listOf(
                            SuggestionItem(text = s1, isPrimary = false),
                            SuggestionItem(text = s2, isPrimary = false),
                            SuggestionItem(text = center, isPrimary = true),
                            SuggestionItem(text = s4, isPrimary = false),
                            SuggestionItem(text = s5, isPrimary = false)
                        )
                    }
                }
            }

            // Defaults based on language (5 items each)
            return when (lang) {
                LANG_TA -> listOf(
                    SuggestionItem("ஆமாம்"),
                    SuggestionItem("நான்"),
                    SuggestionItem("நீங்கள்", isPrimary = true),
                    SuggestionItem("என்ன"),
                    SuggestionItem("இல்லை")
                )
                LANG_TANG -> listOf(
                    SuggestionItem("seri"),
                    SuggestionItem("naan"),
                    SuggestionItem("enna", isPrimary = true),
                    SuggestionItem("eppadi"),
                    SuggestionItem("apdiya")
                )
                else -> listOf(
                    SuggestionItem("and"),
                    SuggestionItem("I"),
                    SuggestionItem("you", isPrimary = true),
                    SuggestionItem("the"),
                    SuggestionItem("to")
                )
            }
        }

        // Case B: User is actively typing `currentWord`
        val lower = currentWord.lowercase().trim()

        // 0. Tanglish Transliteration (When in Tanglish mode)
        if (lang == LANG_TANG && lower.length >= 2) {
            val (primary, secondary, original) = TamilTransliterationEngine.getTransliterationCandidates(currentWord)
            if (primary.isNotEmpty() && !primary.equals(original, ignoreCase = true)) {
                val tanglishFallbacks = listOf("seri", "apdiya", "super", "nalla", "vaanga")
                val remaining = tanglishFallbacks.filter { !it.equals(primary, true) && !it.equals(secondary, true) && !it.equals(original, true) }
                return listOf(
                    SuggestionItem(text = original, isPrimary = false),
                    SuggestionItem(text = secondary.ifBlank { remaining.getOrElse(0) { "seri" } }, isPrimary = false),
                    SuggestionItem(text = primary, isCorrection = true, isPrimary = true),
                    SuggestionItem(text = remaining.getOrElse(1) { "apdiya" }, isPrimary = false),
                    SuggestionItem(text = remaining.getOrElse(2) { "super" }, isPrimary = false)
                )
            }
        }

        // 1. High-priority curated typo suggestions (e.g. teh -> the, they, them, then, their)
        val curated = getCuratedTypoSuggestions(lower)
        if (curated != null && curated.size >= 5) {
            val center = matchCase(currentWord, curated[0])
            val s1 = matchCase(currentWord, curated[1])
            val s2 = matchCase(currentWord, curated[2])
            val s4 = matchCase(currentWord, curated[3])
            val s5 = matchCase(currentWord, curated[4])
            return listOf(
                SuggestionItem(text = s1, isPrimary = false),
                SuggestionItem(text = s2, isPrimary = false),
                SuggestionItem(text = center, isCorrection = true, isPrimary = true),
                SuggestionItem(text = s4, isPrimary = false),
                SuggestionItem(text = s5, isPrimary = false)
            )
        }

        // 2. High-priority prefix patterns specified by user
        val customMatches = getCuratedPrefixMatches(lower, lang)
        if (customMatches != null && customMatches.size >= 3) {
            val c0 = matchCase(currentWord, customMatches[0])
            val c1 = matchCase(currentWord, customMatches[1])
            val c2 = matchCase(currentWord, customMatches[2])
            val c3 = if (customMatches.size > 3) matchCase(currentWord, customMatches[3]) else currentWord
            val c4 = if (customMatches.size > 4) matchCase(currentWord, customMatches[4]) else "ok"
            return listOf(
                SuggestionItem(text = c1, isPrimary = false),
                SuggestionItem(text = c2, isPrimary = false),
                SuggestionItem(text = c0, isPrimary = true),
                SuggestionItem(text = c3, isPrimary = false),
                SuggestionItem(text = c4, isPrimary = false)
            )
        }

        // 3. Known Typo Correction check
        val typoCorrection = getCorrection(context, currentWord)

        // 4. Gather Candidates using Dictionary + Levenshtein Distance
        val candidatePool = mutableListOf<String>()

        if (typoCorrection != null) {
            candidatePool.add(typoCorrection)
        }

        // Personal dictionary learned words (frequent words)
        if (context == null || isPersonalizedSuggestionsEnabled(context)) {
            val personal = if (context != null) getPersonalWords(context, lower) else getPersonalWordsInMemory(lower)
            candidatePool.addAll(personal)
        }

        // Search 10k+ words.txt using Levenshtein Edit Distance
        val allWords = if (context != null) getWordsList(context) else (wordsList ?: emptyList())
        val dist1 = mutableListOf<String>()
        val dist2 = mutableListOf<String>()
        val prefixes = mutableListOf<String>()

        val qLen = lower.length
        for (w in allWords) {
            if (w.startsWith(lower) && !w.equals(lower, ignoreCase = true)) {
                if (prefixes.size < 6) prefixes.add(w)
            }
            if (Math.abs(w.length - qLen) <= 2) {
                val d = levenshteinDistance(lower, w)
                if (d == 1 && dist1.size < 8) {
                    dist1.add(w)
                } else if (d == 2 && dist2.size < 8) {
                    dist2.add(w)
                }
            }
            if (dist1.size >= 6 && prefixes.size >= 4) break
        }

        candidatePool.addAll(dist1)
        candidatePool.addAll(prefixes)
        candidatePool.addAll(dist2)

        // Also check legacy prefix maps if needed
        val dictMatches = if (context != null) findPrefixMatches(context, lower, lang) else emptyList()
        candidatePool.addAll(dictMatches)

        // Filter out duplicates (case-insensitive)
        val distinctCandidates = mutableListOf<String>()
        for (cand in candidatePool) {
            val formatted = matchCase(currentWord, cand)
            if (!distinctCandidates.any { it.equals(formatted, ignoreCase = true) } &&
                !formatted.equals(currentWord, ignoreCase = true)) {
                distinctCandidates.add(formatted)
            }
            if (distinctCandidates.size >= 8) break
        }

        // Build guaranteed 5 distinct suggestions:
        val distinctItems = mutableListOf<String>()
        if (typoCorrection != null) {
            distinctItems.add(typoCorrection)
        } else if (distinctCandidates.isNotEmpty()) {
            distinctItems.add(distinctCandidates[0])
        } else {
            distinctItems.add(currentWord)
        }

        for (cand in distinctCandidates) {
            if (!distinctItems.any { it.equals(cand, ignoreCase = true) }) {
                distinctItems.add(cand)
            }
            if (distinctItems.size >= 5) break
        }

        // If user typed word is not in suggestions, include it as alternative so user can keep it
        if (!distinctItems.any { it.equals(currentWord, ignoreCase = true) }) {
            if (distinctItems.size < 5) {
                distinctItems.add(currentWord)
            }
        }

        // Fallbacks to guarantee 5 DIFFERENT words (never duplicate!)
        val fallbacks = when (lang) {
            LANG_TA -> listOf("நான்", "நீங்கள்", "என்ன", "ஆமாம்", "இல்லை", "எப்படி", "நன்றி")
            LANG_TANG -> listOf("naan", "enna", "eppadi", "seri", "apdiya", "super", "vaanga")
            else -> listOf("the", "you", "I", "and", "to", "world", "hello", "good")
        }
        for (fb in fallbacks) {
            if (distinctItems.size >= 5) break
            val formatted = matchCase(currentWord, fb)
            if (!distinctItems.any { it.equals(formatted, ignoreCase = true) }) {
                distinctItems.add(formatted)
            }
        }

        // Final safe 5 items:
        // centerWord (distinctItems[0]) is the best correction (bold, primary)
        val centerWord = distinctItems[0]
        val s1 = distinctItems.getOrElse(1) { if (!currentWord.equals(centerWord, true)) currentWord else "and" }
        val s2 = distinctItems.getOrElse(2) { "I" }
        val s4 = distinctItems.getOrElse(3) { "to" }
        var s5 = distinctItems.getOrElse(4) { "the" }

        // Emoji matching for typed word
        val searchWord = if (currentWord.isNotBlank()) currentWord else centerWord
        val emojiMatches = com.cleverkeyboard.app.emoji.EmojiManager.searchEmojis(searchWord)
        if (emojiMatches.isNotEmpty()) {
            s5 = emojiMatches[0]
        }

        val isCenterCorrection = typoCorrection != null || !centerWord.equals(currentWord, ignoreCase = true)

        return listOf(
            SuggestionItem(text = s1, isPrimary = false),
            SuggestionItem(text = s2, isPrimary = false),
            SuggestionItem(text = centerWord, isCorrection = isCenterCorrection, isPrimary = true),
            SuggestionItem(text = s4, isPrimary = false),
            SuggestionItem(text = s5, isPrimary = false)
        )
    }

    private fun getCuratedPrefixMatches(prefix: String, lang: String): List<String>? {
        if (lang == LANG_TANG || lang == LANG_EN) {
            when (prefix) {
                "ep" -> return listOf("eppadi", "eppo", "eppovum")
                "sap" -> return listOf("saapadu", "saptiya", "sapten")
                "enna" -> return listOf("enna panra", "enna da", "enna achu")
                "illa" -> return listOf("illa da", "illa ma", "illaye")
                "pan" -> return listOf("panra", "panren", "panniten")
                "va" -> return listOf("va", "varen", "varriya")
                "th" -> return listOf("the", "this", "that")
                "go" -> return listOf("going", "good", "got")
            }
        }
        return null
    }

    private fun findPrefixMatches(context: Context, prefix: String, lang: String): List<String> {
        val results = mutableListOf<String>()

        val primaryMap = when (lang) {
            LANG_TA -> getTamilMap(context)
            LANG_TANG -> getTanglishMap(context)
            else -> getEnglishMap(context)
        }

        for (key in primaryMap.keys) {
            if (key.startsWith(prefix)) {
                val value = primaryMap[key] ?: key
                if (!results.contains(value)) {
                    results.add(value)
                }
            }
            if (results.size >= 5) return results
        }

        // Secondary fallback
        if (lang == LANG_TANG) {
            val enMap = getEnglishMap(context)
            for (key in enMap.keys) {
                if (key.startsWith(prefix)) {
                    val value = enMap[key] ?: key
                    if (!results.contains(value)) results.add(value)
                }
                if (results.size >= 5) break
            }
        } else if (lang == LANG_EN) {
            val tangMap = getTanglishMap(context)
            for (key in tangMap.keys) {
                if (key.startsWith(prefix)) {
                    val value = tangMap[key] ?: key
                    if (!results.contains(value)) results.add(value)
                }
                if (results.size >= 5) break
            }
        }

        return results
    }

    fun isSwipeTypingEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_SWIPE_TYPING, false)
    }

    fun setSwipeTypingEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_SWIPE_TYPING, enabled).apply()
    }

    fun isSmartPunctuationEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_SMART_PUNCTUATION, true)
    }

    fun setSmartPunctuationEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_SMART_PUNCTUATION, enabled).apply()
    }

    fun matchSwipeTrajectory(context: Context?, visitedChars: List<Char>): List<String> {
        if (visitedChars.size < 2) return emptyList()

        val startChar = visitedChars.first().lowercaseChar()
        val endChar = visitedChars.last().lowercaseChar()
        val words = wordsList ?: if (context != null) getWordsList(context) else emptyList()

        // Deduplicate consecutive letters in visitedChars
        val collapsedVisited = mutableListOf<Char>()
        for (c in visitedChars) {
            val lower = c.lowercaseChar()
            if (collapsedVisited.isEmpty() || collapsedVisited.last() != lower) {
                collapsedVisited.add(lower)
            }
        }

        fun isSubsequence(w: String, visited: List<Char>): Boolean {
            var vIdx = 0
            for (i in w.indices) {
                val c = w[i]
                var found = false
                while (vIdx < visited.size) {
                    if (visited[vIdx] == c) {
                        found = true
                        vIdx++
                        break
                    }
                    vIdx++
                }
                if (!found) return false
            }
            return true
        }

        val directMatches = mutableListOf<String>()
        for (word in words) {
            if (word.length < 2) continue
            if (word.first() != startChar || word.last() != endChar) continue

            // Collapse consecutive letters in candidate word (e.g. "hello" -> "helo")
            val collapsedWord = StringBuilder()
            for (ch in word) {
                if (collapsedWord.isEmpty() || collapsedWord.last() != ch) {
                    collapsedWord.append(ch)
                }
            }

            if (isSubsequence(collapsedWord.toString(), collapsedVisited)) {
                directMatches.add(word)
                if (directMatches.size >= 15) break
            }
        }

        // Rank by length closeness to the swipe sample count
        directMatches.sortBy { Math.abs(it.length - collapsedVisited.size) }
        return directMatches.take(5)
    }

    fun matchCase(original: String, target: String): String {
        return when {
            original.all { it.isUpperCase() } -> target.uppercase()
            original.firstOrNull()?.isUpperCase() == true -> target.replaceFirstChar { it.uppercase() }
            else -> target
        }
    }
}
