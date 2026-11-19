package com.cleverkeyboard.app.intelligence

import org.junit.Assert.*
import org.junit.Test

class GrammarCorrectionEngineTest {

    @Test
    fun testSubjectVerbAgreement() {
        val fix1 = GrammarCorrectionEngine.findGrammarIssue("he go to office")
        assertNotNull(fix1)
        assertEquals("he goes", fix1?.correctedText)

        val fix2 = GrammarCorrectionEngine.findGrammarIssue("they is happy")
        assertNotNull(fix2)
        assertEquals("they are", fix2?.correctedText)

        val fix3 = GrammarCorrectionEngine.findGrammarIssue("I has a meeting tomorrow.")
        assertNotNull(fix3)
        assertEquals("I have", fix3?.correctedText)
        assertEquals("I have a meeting tomorrow.", fix3?.fullCorrectedSentence)

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
    fun testTenseErrors() {
        val fix1 = GrammarCorrectionEngine.findGrammarIssue("she did went there")
        assertNotNull(fix1)
        assertEquals("did go", fix1?.correctedText)

        val fix2 = GrammarCorrectionEngine.findGrammarIssue("they have saw it")
        assertNotNull(fix2)
        assertEquals("have seen", fix2?.correctedText)

        val fix3 = GrammarCorrectionEngine.findGrammarIssue("Yesterday I go to college.")
        assertNotNull(fix3)
        assertEquals("Yesterday I went", fix3?.correctedText)
        assertEquals("Yesterday I went to college.", fix3?.fullCorrectedSentence)
    }

    @Test
    fun testPunctuationAndGreeting() {
        val fix = GrammarCorrectionEngine.findGrammarIssue("hello how are you")
        assertNotNull(fix)
        assertEquals("hello, how are you?", fix?.correctedText)
    }

    @Test
    fun testArticleCorrections() {
        val fix1 = GrammarCorrectionEngine.findGrammarIssue("I bought an car")
        assertNotNull(fix1)
        assertEquals("a car", fix1?.correctedText)

        val fix2 = GrammarCorrectionEngine.findGrammarIssue("eating a apple")
        assertNotNull(fix2)
        assertEquals("an apple", fix2?.correctedText)
    }

    @Test
    fun testMissingPrepositions() {
        val fix = GrammarCorrectionEngine.findGrammarIssue("listen music now")
        assertNotNull(fix)
        assertEquals("listen to music", fix?.correctedText)
    }

    @Test
    fun testPunctuationSpacing() {
        val fix = GrammarCorrectionEngine.findGrammarIssue("hello , world")
        assertNotNull(fix)
        assertEquals(",", fix?.correctedText)
    }

    @Test
    fun testValidSentenceHasNoErrors() {
        val fix = GrammarCorrectionEngine.findGrammarIssue("He goes to school every day.")
        assertNull(fix)
    }
}
