package com.cleverkeyboard.app.security

import android.view.inputmethod.EditorInfo

/**
 * Dedicated Security & Privacy Guard for Sensitive Input Fields.
 * Prevents AI processing, suggestions persistence, clipboard harvesting,
 * and dictionary learning during password, PIN, OTP, and confidential entry.
 */
object SensitiveFieldGuard {

    /**
     * Determines whether the given editor represents a sensitive field (Password, PIN, OTP).
     */
    fun isSensitiveField(info: EditorInfo?): Boolean {
        if (info == null) return false
        val inputType = info.inputType
        val classType = inputType and EditorInfo.TYPE_MASK_CLASS
        val variation = inputType and EditorInfo.TYPE_MASK_VARIATION

        // Password variations
        if (variation == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD ||
            variation == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
            variation == EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD
        ) {
            return true
        }

        // Numeric password or PIN
        if (classType == EditorInfo.TYPE_CLASS_NUMBER) {
            val numVariation = inputType and EditorInfo.TYPE_MASK_VARIATION
            if (numVariation == EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD) {
                return true
            }
        }

        // Explicit no-suggestions or private fields
        if ((inputType and EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS) != 0) {
            val fieldName = (info.fieldName ?: "").lowercase()
            val packageName = (info.packageName ?: "").lowercase()
            if (fieldName.contains("pass") || fieldName.contains("pin") || fieldName.contains("otp") ||
                fieldName.contains("cvv") || fieldName.contains("card") || fieldName.contains("secret") ||
                packageName.contains("banking") || packageName.contains("authenticator")
            ) {
                return true
            }
        }

        return false
    }

    /**
     * Checks if AI writing or grammar fixing is permitted on this field.
     * Strictly blocked on passwords, PINs, and banking credentials.
     */
    fun isAiAllowed(info: EditorInfo?): Boolean {
        return !isSensitiveField(info)
    }

    /**
     * Checks if words typed in this field can be recorded in personal dictionary.
     */
    fun isLearningAllowed(info: EditorInfo?): Boolean {
        if (isSensitiveField(info)) return false
        val inputType = info?.inputType ?: 0
        val variation = inputType and EditorInfo.TYPE_MASK_VARIATION
        if (variation == EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS ||
            variation == EditorInfo.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS ||
            variation == EditorInfo.TYPE_TEXT_VARIATION_URI
        ) {
            return false
        }
        return true
    }

    /**
     * Checks if predictive suggestions bar should be displayed.
     */
    fun isSuggestionsAllowed(info: EditorInfo?): Boolean {
        return !isSensitiveField(info)
    }
}
