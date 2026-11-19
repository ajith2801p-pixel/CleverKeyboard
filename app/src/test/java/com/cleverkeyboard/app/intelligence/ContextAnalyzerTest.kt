package com.cleverkeyboard.app.intelligence

import com.cleverkeyboard.app.autocorrect.AutoCorrectManager
import org.junit.Assert.*
import org.junit.Test

class ContextAnalyzerTest {

    @Test
    fun testEmptyContext() {
        val context = ContextAnalyzer.analyze("")
        assertEquals("", context.fullTextBeforeCursor)
        assertEquals("", context.currentWord)
        assertEquals("", context.previousWord)
        assertTrue(context.isSentenceStart)
        assertFalse(context.isIncompleteSentence)
    }

    @Test
    fun testSentenceWithActiveWord() {
        val context = ContextAnalyzer.analyze("Hello, how are yo")
        assertEquals("yo", context.currentWord)
        assertEquals("are", context.previousWord)
        assertEquals("how", context.twoWordsBack)
        assertFalse(context.isSentenceStart)
    }

    @Test
    fun testSentenceWithCompletedWordAndSpace() {
        val context = ContextAnalyzer.analyze("Thank you for ")
        assertEquals("", context.currentWord)
        assertEquals("for", context.previousWord)
        assertEquals("you", context.twoWordsBack)
        assertTrue(context.isIncompleteSentence)
    }

    @Test
    fun testTanglishLanguageDetection() {
        val context = ContextAnalyzer.analyze("machan eppo varuva")
        assertEquals(AutoCorrectManager.LANG_TANG, context.detectedLanguage)
    }

    @Test
    fun testTamilScriptDetection() {
        val context = ContextAnalyzer.analyze("வணக்கம் நண்பா")
        assertEquals(AutoCorrectManager.LANG_TA, context.detectedLanguage)
    }
}
