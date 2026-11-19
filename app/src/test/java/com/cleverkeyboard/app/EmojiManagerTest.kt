package com.cleverkeyboard.app

import com.cleverkeyboard.app.emoji.EmojiManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EmojiManagerTest {

    @Test
    fun testCategoriesExist() {
        assertTrue(EmojiManager.CATEGORIES.size >= 10)
        assertTrue(EmojiManager.CATEGORIES.any { it.contains("Smileys") })
        assertTrue(EmojiManager.CATEGORIES.any { it.contains("People") })
        assertTrue(EmojiManager.CATEGORIES.any { it.contains("Animals") })
        assertTrue(EmojiManager.CATEGORIES.any { it.contains("Food") })
    }

    @Test
    fun testKeywordSearch() {
        val happyResults = EmojiManager.searchEmojis("happy")
        assertTrue(happyResults.isNotEmpty())
        assertTrue(happyResults.contains("😀") || happyResults.contains("😊"))

        val loveResults = EmojiManager.searchEmojis("love")
        assertTrue(loveResults.isNotEmpty())
        assertTrue(loveResults.contains("❤️"))

        val fireResults = EmojiManager.searchEmojis("fire")
        assertTrue(fireResults.isNotEmpty())
        assertTrue(fireResults.contains("🔥"))

        val dogResults = EmojiManager.searchEmojis("dog")
        assertTrue(dogResults.isNotEmpty())
        assertTrue(dogResults.contains("🐶"))

        val foodResults = EmojiManager.searchEmojis("pizza")
        assertTrue(foodResults.isNotEmpty())
        assertTrue(foodResults.contains("🍕"))
    }

    @Test
    fun testEmptySearchReturnsSmileys() {
        val results = EmojiManager.searchEmojis("")
        assertTrue(results.isNotEmpty())
        assertTrue(results.contains("😀"))
    }

    @Test
    fun testSkinToneModifiers() {
        assertEquals(6, EmojiManager.SKIN_TONES.size)

        val defaultHand = "👍"
        val lightHand = EmojiManager.applySkinTone(defaultHand, 1)
        assertTrue(lightHand.contains("\uD83C\uDFFB"))

        val darkHand = EmojiManager.applySkinTone(defaultHand, 5)
        assertTrue(darkHand.contains("\uD83C\uDFFF"))

        // Applying default skin tone (0) returns clean emoji
        val resetHand = EmojiManager.applySkinTone(lightHand, 0)
        assertEquals(defaultHand, resetHand)
    }
}
