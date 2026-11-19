package com.cleverkeyboard.app.language

import java.util.Locale

/**
 * Phonetic Tanglish-to-Tamil Transliteration Engine.
 * Converts Romanized Tamil (Tanglish) into pure Unicode Tamil script.
 * Supports both rule-based phonetic conversion and colloquial word mapping.
 */
object TamilTransliterationEngine {

    // Common colloquial Tanglish words mapped to proper Tamil words & questions
    private val COLLOQUIAL_MAP = mapOf(
        "vanakkam" to "வணக்கம்",
        "epdi" to "எப்படி",
        "eppadi" to "எப்படி",
        "iruka" to "இருக்க?",
        "irukaa" to "இருக்கா?",
        "irukiya" to "இருக்கியா?",
        "irukinga" to "இருக்கீங்களா?",
        "naan" to "நான்",
        "nan" to "நான்",
        "nee" to "நீ",
        "neenga" to "நீங்க",
        "sapten" to "சாப்பிட்டேன்",
        "saapten" to "சாப்பிட்டேன்",
        "sapteya" to "சாப்பிட்டியா?",
        "saptiya" to "சாப்பிட்டியா?",
        "saaptiya" to "சாப்பிட்டியா?",
        "sapadu" to "சாப்பாடு",
        "saapadu" to "சாப்பாடு",
        "enna" to "என்ன",
        "panra" to "பண்ற?",
        "panreenga" to "பண்றீங்க?",
        "panren" to "பண்றேன்",
        "pandren" to "பண்றேன்",
        "pannu" to "பண்ணு",
        "pannunga" to "பண்ணுங்க",
        "enga" to "எங்க",
        "inga" to "இங்க",
        "anga" to "அங்க",
        "romba" to "ரொம்ப",
        "nalla" to "நல்லா",
        "nallathu" to "நல்லது",
        "thanks" to "நன்றி",
        "nandri" to "நன்றி",
        "vaazhkai" to "வாழ்க்கை",
        "valkai" to "வாழ்க்கை",
        "thamizh" to "தமிழ்",
        "tamil" to "தமிழ்",
        "thalaiva" to "தலைவா",
        "machi" to "மச்சி",
        "machan" to "மச்சான்",
        "seri" to "சரி",
        "sari" to "சரி",
        "illa" to "இல்லை",
        "illai" to "இல்லை",
        "illaye" to "இல்லையே",
        "aama" to "ஆமாம்",
        "aamam" to "ஆமாம்",
        "anna" to "அண்ணா",
        "akka" to "அக்கா",
        "amma" to "அம்மா",
        "appa" to "அப்பா",
        "thambi" to "தம்பி",
        "thangachi" to "தங்கச்சி",
        "nanba" to "நண்பா",
        "nanban" to "நண்பன்",
        "polam" to "போகலாம்",
        "polaama" to "போகலாமா?",
        "pesalam" to "பேசலாம்",
        "pesu" to "பேசு",
        "pesunga" to "பேசுங்க",
        "va" to "வா",
        "vaanga" to "வாங்க",
        "po" to "போ",
        "ponga" to "போங்க",
        "varren" to "வர்றேன்",
        "varen" to "வர்றேன்",
        "seekiram" to "சீக்கிரம்",
        "iniku" to "இன்னைக்கு",
        "innaiku" to "இன்னைக்கு",
        "naalaiki" to "நாளைக்கு",
        "mudiyum" to "முடியும்",
        "mudiyathu" to "முடியாது",
        "mudiyadhu" to "முடியாது",
        "theriyum" to "தெரியும்",
        "theriyathu" to "தெரியாது",
        "theriyadhu" to "தெரியாது",
        "veetla" to "வீட்ல",
        "veedu" to "வீடு",
        "office" to "ஆபீஸ்",
        "kudunga" to "குடுங்க",
        "kudu" to "குடு",
        "paapom" to "பார்க்கலாம்",
        "paakalaam" to "பார்க்கலாம்",
        "paaru" to "பாரு",
        "super" to "சூப்பர்",
        "semma" to "செம்ம",
        "mass" to "மாஸ்",
        "kalakku" to "கலக்கு",
        "azhagu" to "அழகு",
        "kaadhal" to "காதல்",
        "kadhal" to "காதல்",
        "anbu" to "அன்பு",
        "kovam" to "கோபம்",
        "bayam" to "பயம்",
        "santhosham" to "சந்தோஷம்",
        "kashtam" to "கஷ்டம்",
        "solra" to "சொல்ற?",
        "solren" to "சொல்றேன்",
        "solunga" to "சொல்லுங்க",
        "sonnen" to "சொன்னேன்",
        "paarunga" to "பாருங்க",
        "keta" to "கேட்டா",
        "keten" to "கேட்டேன்",
        "kekala" to "கேக்கல",
        "terila" to "தெரியல",
        "puriyala" to "புரியல",
        "purithu" to "புரியுது",
        "puriyudhu" to "புரியுது"
    )

    // Independent Tamil Vowels (உயிர் எழுத்துக்கள்)
    private val VOWELS = mapOf(
        "a" to "அ",
        "aa" to "ஆ",
        "A" to "ஆ",
        "i" to "இ",
        "ee" to "ஈ",
        "ii" to "ஈ",
        "I" to "ஈ",
        "u" to "உ",
        "oo" to "ஊ",
        "uu" to "ஊ",
        "U" to "ஊ",
        "e" to "எ",
        "ae" to "ஏ",
        "E" to "ஏ",
        "ai" to "ஐ",
        "o" to "ஒ",
        "oa" to "ஓ",
        "O" to "ஓ",
        "au" to "ஔ",
        "ou" to "ஔ"
    )

    // Dependent Vowel Signs (உயிர்மெய் குறியீடுகள்)
    private val VOWEL_SIGNS = mapOf(
        "a" to "",          // inherent vowel
        "aa" to "\u0BBE",   // ா
        "A" to "\u0BBE",    // ா
        "i" to "\u0BBF",    // ி
        "ee" to "\u0BC0",   // ீ
        "ii" to "\u0BC0",   // ீ
        "I" to "\u0BC0",    // ீ
        "u" to "\u0BC1",    // ு
        "oo" to "\u0BC2",   // ூ
        "uu" to "\u0BC2",   // ூ
        "U" to "\u0BC2",    // ூ
        "e" to "\u0BC6",    // ெ
        "ae" to "\u0BC7",   // ே
        "E" to "\u0BC7",    // ே
        "ai" to "\u0BC8",   // ை
        "o" to "\u0BCA",    // ொ
        "oa" to "\u0BCB",   // ோ
        "O" to "\u0BCB",    // ோ
        "au" to "\u0BCC",   // ௌ
        "ou" to "\u0BCC"    // ௌ
    )

    // Pure Consonants base glyphs (without pulli)
    private val CONSONANTS = mapOf(
        "k" to "\u0B95",    // க
        "g" to "\u0B95",    // க
        "ng" to "\u0B99",   // ங
        "c" to "\u0B9A",    // ச
        "ch" to "\u0B9A",   // ச
        "s" to "\u0B9A",    // ச
        "j" to "\u0B9C",    // ஜ
        "gn" to "\u0B9E",   // ஞ
        "nj" to "\u0B9E",   // ஞ
        "t" to "\u0BA4",    // த (standard)
        "th" to "\u0BA4",   // த
        "d" to "\u0BA4",    // த
        "dh" to "\u0BA4",   // த
        "T" to "\u0B9F",    // ட (retroflex)
        "tt" to "\u0B9F",   // ட
        "dd" to "\u0B9F",   // ட
        "N" to "\u0BA3",    // ண
        "n" to "\u0BA8",    // ந
        "nh" to "\u0BA8",   // ந
        "nn" to "\u0BA9",   // ன
        "p" to "\u0BAA",    // ப
        "b" to "\u0BAA",    // ப
        "m" to "\u0BAE",    // ம
        "y" to "\u0BAF",    // ய
        "r" to "\u0BB0",    // ர
        "R" to "\u0BB1",    // ற
        "rh" to "\u0BB1",   // ற
        "l" to "\u0BB2",    // ல
        "L" to "\u0BB3",    // ள
        "lh" to "\u0BB3",   // ள
        "zh" to "\u0BB4",   // ழ (Unique Tamil sound)
        "z" to "\u0BB4",    // ழ
        "v" to "\u0BB5",    // வ
        "w" to "\u0BB5",    // வ
        "sh" to "\u0BB7",   // ஷ
        "S" to "\u0BB8",    // ஸ
        "h" to "\u0BB9"     // ஹ
    )

    private const val VIRAMA = "\u0BCD" // Pulli ்

    /**
     * Transliterates a single Tanglish word into Tamil.
     */
    fun transliterateWord(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return ""

        val lower = trimmed.lowercase(Locale.ROOT)
        // 1. Direct colloquial map check
        COLLOQUIAL_MAP[lower]?.let { return it }

        // 2. Rule-based phonetic transliteration
        val sb = StringBuilder()
        var i = 0
        val len = trimmed.length

        while (i < len) {
            // Check for 2-char consonant / vowel
            val twoChar = if (i + 1 < len) trimmed.substring(i, i + 2).lowercase(Locale.ROOT) else ""
            val oneChar = trimmed.substring(i, i + 1)
            val oneCharLower = oneChar.lowercase(Locale.ROOT)

            // Check if at start or previous character was a vowel
            val isConsonantMatch = if (twoChar.isNotEmpty() && CONSONANTS.containsKey(twoChar)) {
                twoChar
            } else if (CONSONANTS.containsKey(oneChar) || CONSONANTS.containsKey(oneCharLower)) {
                if (CONSONANTS.containsKey(oneChar)) oneChar else oneCharLower
            } else {
                null
            }

            if (isConsonantMatch != null) {
                val consonantGlyph = CONSONANTS[isConsonantMatch]!!
                val consLen = isConsonantMatch.length
                val nextPos = i + consLen

                // Check following vowel
                val nextTwo = if (nextPos + 1 < len) trimmed.substring(nextPos, nextPos + 2).lowercase(Locale.ROOT) else ""
                val nextOne = if (nextPos < len) trimmed.substring(nextPos, nextPos + 1) else ""
                val nextOneLower = nextOne.lowercase(Locale.ROOT)

                val vowelMatch = if (nextTwo.isNotEmpty() && VOWEL_SIGNS.containsKey(nextTwo)) {
                    nextTwo
                } else if (VOWEL_SIGNS.containsKey(nextOne) || VOWEL_SIGNS.containsKey(nextOneLower)) {
                    if (VOWEL_SIGNS.containsKey(nextOne)) nextOne else nextOneLower
                } else {
                    null
                }

                if (vowelMatch != null) {
                    // Combine consonant + vowel sign (உயிர்மெய்)
                    sb.append(consonantGlyph)
                    sb.append(VOWEL_SIGNS[vowelMatch] ?: "")
                    i = nextPos + vowelMatch.length
                } else {
                    // Pure consonant with pulli (மெய்)
                    sb.append(consonantGlyph)
                    sb.append(VIRAMA)
                    i = nextPos
                }
            } else {
                // Check independent vowel
                val vowelMatch = if (twoChar.isNotEmpty() && VOWELS.containsKey(twoChar)) {
                    twoChar
                } else if (VOWELS.containsKey(oneChar) || VOWELS.containsKey(oneCharLower)) {
                    if (VOWELS.containsKey(oneChar)) oneChar else oneCharLower
                } else {
                    null
                }

                if (vowelMatch != null) {
                    sb.append(VOWELS[vowelMatch] ?: oneChar)
                    i += vowelMatch.length
                } else {
                    // Non-Tamil characters (digits, punctuation, or spaces)
                    sb.append(oneChar)
                    i += 1
                }
            }
        }

        return sb.toString()
    }

    /**
     * Generates top 3 ranked transliteration suggestions for Tanglish typing.
     * Triple: (Primary Tamil, Secondary Candidate, Original Tanglish)
     */
    fun getTransliterationCandidates(input: String): Triple<String, String, String> {
        val clean = input.trim()
        if (clean.isEmpty()) return Triple("", "", "")

        val lower = clean.lowercase(Locale.ROOT)
        val colloquial = COLLOQUIAL_MAP[lower]

        val primary = colloquial ?: transliterateWord(clean)
        val secondary = if (colloquial != null) {
            transliterateWord(clean)
        } else {
            // If primary has a question mark or variation
            if (primary.endsWith("?")) primary.removeSuffix("?") else "$primary 🙏"
        }

        return Triple(primary, secondary, clean)
    }

    /**
     * Full sentence transliteration helper.
     */
    fun transliterateSentence(sentence: String): String {
        val words = sentence.split(Regex("(?<=\\s)|(?=\\s)"))
        val sb = StringBuilder()
        for (w in words) {
            if (w.isBlank()) {
                sb.append(w)
            } else {
                sb.append(transliterateWord(w))
            }
        }
        return sb.toString()
    }
}
