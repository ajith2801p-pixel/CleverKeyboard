package com.cleverkeyboard.app.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object ApiKeyManager {
    private const val TAG = "ApiKeyManager"
    private const val SECURE_PREFS_NAME = "clever_keyboard_secure_prefs"
    private const val FALLBACK_PREFS_NAME = "clever_keyboard_fallback_prefs"
    private const val KEY_GEMINI_API_KEY = "gemini_api_key"

    private fun getPreferences(context: Context): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(context.applicationContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context.applicationContext,
                SECURE_PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "EncryptedSharedPreferences initialization failed, falling back to standard prefs", e)
            context.applicationContext.getSharedPreferences(FALLBACK_PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    fun saveApiKey(context: Context, apiKey: String) {
        val trimmed = apiKey.trim()
        getPreferences(context).edit().putString(KEY_GEMINI_API_KEY, trimmed).apply()
    }

    fun getApiKey(context: Context): String? {
        val key = getPreferences(context).getString(KEY_GEMINI_API_KEY, null)
        return if (key.isNullOrBlank()) null else key
    }

    fun hasApiKey(context: Context): Boolean {
        return !getApiKey(context).isNullOrBlank()
    }

    fun clearApiKey(context: Context) {
        getPreferences(context).edit().remove(KEY_GEMINI_API_KEY).apply()
    }
}

