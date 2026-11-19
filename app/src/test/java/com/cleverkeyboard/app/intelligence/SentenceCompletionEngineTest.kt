package com.cleverkeyboard.app.intelligence

import org.junit.Assert.*
import org.junit.Test

class SentenceCompletionEngineTest {

    @Test
    fun testMultiSentenceCompletions() {
        val callCompletions = SentenceCompletionEngine.matchMultiCompletions("I will call you")
        assertTrue(callCompletions.isNotEmpty())
        assertTrue(callCompletions.contains("when I reach home."))

        val emailCompletions = SentenceCompletionEngine.matchMultiCompletions("Dear Sir, I am writing this email to")
        assertTrue(emailCompletions.isNotEmpty())
        assertTrue(emailCompletions.contains("request permission to attend the event."))

        val opinionCompletions = SentenceCompletionEngine.matchMultiCompletions("I think we should")
        assertTrue(opinionCompletions.isNotEmpty())
        assertTrue(opinionCompletions.contains("go out this weekend."))

        val prepareCompletions = SentenceCompletionEngine.matchMultiCompletions("I need to prepare")
        assertTrue(prepareCompletions.isNotEmpty())
        assertTrue(prepareCompletions.contains("the presentation."))

        val techCompletions = SentenceCompletionEngine.matchMultiCompletions("I am currently working on a project using")
        assertTrue(techCompletions.isNotEmpty())
        assertTrue(techCompletions.contains("Kotlin and Jetpack Compose."))
    }

    @Test
    fun testSuffixPhraseCompletion() {
        val completion = SentenceCompletionEngine.matchLocalCompletion("Hey bro, could you please")
        assertNotNull(completion)
        assertEquals("let me know when you are available?", completion?.completionText)
    }

    @Test
    fun testTanglishSentenceCompletion() {
        val completion = SentenceCompletionEngine.matchLocalCompletion("naan ungalukku")
        assertNotNull(completion)
        assertEquals("udane anuparen", completion?.completionText)
    }

    @Test
    fun testUnknownSentenceReturnsNull() {
        val completion = SentenceCompletionEngine.matchLocalCompletion("The quick brown fox")
        assertNull(completion)
    }
}
