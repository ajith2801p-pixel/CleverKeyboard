package com.cleverkeyboard.app.intelligence

import android.content.Context
import com.cleverkeyboard.app.language.TamilTransliterationEngine
import com.cleverkeyboard.app.network.AiService

data class TanglishConversion(
    val input: String,
    val tanglishText: String,
    val englishTranslation: String,
    val tamilScript: String,
    val isLocal: Boolean
)

class TanglishLanguageEngine private constructor(private val context: Context) {

    private val aiService = AiService.getInstance()
    private val personalVocab = PersonalVocabularyManager.getInstance(context)

    companion object {
        @Volatile
        private var instance: TanglishLanguageEngine? = null

        fun getInstance(context: Context): TanglishLanguageEngine {
            return instance ?: synchronized(this) {
                instance ?: TanglishLanguageEngine(context.applicationContext).also { instance = it }
            }
        }

        // Comprehensive dictionary of high-frequency Tanglish conversational phrases
        // Format: key (tanglish lowercase) -> Pair(English translation, Tamil script)
        private val TANGLISH_PHRASES = mapOf(
            "naan sapten" to Pair("I have eaten", "நான் சாப்பிட்டேன்"),
            "saapten" to Pair("I ate / I have eaten", "சாப்பிட்டேன்"),
            "saapacha" to Pair("Did you eat?", "சாப்டாச்சா"),
            "enna panra" to Pair("What are you doing?", "என்ன பண்ற"),
            "enna pandra" to Pair("What are you doing?", "என்ன பண்ற"),
            "eppo varuva" to Pair("When will you come?", "எப்போ வருவ"),
            "eppo varreenga" to Pair("When are you coming?", "எப்போ வரீங்க"),
            "romba nandri" to Pair("Thank you very much", "ரொம்ப நன்றி"),
            "nandri" to Pair("Thank you", "நன்றி"),
            "nalla irukiya" to Pair("Are you doing well?", "நல்லா இருக்கியா"),
            "nalla irukken" to Pair("I am doing well", "நல்லா இருக்கேன்"),
            "aprom peslam" to Pair("Let's talk later", "அப்புறம் பேசலாம்"),
            "seri da" to Pair("Alright bro", "சரி டா"),
            "sari da" to Pair("Alright bro", "சரி டா"),
            "seri pa" to Pair("Alright", "சரி பா"),
            "vera level" to Pair("Next level / Amazing", "வேற லெவல்"),
            "enga iruka" to Pair("Where are you?", "எங்க இருக்க"),
            "enga pora" to Pair("Where are you going?", "எங்க போற"),
            "vaanga" to Pair("Welcome / Come in", "வாங்க"),
            "vaa da" to Pair("Come bro", "வா டா"),
            "polaama" to Pair("Shall we go?", "போலாமா"),
            "mudinjidha" to Pair("Is it finished?", "முடிஞ்சதா"),
            "mudichuten" to Pair("I finished it", "முடிச்சிட்டேன்"),
            "kovama irukiya" to Pair("Are you angry?", "கோவமா இருக்கியா"),
            "kavala padadha" to Pair("Don't worry", "கவலைப்படாத"),
            "paathukalam" to Pair("We will handle it", "பாத்துக்கலாம்"),
            "puriyudhu" to Pair("I understand", "புரியுது"),
            "purila" to Pair("I don't understand", "புரியல"),
            "theriyum" to Pair("I know", "தெரியும்"),
            "theriyadhu" to Pair("I don't know", "தெரியாது"),
            "seekiram vaa" to Pair("Come quickly", "சீக்கிரம் வா"),
            "apdiya" to Pair("Is that so?", "அப்படியா"),
            "aama" to Pair("Yes", "ஆமா"),
            "illa" to Pair("No", "இல்ல"),
            "edhuvum illa" to Pair("Nothing", "எதுவும் இல்ல"),
            "machan" to Pair("Dude / Bro / Friend", "மச்சான்"),
            "thalaiva" to Pair("Boss / Leader", "தலைவா")
        )

        fun matchLocalTanglish(text: String): TanglishConversion? {
            val clean = text.trim().lowercase()
            val match = TANGLISH_PHRASES[clean]
            if (match != null) {
                return TanglishConversion(
                    input = text,
                    tanglishText = text.trim(),
                    englishTranslation = match.first,
                    tamilScript = match.second,
                    isLocal = true
                )
            }
            return null
        }
    }

    /**
     * Resolves Tanglish input locally with instant response (<2ms).
     */
    fun convertLocal(text: String): TanglishConversion? {
        val staticMatch = matchLocalTanglish(text)
        if (staticMatch != null) return staticMatch

        val clean = text.trim().lowercase()
        // Check transliteration for single words via TamilTransliterationEngine
        if (!clean.contains(" ")) {
            val transliterated = TamilTransliterationEngine.transliterateWord(clean)
            if (transliterated != clean && transliterated.isNotBlank()) {
                return TanglishConversion(
                    input = text,
                    tanglishText = text.trim(),
                    englishTranslation = text.trim(), // Keep clean word
                    tamilScript = transliterated,
                    isLocal = true
                )
            }
        }

        return null
    }

    /**
     * Translates & Transliterates Tanglish using Google Gemini for complete sentences.
     */
    suspend fun convertCloud(text: String, apiKey: String): Result<TanglishConversion> {
        val local = convertLocal(text)
        if (local != null) return Result.success(local)

        return aiService.convertTanglish(text, apiKey).map { res ->
            // Learn user-approved Tanglish phrases for personal vocabulary
            personalVocab.learnWord(text.trim(), language = "TANG")

            TanglishConversion(
                input = text,
                tanglishText = res.originalSpoken.ifBlank { text },
                englishTranslation = res.english.ifBlank { text },
                tamilScript = res.tamil.ifBlank { TamilTransliterationEngine.transliterateSentence(text) },
                isLocal = false
            )
        }
    }
}
