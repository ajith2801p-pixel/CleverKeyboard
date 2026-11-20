package com.cleverkeyboard.app.clipboard

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log

data class ScreenshotItem(
    val uri: Uri?,
    val displayName: String,
    val dateAddedMs: Long,
    val ageSeconds: Long
)

object ScreenshotManager {
    private const val TAG = "ScreenshotManager"

    fun getLatestScreenshot(context: Context): ScreenshotItem? {
        // 1. Primary path: query MediaStore for latest screenshot
        try {
            val projection = mutableListOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Images.Media.RELATIVE_PATH else MediaStore.Images.Media.DATA
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    add(MediaStore.Images.Media.DATE_TAKEN)
                }
            }.toTypedArray()

            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )

            cursor?.use { c ->
                val idCol = c.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val dateTakenCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    c.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)
                } else -1
                val pathCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    c.getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH)
                } else {
                    c.getColumnIndex(MediaStore.Images.Media.DATA)
                }

                var count = 0
                while (c.moveToNext() && count < 25) {
                    count++
                    val id = c.getLong(idCol)
                    val name = c.getString(nameCol) ?: ""
                    val dateAddedSec = c.getLong(dateCol)
                    val dateTakenMs = if (dateTakenCol != -1) c.getLong(dateTakenCol) else 0L
                    val path = if (pathCol != -1) c.getString(pathCol) ?: "" else ""

                    val isScreenshot = name.contains("screenshot", ignoreCase = true) ||
                            path.contains("screenshot", ignoreCase = true) ||
                            name.startsWith("Screenshot", ignoreCase = true) ||
                            path.contains("screen_shot", ignoreCase = true)

                    if (isScreenshot) {
                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        val timestampMs = when {
                            dateTakenMs > 0L -> dateTakenMs
                            dateAddedSec > 1_000_000_000_000L -> dateAddedSec
                            dateAddedSec > 0L -> dateAddedSec * 1000L
                            else -> System.currentTimeMillis()
                        }
                        val nowMs = System.currentTimeMillis()
                        val ageSec = Math.max(0L, (nowMs - timestampMs) / 1000L)
                        return ScreenshotItem(
                            uri = contentUri,
                            displayName = name,
                            dateAddedMs = timestampMs,
                            ageSeconds = ageSec
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Unable to query MediaStore screenshot: ${e.message}")
        }

        // 2. Fallback: check system ClipboardManager primaryClip for recent image
        try {
            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
            val clip = cm?.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val item = clip.getItemAt(0)
                val uri = item?.uri
                if (uri != null) {
                    val desc = clip.description
                    val hasImage = (0 until desc.mimeTypeCount).any { i ->
                        desc.getMimeType(i).startsWith("image/", ignoreCase = true)
                    }
                    if (hasImage) {
                        val label = desc.label?.toString() ?: "Recent screenshot"
                        return ScreenshotItem(
                            uri = uri,
                            displayName = label,
                            dateAddedMs = System.currentTimeMillis(),
                            ageSeconds = 0L
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Unable to query clipboard image: ${e.message}")
        }

        return null
    }

    /**
     * Checks if screenshot was captured within the recent threshold (default: strictly 10 seconds).
     */
    fun isScreenshotVeryRecent(item: ScreenshotItem, maxSeconds: Long = 10): Boolean {
        return item.ageSeconds in 0..maxSeconds
    }
}
