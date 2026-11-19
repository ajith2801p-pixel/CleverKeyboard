package com.cleverkeyboard.app

import android.view.inputmethod.EditorInfo
import com.cleverkeyboard.app.security.SensitiveFieldGuard
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SensitiveFieldGuardTest {

    @Test
    fun testPasswordFieldsAreSensitive() {
        val info = EditorInfo().apply {
            inputType = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
        }
        assertTrue(SensitiveFieldGuard.isSensitiveField(info))
        assertFalse(SensitiveFieldGuard.isAiAllowed(info))
        assertFalse(SensitiveFieldGuard.isLearningAllowed(info))
        assertFalse(SensitiveFieldGuard.isSuggestionsAllowed(info))
    }

    @Test
    fun testVisiblePasswordFieldsAreSensitive() {
        val info = EditorInfo().apply {
            inputType = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
        assertTrue(SensitiveFieldGuard.isSensitiveField(info))
        assertFalse(SensitiveFieldGuard.isAiAllowed(info))
    }

    @Test
    fun testNumericPinFieldsAreSensitive() {
        val info = EditorInfo().apply {
            inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD
        }
        assertTrue(SensitiveFieldGuard.isSensitiveField(info))
        assertFalse(SensitiveFieldGuard.isAiAllowed(info))
    }

    @Test
    fun testNormalTextFieldsAreAllowed() {
        val info = EditorInfo().apply {
            inputType = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
        }
        assertFalse(SensitiveFieldGuard.isSensitiveField(info))
        assertTrue(SensitiveFieldGuard.isAiAllowed(info))
        assertTrue(SensitiveFieldGuard.isLearningAllowed(info))
        assertTrue(SensitiveFieldGuard.isSuggestionsAllowed(info))
    }
}
