package com.cleverkeyboard.app

import com.cleverkeyboard.app.language.TamilTransliterationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TamilTransliterationTest {

    @Test
    fun testColloquialTransliterations() {
        assertEquals("வணக்கம்", TamilTransliterationEngine.transliterateWord("vanakkam"))
        assertEquals("எப்படி", TamilTransliterationEngine.transliterateWord("epdi"))
        assertEquals("நான்", TamilTransliterationEngine.transliterateWord("naan"))
        assertEquals("சாப்பிட்டேன்", TamilTransliterationEngine.transliterateWord("sapten"))
        assertEquals("என்ன", TamilTransliterationEngine.transliterateWord("enna"))
        assertEquals("பண்ற?", TamilTransliterationEngine.transliterateWord("panra"))
        assertEquals("தமிழ்", TamilTransliterationEngine.transliterateWord("thamizh"))
        assertEquals("வாழ்க்கை", TamilTransliterationEngine.transliterateWord("vaazhkai"))
    }

    @Test
    fun testCandidateRanking() {
        val (primary, secondary, original) = TamilTransliterationEngine.getTransliterationCandidates("vanakkam")
        assertEquals("வணக்கம்", primary)
        assertEquals("vanakkam", original)
    }

    @Test
    fun testSentenceTransliteration() {
        val result = TamilTransliterationEngine.transliterateSentence("vanakkam nanba epdi iruka")
        assertTrue(result.contains("வணக்கம்"))
        assertTrue(result.contains("நண்பா"))
        assertTrue(result.contains("எப்படி"))
    }
}
