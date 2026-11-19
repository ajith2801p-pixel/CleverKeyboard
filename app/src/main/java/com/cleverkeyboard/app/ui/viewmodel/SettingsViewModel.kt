package com.cleverkeyboard.app.ui.viewmodel

import android.app.Application
import android.content.Context
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cleverkeyboard.app.autocorrect.AutoCorrectManager
import com.cleverkeyboard.app.data.ApiKeyManager
import com.cleverkeyboard.app.data.KeyboardTheme
import com.cleverkeyboard.app.data.ThemeManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ColorTarget {
    KEYBOARD_BG,
    KEY_BG,
    KEY_TEXT,
    ACCENT
}

data class SettingsUiState(
    val selectedTab: Int = 0, // 0 = General Settings, 1 = Themes
    val apiKey: String = "",
    val hasApiKey: Boolean = false,
    val apiKeySavedMessage: String? = null,
    val isAutoCorrectEnabled: Boolean = true,
    val isNumberRowEnabled: Boolean = true,
    val keyBordersMode: String = KeyboardTheme.BORDERS_AUTO,
    val isKeyBordersEnabled: Boolean = true,
    val isAutoCapitalizationEnabled: Boolean = true,
    val isAiToolbarLabelsEnabled: Boolean = true,
    val isAutoNumPadEnabled: Boolean = true,
    val isNextWordPredictionEnabled: Boolean = true,
    val isPersonalizedSuggestionsEnabled: Boolean = true,
    val isSoundEnabled: Boolean = false,
    val isVibrationEnabled: Boolean = true,
    val currentTheme: KeyboardTheme = KeyboardTheme.DARK_THEME,
    val selectedPresetId: String? = KeyboardTheme.DARK_THEME.id,
    val isKeyboardEnabled: Boolean = false,
    val isKeyboardSelected: Boolean = false,
    val activeColorTarget: ColorTarget? = null,
    val keyShape: String = KeyboardTheme.KEY_SHAPE_ROUNDED,
    val activeLanguage: String = "EN",
    val keyboardHeightPercent: Int = 100,
    val isSwipeTypingEnabled: Boolean = false,
    val isSmartPunctuationEnabled: Boolean = true
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadInitialState()
    }

    fun selectTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tabIndex)
    }

    fun loadInitialState() {
        val context = getApplication<Application>()
        val savedKey = ApiKeyManager.getApiKey(context) ?: ""
        val savedTheme = ThemeManager.getTheme(context)
        val autoCorrect = AutoCorrectManager.isAutoCorrectEnabled(context)
        val isSwipe = AutoCorrectManager.isSwipeTypingEnabled(context)
        val isSmartPunct = AutoCorrectManager.isSmartPunctuationEnabled(context)
        val enabled = checkKeyboardEnabled(context)
        val selected = checkKeyboardSelected(context)

        _uiState.value = _uiState.value.copy(
            apiKey = savedKey,
            hasApiKey = savedKey.isNotBlank(),
            isAutoCorrectEnabled = autoCorrect,
            isSwipeTypingEnabled = isSwipe,
            isSmartPunctuationEnabled = isSmartPunct,
            isNumberRowEnabled = savedTheme.showNumberRow,
            keyBordersMode = savedTheme.keyBordersMode,
            isKeyBordersEnabled = savedTheme.isBordersActive(),
            isAutoCapitalizationEnabled = savedTheme.autoCapitalization,
            isAiToolbarLabelsEnabled = savedTheme.showAiToolbarLabels,
            isAutoNumPadEnabled = savedTheme.autoNumPadForOtp,
            isNextWordPredictionEnabled = savedTheme.nextWordPrediction,
            isPersonalizedSuggestionsEnabled = savedTheme.personalizedSuggestions,
            isSoundEnabled = ThemeManager.isSoundEnabled(context),
            isVibrationEnabled = ThemeManager.isVibrationEnabled(context),
            currentTheme = savedTheme,
            selectedPresetId = savedTheme.id,
            isKeyboardEnabled = enabled,
            isKeyboardSelected = selected,
            keyShape = savedTheme.keyShape,
            activeLanguage = AutoCorrectManager.getActiveLanguage(context),
            keyboardHeightPercent = savedTheme.keyboardHeightPercent
        )
    }

    fun setKeyShape(shape: String) {
        val context = getApplication<Application>()
        val updated = _uiState.value.currentTheme.copy(keyShape = shape)
        ThemeManager.saveTheme(context, updated)
        _uiState.value = _uiState.value.copy(
            currentTheme = updated,
            keyShape = shape
        )
    }

    fun setActiveLanguage(lang: String) {
        val context = getApplication<Application>()
        AutoCorrectManager.setActiveLanguage(context, lang)
        _uiState.value = _uiState.value.copy(activeLanguage = lang)
    }

    fun setKeyboardHeight(percent: Int) {
        val context = getApplication<Application>()
        val updated = _uiState.value.currentTheme.copy(keyboardHeightPercent = percent)
        ThemeManager.saveTheme(context, updated)
        _uiState.value = _uiState.value.copy(
            currentTheme = updated,
            keyboardHeightPercent = percent
        )
    }

    fun toggleSwipeTyping(enabled: Boolean) {
        val context = getApplication<Application>()
        AutoCorrectManager.setSwipeTypingEnabled(context, enabled)
        _uiState.value = _uiState.value.copy(isSwipeTypingEnabled = enabled)
    }

    fun toggleSmartPunctuation(enabled: Boolean) {
        val context = getApplication<Application>()
        AutoCorrectManager.setSmartPunctuationEnabled(context, enabled)
        _uiState.value = _uiState.value.copy(isSmartPunctuationEnabled = enabled)
    }

    fun toggleAutoCorrect(enabled: Boolean) {
        val context = getApplication<Application>()
        AutoCorrectManager.setAutoCorrectEnabled(context, enabled)
        _uiState.value = _uiState.value.copy(isAutoCorrectEnabled = enabled)
    }

    fun toggleNumberRow(enabled: Boolean) {
        val context = getApplication<Application>()
        val updated = _uiState.value.currentTheme.copy(showNumberRow = enabled)
        ThemeManager.saveTheme(context, updated)
        _uiState.value = _uiState.value.copy(
            currentTheme = updated,
            isNumberRowEnabled = enabled
        )
    }

    fun setKeyBordersMode(mode: String) {
        val context = getApplication<Application>()
        val updated = _uiState.value.currentTheme.copy(keyBordersMode = mode)
        ThemeManager.saveTheme(context, updated)
        _uiState.value = _uiState.value.copy(
            currentTheme = updated,
            keyBordersMode = mode,
            isKeyBordersEnabled = updated.isBordersActive()
        )
    }

    fun toggleKeyBorders(enabled: Boolean) {
        setKeyBordersMode(if (enabled) KeyboardTheme.BORDERS_ON else KeyboardTheme.BORDERS_OFF)
    }

    fun toggleAutoCapitalization(enabled: Boolean) {
        val context = getApplication<Application>()
        val updated = _uiState.value.currentTheme.copy(autoCapitalization = enabled)
        ThemeManager.saveTheme(context, updated)
        _uiState.value = _uiState.value.copy(
            currentTheme = updated,
            isAutoCapitalizationEnabled = enabled
        )
    }

    fun toggleAiToolbarLabels(enabled: Boolean) {
        val context = getApplication<Application>()
        val updated = _uiState.value.currentTheme.copy(showAiToolbarLabels = enabled)
        ThemeManager.saveTheme(context, updated)
        _uiState.value = _uiState.value.copy(
            currentTheme = updated,
            isAiToolbarLabelsEnabled = enabled
        )
    }

    fun toggleAutoNumPad(enabled: Boolean) {
        val context = getApplication<Application>()
        val updated = _uiState.value.currentTheme.copy(autoNumPadForOtp = enabled)
        ThemeManager.saveTheme(context, updated)
        _uiState.value = _uiState.value.copy(
            currentTheme = updated,
            isAutoNumPadEnabled = enabled
        )
    }

    fun toggleNextWordPrediction(enabled: Boolean) {
        val context = getApplication<Application>()
        val updated = _uiState.value.currentTheme.copy(nextWordPrediction = enabled)
        ThemeManager.saveTheme(context, updated)
        AutoCorrectManager.setNextWordPredictionEnabled(context, enabled)
        _uiState.value = _uiState.value.copy(
            currentTheme = updated,
            isNextWordPredictionEnabled = enabled
        )
    }

    fun togglePersonalizedSuggestions(enabled: Boolean) {
        val context = getApplication<Application>()
        val updated = _uiState.value.currentTheme.copy(personalizedSuggestions = enabled)
        ThemeManager.saveTheme(context, updated)
        AutoCorrectManager.setPersonalizedSuggestionsEnabled(context, enabled)
        _uiState.value = _uiState.value.copy(
            currentTheme = updated,
            isPersonalizedSuggestionsEnabled = enabled
        )
    }

    fun setDismissButtonMode(mode: String) {
        val context = getApplication<Application>()
        val updated = _uiState.value.currentTheme.copy(dismissButtonMode = mode)
        ThemeManager.saveTheme(context, updated)
        _uiState.value = _uiState.value.copy(currentTheme = updated)
    }

    fun toggleSound(enabled: Boolean) {
        val context = getApplication<Application>()
        ThemeManager.setSoundEnabled(context, enabled)
        _uiState.value = _uiState.value.copy(isSoundEnabled = enabled)
    }

    fun toggleVibration(enabled: Boolean) {
        val context = getApplication<Application>()
        ThemeManager.setVibrationEnabled(context, enabled)
        _uiState.value = _uiState.value.copy(isVibrationEnabled = enabled)
    }

    fun onApiKeyChanged(newKey: String) {
        _uiState.value = _uiState.value.copy(
            apiKey = newKey,
            apiKeySavedMessage = null
        )
    }

    fun saveApiKey() {
        val context = getApplication<Application>()
        val key = _uiState.value.apiKey.trim()
        ApiKeyManager.saveApiKey(context, key)
        _uiState.value = _uiState.value.copy(
            hasApiKey = key.isNotBlank(),
            apiKeySavedMessage = if (key.isNotBlank()) "API Key securely saved!" else "API Key cleared."
        )
    }

    fun selectPreset(preset: KeyboardTheme) {
        val context = getApplication<Application>()
        ThemeManager.saveTheme(context, preset)
        _uiState.value = _uiState.value.copy(
            currentTheme = preset,
            selectedPresetId = preset.id,
            isNumberRowEnabled = preset.showNumberRow,
            keyBordersMode = preset.keyBordersMode,
            isKeyBordersEnabled = preset.isBordersActive(),
            keyShape = preset.keyShape
        )
    }

    fun createAndSaveCustomTheme(
        name: String,
        keyboardBgColor: Int,
        keyBgColor: Int,
        keyTextColor: Int,
        accentColor: Int,
        keyShape: String = KeyboardTheme.KEY_SHAPE_ROUNDED,
        keyBordersMode: String = KeyboardTheme.BORDERS_ON
    ) {
        val context = getApplication<Application>()
        val customTheme = KeyboardTheme(
            id = "custom_" + System.currentTimeMillis(),
            name = name.ifBlank { "Custom Theme" },
            category = "My Themes",
            keyboardBgColor = keyboardBgColor,
            keyBgColor = keyBgColor,
            keyTextColor = keyTextColor,
            accentColor = accentColor,
            keyShape = keyShape,
            keyBordersMode = keyBordersMode,
            isCustom = true
        )
        ThemeManager.saveTheme(context, customTheme)
        _uiState.value = _uiState.value.copy(
            currentTheme = customTheme,
            selectedPresetId = customTheme.id,
            keyShape = keyShape,
            keyBordersMode = keyBordersMode
        )
    }

    fun deleteCustomTheme(themeId: String) {
        val context = getApplication<Application>()
        viewModelScope.launch {
            ThemeManager.deleteCustomTheme(context, themeId)
            if (_uiState.value.currentTheme.id == themeId) {
                selectPreset(KeyboardTheme.DARK_THEME)
            }
        }
    }

    fun setSolidColor(colorInt: Int, name: String) {
        val current = _uiState.value.currentTheme
        val updated = current.copy(
            id = "solid_${name.lowercase()}",
            name = name,
            keyboardBgColor = colorInt,
            photoPath = null
        )
        val context = getApplication<Application>()
        ThemeManager.saveTheme(context, updated)
        _uiState.value = _uiState.value.copy(
            currentTheme = updated,
            selectedPresetId = updated.id
        )
    }

    fun setPhotoTheme(photoPath: String?, darkness: Float = 0.20f, blur: Float = 15f) {
        val current = _uiState.value.currentTheme
        val updated = current.copy(
            id = "photo_custom",
            name = "Photo Theme",
            photoPath = photoPath,
            photoDarkness = darkness,
            photoBlur = blur,
            keyBgColor = android.graphics.Color.parseColor("#E6FFFFFF"),
            keyTextColor = android.graphics.Color.BLACK,
            keyboardBgColor = android.graphics.Color.BLACK
        )
        val context = getApplication<Application>()
        ThemeManager.saveTheme(context, updated)
        _uiState.value = _uiState.value.copy(
            currentTheme = updated,
            selectedPresetId = "photo_custom",
            isKeyBordersEnabled = updated.isBordersActive()
        )
    }

    fun updatePhotoDarkness(darkness: Float) {
        val current = _uiState.value.currentTheme
        val updated = current.copy(photoDarkness = darkness.coerceIn(0f, 0.80f))
        val context = getApplication<Application>()
        ThemeManager.saveTheme(context, updated)
        _uiState.value = _uiState.value.copy(currentTheme = updated)
    }

    fun updatePhotoOpacity(opacity: Float) {
        updatePhotoDarkness(opacity)
    }

    fun updatePhotoBlur(blur: Float) {
        val current = _uiState.value.currentTheme
        val updated = current.copy(photoBlur = blur.coerceIn(0f, 100f))
        val context = getApplication<Application>()
        ThemeManager.saveTheme(context, updated)
        _uiState.value = _uiState.value.copy(currentTheme = updated)
    }

    fun removePhotoTheme() {
        val current = _uiState.value.currentTheme
        val updated = current.copy(
            id = "dark_theme",
            name = "Dark Theme",
            photoPath = null
        )
        val context = getApplication<Application>()
        ThemeManager.saveTheme(context, updated)
        _uiState.value = _uiState.value.copy(
            currentTheme = updated,
            selectedPresetId = updated.id,
            isKeyBordersEnabled = updated.isBordersActive()
        )
    }

    fun openColorPicker(target: ColorTarget) {
        _uiState.value = _uiState.value.copy(activeColorTarget = target)
    }

    fun closeColorPicker() {
        _uiState.value = _uiState.value.copy(activeColorTarget = null)
    }

    fun onColorSelected(colorInt: Int) {
        val target = _uiState.value.activeColorTarget ?: return
        val current = _uiState.value.currentTheme
        val hasPhoto = !current.photoPath.isNullOrBlank()
        val themeId = if (hasPhoto) "photo_custom" else "custom"
        val themeName = if (hasPhoto) "Photo Theme" else "Custom Theme"

        val updated = when (target) {
            ColorTarget.KEYBOARD_BG -> current.copy(id = themeId, name = themeName, keyboardBgColor = colorInt)
            ColorTarget.KEY_BG -> current.copy(id = themeId, name = themeName, keyBgColor = colorInt)
            ColorTarget.KEY_TEXT -> current.copy(id = themeId, name = themeName, keyTextColor = colorInt)
            ColorTarget.ACCENT -> current.copy(id = themeId, name = themeName, accentColor = colorInt)
        }

        val context = getApplication<Application>()
        ThemeManager.saveTheme(context, updated)

        _uiState.value = _uiState.value.copy(
            currentTheme = updated,
            selectedPresetId = themeId,
            activeColorTarget = null
        )
    }

    fun refreshKeyboardStatus() {
        val context = getApplication<Application>()
        _uiState.value = _uiState.value.copy(
            isKeyboardEnabled = checkKeyboardEnabled(context),
            isKeyboardSelected = checkKeyboardSelected(context)
        )
    }

    private fun checkKeyboardEnabled(context: Context): Boolean {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return false
        val pkgName = context.packageName
        return imm.enabledInputMethodList.any { it.packageName == pkgName }
    }

    private fun checkKeyboardSelected(context: Context): Boolean {
        val defaultIme = Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD) ?: ""
        return defaultIme.contains(context.packageName)
    }
}
