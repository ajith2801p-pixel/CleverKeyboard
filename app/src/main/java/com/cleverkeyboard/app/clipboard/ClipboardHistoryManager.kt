package com.cleverkeyboard.app.clipboard

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class ClipboardItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    var isPinned: Boolean = false,
    var isSensitive: Boolean = false,
    val uriString: String? = null,
    val isImage: Boolean = false
)

object ClipboardHistoryManager {
    private const val PREFS_NAME = "clever_clipboard_prefs"
    private const val KEY_ITEMS = "clipboard_items_json"
    private const val MAX_TOTAL_ITEMS = 50
    private const val SENSITIVE_EXPIRY_MS = 3600_000L // 1 Hour

    private var cachedItems: MutableList<ClipboardItem>? = null

    private fun getPrefs(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Detect if text is sensitive: OTP (4-8 digits) or contains sensitive keywords with numbers
     */
    fun isSensitiveText(text: String): Boolean {
        val trimmed = text.trim()
        if (trimmed.length in 4..8 && trimmed.all { it.isDigit() }) {
            return true
        }
        val lower = trimmed.lowercase()
        val hasSensitiveKeyword = lower.contains("otp") ||
                lower.contains("passcode") ||
                lower.contains("password") ||
                lower.contains("verification code") ||
                lower.contains("secret code") ||
                lower.contains("pin code") ||
                (lower.contains("code") && trimmed.any { it.isDigit() })
        return hasSensitiveKeyword && trimmed.any { it.isDigit() }
    }

    @Synchronized
    fun getItems(context: Context): List<ClipboardItem> {
        if (cachedItems == null) {
            loadItems(context)
        }
        val items = cachedItems ?: mutableListOf()

        // Auto-delete sensitive items older than 1 hour (unless pinned)
        val now = System.currentTimeMillis()
        val expired = items.filter { it.isSensitive && !it.isPinned && (now - it.timestamp > SENSITIVE_EXPIRY_MS) }
        if (expired.isNotEmpty()) {
            items.removeAll(expired)
            saveItems(context)
        }

        // Sort: pinned first, then by timestamp descending (newest first)
        return items.sortedWith(
            compareByDescending<ClipboardItem> { it.isPinned }
                .thenByDescending { it.timestamp }
        )
    }

    @Synchronized
    private fun loadItems(context: Context) {
        val list = mutableListOf<ClipboardItem>()
        val raw = getPrefs(context).getString(KEY_ITEMS, null)
        if (!raw.isNullOrBlank()) {
            try {
                val array = JSONArray(raw)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        ClipboardItem(
                            id = obj.optString("id", UUID.randomUUID().toString()),
                            text = obj.optString("text", ""),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                            isPinned = obj.optBoolean("isPinned", false),
                            isSensitive = obj.optBoolean("isSensitive", false),
                            uriString = if (obj.has("uriString") && !obj.isNull("uriString")) obj.getString("uriString") else null,
                            isImage = obj.optBoolean("isImage", false)
                        )
                    )
                }
            } catch (_: Exception) {}
        }
        cachedItems = list
    }

    @Synchronized
    private fun saveItems(context: Context) {
        val list = cachedItems ?: return
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("text", item.text)
                put("timestamp", item.timestamp)
                put("isPinned", item.isPinned)
                put("isSensitive", item.isSensitive)
                if (item.uriString != null) {
                    put("uriString", item.uriString)
                }
                put("isImage", item.isImage)
            }
            array.put(obj)
        }
        getPrefs(context).edit().putString(KEY_ITEMS, array.toString()).apply()
    }

    /**
     * Stores an image or recent screenshot into the clipboard history.
     */
    @Synchronized
    fun addImageItem(context: Context, uri: android.net.Uri, displayName: String = "Screenshot"): Boolean {
        val uriStr = uri.toString()
        if (cachedItems == null) {
            loadItems(context)
        }
        val list = cachedItems ?: mutableListOf()

        val existingIndex = list.indexOfFirst { it.uriString == uriStr }
        if (existingIndex != -1) {
            val existing = list.removeAt(existingIndex)
            list.add(0, existing.copy(timestamp = System.currentTimeMillis()))
        } else {
            list.add(0, ClipboardItem(
                text = displayName,
                timestamp = System.currentTimeMillis(),
                uriString = uriStr,
                isImage = true
            ))
        }

        if (list.size > MAX_TOTAL_ITEMS) {
            val nonPinned = list.filter { !it.isPinned }.sortedBy { it.timestamp }
            var excess = list.size - MAX_TOTAL_ITEMS
            for (item in nonPinned) {
                if (excess <= 0) break
                list.remove(item)
                excess--
            }
        }

        saveItems(context)
        return true
    }

    @Synchronized
    fun addItem(context: Context, text: String): Boolean {
        val clean = text.trim()
        if (clean.isBlank()) return false

        if (cachedItems == null) {
            loadItems(context)
        }
        val list = cachedItems ?: mutableListOf()
        val sensitive = isSensitiveText(clean)

        // If identical text exists, update timestamp and move to front
        val existingIndex = list.indexOfFirst { it.text == clean }
        if (existingIndex != -1) {
            val existing = list.removeAt(existingIndex)
            list.add(0, existing.copy(timestamp = System.currentTimeMillis(), isSensitive = existing.isSensitive || sensitive))
        } else {
            list.add(0, ClipboardItem(
                text = clean,
                timestamp = System.currentTimeMillis(),
                isSensitive = sensitive
            ))
        }

        // Limit total history to 50 items. Auto-delete oldest non-pinned items when exceeded
        if (list.size > MAX_TOTAL_ITEMS) {
            val nonPinned = list.filter { !it.isPinned }.sortedBy { it.timestamp }
            var excess = list.size - MAX_TOTAL_ITEMS
            for (item in nonPinned) {
                if (excess <= 0) break
                list.remove(item)
                excess--
            }
        }

        saveItems(context)
        return true
    }

    @Synchronized
    fun togglePin(context: Context, id: String): Boolean {
        if (cachedItems == null) loadItems(context)
        val item = cachedItems?.find { it.id == id } ?: return false
        item.isPinned = !item.isPinned
        saveItems(context)
        return true
    }

    @Synchronized
    fun deleteItem(context: Context, id: String): Boolean {
        if (cachedItems == null) loadItems(context)
        val removed = cachedItems?.removeAll { it.id == id } ?: false
        if (removed) saveItems(context)
        return removed
    }

    @Synchronized
    fun clearAll(context: Context) {
        if (cachedItems == null) loadItems(context)
        cachedItems?.removeAll { !it.isPinned }
        saveItems(context)
    }

    fun formatRelativeTime(timestamp: Long): String {
        val diffMs = System.currentTimeMillis() - timestamp
        val diffSec = diffMs / 1000
        val diffMin = diffSec / 60
        val diffHr = diffMin / 60
        val diffDays = diffHr / 24

        return when {
            diffSec < 60 -> "Just now"
            diffMin < 60 -> "${diffMin}m ago"
            diffHr < 24 -> "${diffHr}h ago"
            diffDays == 1L -> "Yesterday"
            else -> "${diffDays}d ago"
        }
    }

    fun formatSensitiveBadge(timestamp: Long): String {
        val remainingMs = SENSITIVE_EXPIRY_MS - (System.currentTimeMillis() - timestamp)
        val remainingMin = (remainingMs / 60000L).coerceAtLeast(1)
        return if (remainingMin >= 55) {
            "⏱ Auto-delete in 1h"
        } else {
            "⏱ Auto-delete in ${remainingMin}m"
        }
    }
}
