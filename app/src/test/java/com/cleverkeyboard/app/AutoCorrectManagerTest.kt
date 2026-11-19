package com.cleverkeyboard.app

import com.cleverkeyboard.app.autocorrect.AutoCorrectManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AutoCorrectManagerTest {

    @Before
    fun setup() {
        AutoCorrectManager.setTestWordsList(
            listOf(
                "the", "receive", "because", "their", "hello", "weird",
                "separate", "definitely", "until", "truly", "tomorrow",
                "across", "believe", "surprise", "grammar", "world", "word",
                "clever", "keyboard", "crearemind", "project", "development",
                "technology", "good", "morning", "thank", "you"
            )
        )
    }

    @Test
    fun testCommonTypoCorrections() {
        assertEquals("the", AutoCorrectManager.getCorrection(null, "teh"))
        assertEquals("receive", AutoCorrectManager.getCorrection(null, "recieve"))
        assertEquals("because", AutoCorrectManager.getCorrection(null, "becuase"))
        assertEquals("their", AutoCorrectManager.getCorrection(null, "thier"))
        assertEquals("hello", AutoCorrectManager.getCorrection(null, "helllo"))
        assertEquals("hello", AutoCorrectManager.getCorrection(null, "helo"))
        assertEquals("weird", AutoCorrectManager.getCorrection(null, "wierd"))
        assertEquals("separate", AutoCorrectManager.getCorrection(null, "seperate"))
        assertEquals("definitely", AutoCorrectManager.getCorrection(null, "definately"))
        assertEquals("until", AutoCorrectManager.getCorrection(null, "untill"))
        assertEquals("truly", AutoCorrectManager.getCorrection(null, "truely"))
        assertEquals("don't", AutoCorrectManager.getCorrection(null, "dont"))
        assertEquals("can't", AutoCorrectManager.getCorrection(null, "cant"))
        assertEquals("won't", AutoCorrectManager.getCorrection(null, "wont"))
        assertEquals("I'm", AutoCorrectManager.getCorrection(null, "im"))
        assertEquals("you're", AutoCorrectManager.getCorrection(null, "youre"))
        assertEquals("good morning", AutoCorrectManager.getCorrection(null, "goodmorning"))
        assertEquals("thank you", AutoCorrectManager.getCorrection(null, "thankyou"))
    }

    @Test
    fun testCasePreservation() {
        assertEquals("The", AutoCorrectManager.getCorrection(null, "Teh"))
        assertEquals("THE", AutoCorrectManager.getCorrection(null, "TEH"))
        assertEquals("Receive", AutoCorrectManager.getCorrection(null, "Recieve"))
        assertEquals("RECEIVE", AutoCorrectManager.getCorrection(null, "RECIEVE"))
    }

    @Test
    fun testPersonalDictionaryProtection() {
        // Seed words should be recognized as personal words and NEVER autocorrected
        assertTrue(AutoCorrectManager.isPersonalWord("cleverkeyboard"))
        assertTrue(AutoCorrectManager.isPersonalWord("crearemind"))
        assertTrue(AutoCorrectManager.isPersonalWord("development"))
        assertTrue(AutoCorrectManager.isPersonalWord("project"))
        assertTrue(AutoCorrectManager.isPersonalWord("technology"))

        assertNull(AutoCorrectManager.getCorrection(null, "cleverkeyboard"))
        assertNull(AutoCorrectManager.getCorrection(null, "CleverKeyboard"))
        assertNull(AutoCorrectManager.getCorrection(null, "crearemind"))
        assertNull(AutoCorrectManager.getCorrection(null, "Crearemind"))
    }

    @Test
    fun testNextWordDynamicSuggestionsReturnsExactlyFiveItems() {
        val suggestions = AutoCorrectManager.getDynamicSuggestions(null, "how ")
        assertEquals(5, suggestions.size)

        // Center item (index 2) must be the primary prediction
        assertTrue(suggestions[2].isPrimary)
        assertFalse(suggestions[0].isPrimary)
        assertFalse(suggestions[1].isPrimary)
        assertFalse(suggestions[3].isPrimary)
        assertFalse(suggestions[4].isPrimary)

        // No duplicates
        val distinct = suggestions.map { it.text.lowercase() }.distinct()
        assertEquals(5, distinct.size)
    }

    @Test
    fun testTypoDynamicSuggestionsCentersBestCorrection() {
        val suggestions = AutoCorrectManager.getDynamicSuggestions(null, "teh")
        assertEquals(5, suggestions.size)

        // Center item (index 2) must be primary and marked as correction
        assertEquals("the", suggestions[2].text.lowercase())
        assertTrue(suggestions[2].isPrimary)
        assertTrue(suggestions[2].isCorrection)

        // Must have 5 distinct suggestions
        val distinct = suggestions.map { it.text.lowercase() }.distinct()
        assertEquals(5, distinct.size)
    }

    @Test
    fun testLevenshteinDistance() {
        assertEquals(0, AutoCorrectManager.levenshteinDistance("hello", "hello"))
        assertEquals(1, AutoCorrectManager.levenshteinDistance("helo", "hello"))
        assertEquals(1, AutoCorrectManager.levenshteinDistance("helllo", "hello"))
        assertEquals(1, AutoCorrectManager.levenshteinDistance("world", "wrld"))
    }
}
