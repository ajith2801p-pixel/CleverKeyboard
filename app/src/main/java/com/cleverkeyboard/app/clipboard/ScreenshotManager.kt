package com.cleverkeyboard.app.clipboard

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log

data class ScreenshotItem(
    val uri: Uri,
    val displayName: String,
    val dateAddedMs: Long,
    val ageSeconds: Long
)

object ScreenshotManager {
    private const val TAG = "ScreenshotManager"

    fun getLatestScreenshot(context: Context): ScreenshotItem? {
        return try {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Images.Media.RELATIVE_PATH else MediaStore.Images.Media.DATA
            )
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            ) ?: return null

            cursor.use { c ->
                val idCol = c.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val pathCol = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    c.getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH)
                } else {
                    c.getColumnIndex(MediaStore.Images.Media.DATA)
                }

                var count = 0
                while (c.moveToNext() && count < 20) {
                    count++
                    val id = c.getLong(idCol)
                    val name = c.getString(nameCol) ?: ""
                    val dateAddedSec = c.getLong(dateCol)
                    val path = if (pathCol != -1) c.getString(pathCol) ?: "" else ""

                    val isScreenshot = name.contains("screenshot", ignoreCase = true) ||
                            path.contains("screenshot", ignoreCase = true) ||
                            name.startsWith("Screenshot", ignoreCase = true)

                    if (isScreenshot) {
                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        val nowSec = System.currentTimeMillis() / 1000
                        val ageSec = Math.max(0, nowSec - dateAddedSec)
                        return ScreenshotItem(
                            uri = contentUri,
                            displayName = name,
                            dateAddedMs = dateAddedSec * 1000,
                            ageSeconds = ageSec
                        )
                    }
                }
            }
            null
        } catch (e: Exception) {
            Log.d(TAG, "Unable to query screenshot: ${e.message}")
            null
        }
    }

    fun isScreenshotVeryRecent(item: ScreenshotItem, maxSeconds: Long = 45): Boolean {
        return item.ageSeconds in 0..maxSeconds
    }
}
