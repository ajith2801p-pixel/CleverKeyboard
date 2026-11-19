package com.cleverkeyboard.app.intelligence

import org.junit.Assert.*
import org.junit.Test

class TanglishLanguageEngineTest {

    @Test
    fun testCommonTanglishPhrases() {
        val conv1 = TanglishLanguageEngine.matchLocalTanglish("naan sapten")
        assertNotNull(conv1)
        assertEquals("I have eaten", conv1?.englishTranslation)
        assertEquals("நான் சாப்பிட்டேன்", conv1?.tamilScript)

        val conv2 = TanglishLanguageEngine.matchLocalTanglish("enna panra")
        assertNotNull(conv2)
        assertEquals("What are you doing?", conv2?.englishTranslation)
        assertEquals("என்ன பண்ற", conv2?.tamilScript)

        val conv3 = TanglishLanguageEngine.matchLocalTanglish("romba nandri")
        assertNotNull(conv3)
        assertEquals("Thank you very much", conv3?.englishTranslation)
        assertEquals("ரொம்ப நன்றி", conv3?.tamilScript)
    }

    @Test
    fun testSlangAndColloquialisms() {
        val conv1 = TanglishLanguageEngine.matchLocalTanglish("seri da")
        assertNotNull(conv1)
        assertEquals("Alright bro", conv1?.englishTranslation)

        val conv2 = TanglishLanguageEngine.matchLocalTanglish("vera level")
        assertNotNull(conv2)
        assertEquals("Next level / Amazing", conv2?.englishTranslation)
    }

    @Test
    fun testUnknownReturnsNullForLocal() {
        val conv = TanglishLanguageEngine.matchLocalTanglish("arbitrary phrase that does not exist")
        assertNull(conv)
    }
}
