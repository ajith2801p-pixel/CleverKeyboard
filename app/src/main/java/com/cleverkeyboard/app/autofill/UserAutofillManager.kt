package com.cleverkeyboard.app.autofill

import android.content.Context
import android.content.SharedPreferences
import android.view.inputmethod.EditorInfo

object UserAutofillManager {
    private const val PREFS_NAME = "clever_user_autofill_prefs"
    private const val KEY_USER_FIRST_NAME = "user_first_name"
    private const val KEY_USER_FULL_NAME = "user_full_name"
    private const val KEY_USER_PHONE_PRIMARY = "user_phone_primary"
    private const val KEY_USER_PHONE_SECONDARY = "user_phone_secondary"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isNameField(info: EditorInfo?): Boolean {
        if (info == null) return false
        val inputType = info.inputType
        val variation = inputType and EditorInfo.TYPE_MASK_VARIATION
        if (variation == EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME) return true

        val hint = (info.hintText ?: "").toString().lowercase()
        if (hint == "name" || hint.contains("first name") || hint.contains("full name") || hint.contains("enter name") || hint.contains("your name")) {
            return true
        }

        val fieldName = (info.fieldName ?: "").lowercase()
        if (fieldName == "name" || fieldName.endsWith("_name") || fieldName.contains("person_name") || fieldName.contains("firstname") || fieldName.contains("fullname")) {
            return true
        }

        val imeOptions = (info.privateImeOptions ?: "").lowercase()
        if (imeOptions.contains("person_name") || imeOptions.contains("user_name")) return true

        return false
    }

    fun isPhoneOrNumberField(info: EditorInfo?): Boolean {
        if (info == null) return false
        val inputType = info.inputType
        val inputClass = inputType and EditorInfo.TYPE_MASK_CLASS
        if (inputClass == EditorInfo.TYPE_CLASS_PHONE) return true

        val hint = (info.hintText ?: "").toString().lowercase()
        if (hint.contains("phone") || hint.contains("mobile") || hint.contains("contact number") || hint.contains("mobile number")) {
            return true
        }

        val fieldName = (info.fieldName ?: "").lowercase()
        if (fieldName.contains("phone") || fieldName.contains("mobile") || fieldName.endsWith("_tel")) return true

        val imeOptions = (info.privateImeOptions ?: "").lowercase()
        if (imeOptions.contains("phone") || imeOptions.contains("mobile")) return true

        return false
    }

    fun getUserNames(context: Context): List<String> {
        val prefs = getPrefs(context)
        val firstName = prefs.getString(KEY_USER_FIRST_NAME, "Ajith") ?: "Ajith"
        val fullName = prefs.getString(KEY_USER_FULL_NAME, "Ajith Kumar") ?: "Ajith Kumar"
        return listOf(firstName, fullName, "Ajith (Udanpirappu)").distinct()
    }

    fun getUserPhoneNumbers(context: Context): List<String> {
        val prefs = getPrefs(context)
        val primary = prefs.getString(KEY_USER_PHONE_PRIMARY, "+91 9876543210") ?: "+91 9876543210"
        val cleanPrimary = primary.replace(" ", "").replace("+91", "")
        return listOf(primary, cleanPrimary).distinct()
    }

    fun saveUserName(context: Context, firstName: String, fullName: String) {
        getPrefs(context).edit()
            .putString(KEY_USER_FIRST_NAME, firstName.trim())
            .putString(KEY_USER_FULL_NAME, fullName.trim())
            .apply()
    }

    fun saveUserPhone(context: Context, phone: String) {
        getPrefs(context).edit()
            .putString(KEY_USER_PHONE_PRIMARY, phone.trim())
            .apply()
    }
}
