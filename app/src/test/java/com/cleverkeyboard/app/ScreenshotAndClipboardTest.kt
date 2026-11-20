package com.cleverkeyboard.app

import android.net.Uri
import com.cleverkeyboard.app.clipboard.ClipboardItem
import com.cleverkeyboard.app.clipboard.ScreenshotItem
import com.cleverkeyboard.app.clipboard.ScreenshotManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ScreenshotAndClipboardTest {

    @Test
    fun testScreenshotRecencyThreshold() {
        val testUri: Uri? = null

        // Screenshot captured 0s ago (just taken)
        val shot0 = ScreenshotItem(testUri, "Screenshot_0.png", System.currentTimeMillis(), 0)
        assertTrue("0s old screenshot must be very recent (<=10s)", ScreenshotManager.isScreenshotVeryRecent(shot0, maxSeconds = 10))

        // Screenshot captured 5s ago
        val shot5 = ScreenshotItem(testUri, "Screenshot_5.png", System.currentTimeMillis() - 5000, 5)
        assertTrue("5s old screenshot must be very recent (<=10s)", ScreenshotManager.isScreenshotVeryRecent(shot5, maxSeconds = 10))

        // Screenshot captured 10s ago (threshold boundary)
        val shot10 = ScreenshotItem(testUri, "Screenshot_10.png", System.currentTimeMillis() - 10000, 10)
        assertTrue("10s old screenshot must be very recent (<=10s)", ScreenshotManager.isScreenshotVeryRecent(shot10, maxSeconds = 10))

        // Screenshot captured 11s ago (> 10s)
        val shot11 = ScreenshotItem(testUri, "Screenshot_11.png", System.currentTimeMillis() - 11000, 11)
        assertFalse("11s old screenshot must not be considered recent", ScreenshotManager.isScreenshotVeryRecent(shot11, maxSeconds = 10))

        // Screenshot captured 30s ago (> 10s)
        val shot30 = ScreenshotItem(testUri, "Screenshot_30.png", System.currentTimeMillis() - 30000, 30)
        assertFalse("30s old screenshot must not be considered recent", ScreenshotManager.isScreenshotVeryRecent(shot30, maxSeconds = 10))
    }

    @Test
    fun testClipboardItemImageProperties() {
        // Standard text clipboard item
        val textItem = ClipboardItem(
            text = "Hello world"
        )
        assertEquals("Hello world", textItem.text)
        assertFalse(textItem.isImage)
        assertNull(textItem.uriString)

        // Screenshot image clipboard item
        val imageItem = ClipboardItem(
            text = "Screenshot_2026.png",
            uriString = "content://media/external/images/media/42",
            isImage = true
        )
        assertEquals("Screenshot_2026.png", imageItem.text)
        assertTrue(imageItem.isImage)
        assertEquals("content://media/external/images/media/42", imageItem.uriString)
    }
}
