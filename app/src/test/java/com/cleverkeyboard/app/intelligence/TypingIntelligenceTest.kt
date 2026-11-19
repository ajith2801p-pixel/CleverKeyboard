package com.cleverkeyboard.app.intelligence

import org.junit.Assert.*
import org.junit.Test

class TypingIntelligenceTest {

    @Test
    fun testContextAnalyzerSemanticKeywords() {
        val hungryContext = ContextAnalyzer.analyze("I am feeling very hungry. I need something to ")
        assertTrue(hungryContext.semanticKeywords.contains("HUNGRY"))
        assertEquals("", hungryContext.currentWord)
        assertEquals("to", hungryContext.previousWord)
        assertEquals("something", hungryContext.twoWordsBack)

        val examContext = ContextAnalyzer.analyze("I have an exam tomorrow. I need to ")
        assertTrue(examContext.semanticKeywords.contains("EXAM"))
        assertEquals("", examContext.currentWord)
        assertEquals("to", examContext.previousWord)

        val projectContext = ContextAnalyzer.analyze("I am currently working on a project using ")
        assertTrue(projectContext.semanticKeywords.contains("PROJECT"))
    }

    @Test
    fun testRealTimeGrammarCorrections() {
        // Example 1: Subject-verb agreement
        val fix1 = GrammarCorrectionEngine.findGrammarIssue("I has a meeting tomorrow.")
        assertNotNull(fix1)
        assertEquals("I have", fix1?.correctedText)
        assertEquals("I have a meeting tomorrow.", fix1?.fullCorrectedSentence)

        // Example 2: Incorrect tense
        val fix2 = GrammarCorrectionEngine.findGrammarIssue("Yesterday I go to college.")
        assertNotNull(fix2)
        assertEquals("Yesterday I went", fix2?.correctedText)
        assertEquals("Yesterday I went to college.", fix2?.fullCorrectedSentence)

        // Example 3: Punctuation and greeting
        val fix3 = GrammarCorrectionEngine.findGrammarIssue("hello how are you")
        assertNotNull(fix3)
        assertEquals("hello, how are you?", fix3?.correctedText)

        // Example 4: Common spoken grammatical errors
        val fix4 = GrammarCorrectionEngine.findGrammarIssue("She don't like coffee.")
        assertNotNull(fix4)
        assertEquals("She doesn't", fix4?.correctedText)
        assertEquals("She doesn't like coffee.", fix4?.fullCorrectedSentence)

        val fix5 = GrammarCorrectionEngine.findGrammarIssue("She don't know about this.")
        assertNotNull(fix5)
        assertEquals("She doesn't", fix5?.correctedText)
        assertEquals("She doesn't know about this.", fix5?.fullCorrectedSentence)
    }

    @Test
    fun testSentenceCompletionMultiOptions() {
        // Example 1: Casual conversation
        val completions1 = SentenceCompletionEngine.matchMultiCompletions("I will call you")
        assertEquals(3, completions1.size)
        assertTrue(completions1.contains("when I reach home."))
        assertTrue(completions1.contains("after my meeting."))
        assertTrue(completions1.contains("as soon as possible."))

        // Example 2: Professional email
        val completions2 = SentenceCompletionEngine.matchMultiCompletions("Dear Sir, I am writing this email to")
        assertEquals(3, completions2.size)
        assertTrue(completions2.contains("request permission to attend the event."))
        assertTrue(completions2.contains("inform you about my absence tomorrow."))
        assertTrue(completions2.contains("discuss the project progress."))

        // Example 3: Decision making
        val completions3 = SentenceCompletionEngine.matchMultiCompletions("I think we should")
        assertEquals(3, completions3.size)
        assertTrue(completions3.contains("go out this weekend."))
        assertTrue(completions3.contains("discuss this later."))
        assertTrue(completions3.contains("try something different."))

        // Example 4: Daily planning
        val completions4 = SentenceCompletionEngine.matchMultiCompletions("I need to prepare")
        assertEquals(3, completions4.size)
        assertTrue(completions4.contains("the presentation."))
        assertTrue(completions4.contains("for tomorrow's meeting."))
        assertTrue(completions4.contains("the project documents."))

        // Example 5: Software development / professional context
        val completions5 = SentenceCompletionEngine.matchMultiCompletions("I am currently working on a project using")
        assertEquals(3, completions5.size)
        assertTrue(completions5.contains("Kotlin and Jetpack Compose."))
        assertTrue(completions5.contains("Python and machine learning."))
        assertTrue(completions5.contains("AWS and cloud infrastructure."))
    }
}
