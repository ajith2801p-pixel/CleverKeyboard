package com.cleverkeyboard.app.data

import android.content.Context
import android.content.SharedPreferences
import com.cleverkeyboard.app.data.db.AppDatabase
import com.cleverkeyboard.app.data.db.entity.CustomThemeEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object ThemeManager {
    private const val PREFS_NAME = "clever_keyboard_theme_prefs"
    private const val KEY_THEME_ID = "theme_id"
    private const val KEY_THEME_NAME = "theme_name"
    private const val KEY_THEME_CATEGORY = "theme_category"
    private const val KEY_KEYBOARD_BG = "keyboard_bg_color"
    private const val KEY_KEY_BG = "key_bg_color"
    private const val KEY_KEY_TEXT = "key_text_color"
    private const val KEY_ACCENT = "accent_color"
    private const val KEY_GRADIENT_START = "gradient_start_color"
    private const val KEY_GRADIENT_END = "gradient_end_color"
    private const val KEY_PHOTO_PATH = "photo_path"
    private const val KEY_PHOTO_DARKNESS = "photo_darkness"
    private const val KEY_PHOTO_OPACITY = "photo_opacity"
    private const val KEY_PHOTO_BLUR = "photo_blur"
    private const val KEY_KEY_BORDERS_MODE = "key_borders_mode"
    private const val KEY_SHOW_NUMBER_ROW = "show_number_row"
    private const val KEY_AUTO_CAPITALIZATION = "auto_capitalization"
    private const val KEY_SHOW_AI_TOOLBAR_LABELS = "show_ai_toolbar_labels"
    private const val KEY_AUTO_NUM_PAD_FOR_OTP = "auto_num_pad_for_otp"
    private const val KEY_NEXT_WORD_PREDICTION = "next_word_prediction"
    private const val KEY_PERSONALIZED_SUGGESTIONS = "personalized_suggestions"
    private const val KEY_DISMISS_BUTTON_MODE = "dismiss_button_mode"
    private const val KEY_KEY_SHAPE = "key_shape"
    private const val KEY_KEYBOARD_HEIGHT = "keyboard_height_percent"
    private const val KEY_SOUND_ENABLED = "sound_enabled"
    private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
    private const val KEY_BORDER_THICKNESS = "border_thickness"
    private const val KEY_KEY_CORNER_RADIUS = "key_corner_radius"
    private const val KEY_KEY_SPACING = "key_spacing"
    private const val KEY_KEYBOARD_OPACITY = "keyboard_opacity"
    private const val KEY_BACKGROUND_BLUR = "background_blur"
    private const val KEY_KEY_SHADOW_ENABLED = "key_shadow_enabled"
    private const val KEY_KEY_OPACITY = "key_opacity"
    private const val KEY_KEY_PRESSED_COLOR = "key_pressed_color"
    private const val KEY_FUNCTION_KEY_COLOR = "function_key_color"
    private const val KEY_TOOLBAR_TEXT_COLOR = "toolbar_text_color"
    private const val KEY_KEY_POPUP_COLOR = "key_popup_color"
    private const val KEY_SWIPE_TRAIL_COLOR = "swipe_trail_color"
    private const val KEY_TEXT_SHADOW_ENABLED = "text_shadow_enabled"

    private var sharedPreferences: SharedPreferences? = null
    private val _currentTheme = MutableStateFlow(KeyboardTheme.DARK_THEME)
    val currentTheme: StateFlow<KeyboardTheme> = _currentTheme.asStateFlow()

    fun init(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            _currentTheme.value = loadSavedTheme()
        }
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return sharedPreferences ?: context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).also {
            sharedPreferences = it
        }
    }

    fun getTheme(context: Context): KeyboardTheme {
        val prefs = getPrefs(context)
        val id = prefs.getString(KEY_THEME_ID, KeyboardTheme.DARK_THEME.id) ?: KeyboardTheme.DARK_THEME.id
        val name = prefs.getString(KEY_THEME_NAME, KeyboardTheme.DARK_THEME.name) ?: KeyboardTheme.DARK_THEME.name
        val category = prefs.getString(KEY_THEME_CATEGORY, KeyboardTheme.DARK_THEME.category) ?: KeyboardTheme.DARK_THEME.category
        val kbBg = prefs.getInt(KEY_KEYBOARD_BG, KeyboardTheme.DARK_THEME.keyboardBgColor)
        val keyBg = prefs.getInt(KEY_KEY_BG, KeyboardTheme.DARK_THEME.keyBgColor)
        val keyText = prefs.getInt(KEY_KEY_TEXT, KeyboardTheme.DARK_THEME.keyTextColor)
        val accent = prefs.getInt(KEY_ACCENT, KeyboardTheme.DARK_THEME.accentColor)
        val gradStart = if (prefs.contains(KEY_GRADIENT_START)) prefs.getInt(KEY_GRADIENT_START, 0) else null
        val gradEnd = if (prefs.contains(KEY_GRADIENT_END)) prefs.getInt(KEY_GRADIENT_END, 0) else null
        val photoPath = prefs.getString(KEY_PHOTO_PATH, null)
        val photoDarkness = if (prefs.contains(KEY_PHOTO_DARKNESS)) {
            prefs.getFloat(KEY_PHOTO_DARKNESS, 0.40f)
        } else {
            0.40f
        }
        val photoBlur = if (prefs.contains(KEY_PHOTO_BLUR)) {
            prefs.getFloat(KEY_PHOTO_BLUR, 25f)
        } else {
            25f
        }
        val keyBordersMode = prefs.getString(KEY_KEY_BORDERS_MODE, KeyboardTheme.BORDERS_AUTO) ?: KeyboardTheme.BORDERS_AUTO
        val showNumberRow = prefs.getBoolean(KEY_SHOW_NUMBER_ROW, true)
        val autoCapitalization = prefs.getBoolean(KEY_AUTO_CAPITALIZATION, true)
        val showAiToolbarLabels = prefs.getBoolean(KEY_SHOW_AI_TOOLBAR_LABELS, true)
        val autoNumPadForOtp = prefs.getBoolean(KEY_AUTO_NUM_PAD_FOR_OTP, true)
        val nextWordPrediction = prefs.getBoolean(KEY_NEXT_WORD_PREDICTION, true)
        val personalizedSuggestions = prefs.getBoolean(KEY_PERSONALIZED_SUGGESTIONS, true)
        val dismissButtonMode = prefs.getString(KEY_DISMISS_BUTTON_MODE, KeyboardTheme.DISMISS_AUTO) ?: KeyboardTheme.DISMISS_AUTO

        val keyShape = prefs.getString(KEY_KEY_SHAPE, KeyboardTheme.KEY_SHAPE_ROUNDED) ?: KeyboardTheme.KEY_SHAPE_ROUNDED
        val kbHeight = prefs.getInt(KEY_KEYBOARD_HEIGHT, 100)
        val borderThickness = prefs.getFloat(KEY_BORDER_THICKNESS, 1.0f)
        val keyCornerRadius = prefs.getFloat(KEY_KEY_CORNER_RADIUS, 8f)
        val keySpacing = prefs.getFloat(KEY_KEY_SPACING, 2.0f)
        val keyboardOpacity = prefs.getFloat(KEY_KEYBOARD_OPACITY, 1.0f)
        val backgroundBlur = prefs.getFloat(KEY_BACKGROUND_BLUR, 0f)
        val keyShadowEnabled = prefs.getBoolean(KEY_KEY_SHADOW_ENABLED, false)
        val keyOpacity = prefs.getFloat(KEY_KEY_OPACITY, 1.0f)
        val keyPressedColor = if (prefs.contains(KEY_KEY_PRESSED_COLOR)) prefs.getInt(KEY_KEY_PRESSED_COLOR, 0) else null
        val functionKeyColor = if (prefs.contains(KEY_FUNCTION_KEY_COLOR)) prefs.getInt(KEY_FUNCTION_KEY_COLOR, 0) else null
        val toolbarTextColor = if (prefs.contains(KEY_TOOLBAR_TEXT_COLOR)) prefs.getInt(KEY_TOOLBAR_TEXT_COLOR, 0) else null
        val keyPopupColor = if (prefs.contains(KEY_KEY_POPUP_COLOR)) prefs.getInt(KEY_KEY_POPUP_COLOR, 0) else null
        val swipeTrailColor = if (prefs.contains(KEY_SWIPE_TRAIL_COLOR)) prefs.getInt(KEY_SWIPE_TRAIL_COLOR, 0) else null
        val textShadowEnabled = prefs.getBoolean(KEY_TEXT_SHADOW_ENABLED, false)

        val theme = KeyboardTheme(
            id = id,
            name = name,
            category = category,
            keyboardBgColor = kbBg,
            keyBgColor = keyBg,
            keyTextColor = keyText,
            accentColor = accent,
            gradientStartColor = gradStart,
            gradientEndColor = gradEnd,
            photoPath = photoPath,
            photoDarkness = photoDarkness,
            photoBlur = photoBlur,
            keyBordersMode = keyBordersMode,
            showNumberRow = showNumberRow,
            autoCapitalization = autoCapitalization,
            showAiToolbarLabels = showAiToolbarLabels,
            autoNumPadForOtp = autoNumPadForOtp,
            nextWordPrediction = nextWordPrediction,
            personalizedSuggestions = personalizedSuggestions,
            dismissButtonMode = dismissButtonMode,
            keyShape = keyShape,
            keyboardHeightPercent = kbHeight,
            borderThickness = borderThickness,
            keyCornerRadius = keyCornerRadius,
            keySpacing = keySpacing,
            keyboardOpacity = keyboardOpacity,
            backgroundBlur = backgroundBlur,
            keyShadowEnabled = keyShadowEnabled,
            keyOpacity = keyOpacity,
            keyPressedColor = keyPressedColor,
            functionKeyColor = functionKeyColor,
            toolbarTextColor = toolbarTextColor,
            keyPopupColor = keyPopupColor,
            swipeTrailColor = swipeTrailColor,
            textShadowEnabled = textShadowEnabled
        )
        _currentTheme.value = theme
        return theme
    }

    private fun loadSavedTheme(): KeyboardTheme {
        val prefs = sharedPreferences ?: return KeyboardTheme.DARK_THEME
        val id = prefs.getString(KEY_THEME_ID, KeyboardTheme.DARK_THEME.id) ?: KeyboardTheme.DARK_THEME.id
        val name = prefs.getString(KEY_THEME_NAME, KeyboardTheme.DARK_THEME.name) ?: KeyboardTheme.DARK_THEME.name
        val category = prefs.getString(KEY_THEME_CATEGORY, KeyboardTheme.DARK_THEME.category) ?: KeyboardTheme.DARK_THEME.category
        val kbBg = prefs.getInt(KEY_KEYBOARD_BG, KeyboardTheme.DARK_THEME.keyboardBgColor)
        val keyBg = prefs.getInt(KEY_KEY_BG, KeyboardTheme.DARK_THEME.keyBgColor)
        val keyText = prefs.getInt(KEY_KEY_TEXT, KeyboardTheme.DARK_THEME.keyTextColor)
        val accent = prefs.getInt(KEY_ACCENT, KeyboardTheme.DARK_THEME.accentColor)
        val gradStart = if (prefs.contains(KEY_GRADIENT_START)) prefs.getInt(KEY_GRADIENT_START, 0) else null
        val gradEnd = if (prefs.contains(KEY_GRADIENT_END)) prefs.getInt(KEY_GRADIENT_END, 0) else null
        val photoPath = prefs.getString(KEY_PHOTO_PATH, null)
        val photoDarkness = if (prefs.contains(KEY_PHOTO_DARKNESS)) {
            prefs.getFloat(KEY_PHOTO_DARKNESS, 0.40f)
        } else {
            0.40f
        }
        val photoBlur = if (prefs.contains(KEY_PHOTO_BLUR)) {
            prefs.getFloat(KEY_PHOTO_BLUR, 25f)
        } else {
            25f
        }
        val keyBordersMode = prefs.getString(KEY_KEY_BORDERS_MODE, KeyboardTheme.BORDERS_AUTO) ?: KeyboardTheme.BORDERS_AUTO
        val showNumberRow = prefs.getBoolean(KEY_SHOW_NUMBER_ROW, true)
        val autoCapitalization = prefs.getBoolean(KEY_AUTO_CAPITALIZATION, true)
        val showAiToolbarLabels = prefs.getBoolean(KEY_SHOW_AI_TOOLBAR_LABELS, true)
        val autoNumPadForOtp = prefs.getBoolean(KEY_AUTO_NUM_PAD_FOR_OTP, true)
        val nextWordPrediction = prefs.getBoolean(KEY_NEXT_WORD_PREDICTION, true)
        val personalizedSuggestions = prefs.getBoolean(KEY_PERSONALIZED_SUGGESTIONS, true)
        val dismissButtonMode = prefs.getString(KEY_DISMISS_BUTTON_MODE, KeyboardTheme.DISMISS_AUTO) ?: KeyboardTheme.DISMISS_AUTO
        val keyShape = prefs.getString(KEY_KEY_SHAPE, KeyboardTheme.KEY_SHAPE_ROUNDED) ?: KeyboardTheme.KEY_SHAPE_ROUNDED
        val kbHeight = prefs.getInt(KEY_KEYBOARD_HEIGHT, 100)
        val borderThickness = prefs.getFloat(KEY_BORDER_THICKNESS, 1.0f)
        val keyCornerRadius = prefs.getFloat(KEY_KEY_CORNER_RADIUS, 8f)
        val keySpacing = prefs.getFloat(KEY_KEY_SPACING, 2.0f)
        val keyboardOpacity = prefs.getFloat(KEY_KEYBOARD_OPACITY, 1.0f)
        val backgroundBlur = prefs.getFloat(KEY_BACKGROUND_BLUR, 0f)
        val keyShadowEnabled = prefs.getBoolean(KEY_KEY_SHADOW_ENABLED, false)
        val keyOpacity = prefs.getFloat(KEY_KEY_OPACITY, 1.0f)
        val keyPressedColor = if (prefs.contains(KEY_KEY_PRESSED_COLOR)) prefs.getInt(KEY_KEY_PRESSED_COLOR, 0) else null
        val functionKeyColor = if (prefs.contains(KEY_FUNCTION_KEY_COLOR)) prefs.getInt(KEY_FUNCTION_KEY_COLOR, 0) else null
        val toolbarTextColor = if (prefs.contains(KEY_TOOLBAR_TEXT_COLOR)) prefs.getInt(KEY_TOOLBAR_TEXT_COLOR, 0) else null
        val keyPopupColor = if (prefs.contains(KEY_KEY_POPUP_COLOR)) prefs.getInt(KEY_KEY_POPUP_COLOR, 0) else null
        val swipeTrailColor = if (prefs.contains(KEY_SWIPE_TRAIL_COLOR)) prefs.getInt(KEY_SWIPE_TRAIL_COLOR, 0) else null
        val textShadowEnabled = prefs.getBoolean(KEY_TEXT_SHADOW_ENABLED, false)

        return KeyboardTheme(
            id = id,
            name = name,
            category = category,
            keyboardBgColor = kbBg,
            keyBgColor = keyBg,
            keyTextColor = keyText,
            accentColor = accent,
            gradientStartColor = gradStart,
            gradientEndColor = gradEnd,
            photoPath = photoPath,
            photoDarkness = photoDarkness,
            photoBlur = photoBlur,
            keyBordersMode = keyBordersMode,
            showNumberRow = showNumberRow,
            autoCapitalization = autoCapitalization,
            showAiToolbarLabels = showAiToolbarLabels,
            autoNumPadForOtp = autoNumPadForOtp,
            nextWordPrediction = nextWordPrediction,
            personalizedSuggestions = personalizedSuggestions,
            dismissButtonMode = dismissButtonMode,
            keyShape = keyShape,
            keyboardHeightPercent = kbHeight,
            borderThickness = borderThickness,
            keyCornerRadius = keyCornerRadius,
            keySpacing = keySpacing,
            keyboardOpacity = keyboardOpacity,
            backgroundBlur = backgroundBlur,
            keyShadowEnabled = keyShadowEnabled,
            keyOpacity = keyOpacity,
            keyPressedColor = keyPressedColor,
            functionKeyColor = functionKeyColor,
            toolbarTextColor = toolbarTextColor,
            keyPopupColor = keyPopupColor,
            swipeTrailColor = swipeTrailColor,
            textShadowEnabled = textShadowEnabled
        )
    }

    fun saveTheme(context: Context, theme: KeyboardTheme) {
        val prefs = getPrefs(context)
        val editor = prefs.edit()
            .putString(KEY_THEME_ID, theme.id)
            .putString(KEY_THEME_NAME, theme.name)
            .putString(KEY_THEME_CATEGORY, theme.category)
            .putInt(KEY_KEYBOARD_BG, theme.keyboardBgColor)
            .putInt(KEY_KEY_BG, theme.keyBgColor)
            .putInt(KEY_KEY_TEXT, theme.keyTextColor)
            .putInt(KEY_ACCENT, theme.accentColor)

        if (theme.gradientStartColor != null && theme.gradientEndColor != null) {
            editor.putInt(KEY_GRADIENT_START, theme.gradientStartColor)
            editor.putInt(KEY_GRADIENT_END, theme.gradientEndColor)
        } else {
            editor.remove(KEY_GRADIENT_START)
            editor.remove(KEY_GRADIENT_END)
        }

        editor.putString(KEY_PHOTO_PATH, theme.photoPath)
            .putFloat(KEY_PHOTO_DARKNESS, theme.photoDarkness)
            .putFloat(KEY_PHOTO_OPACITY, theme.photoDarkness)
            .putFloat(KEY_PHOTO_BLUR, theme.photoBlur)
            .putString(KEY_KEY_BORDERS_MODE, theme.keyBordersMode)
            .putBoolean(KEY_SHOW_NUMBER_ROW, theme.showNumberRow)
            .putBoolean(KEY_AUTO_CAPITALIZATION, theme.autoCapitalization)
            .putBoolean(KEY_SHOW_AI_TOOLBAR_LABELS, theme.showAiToolbarLabels)
            .putBoolean(KEY_AUTO_NUM_PAD_FOR_OTP, theme.autoNumPadForOtp)
            .putBoolean(KEY_NEXT_WORD_PREDICTION, theme.nextWordPrediction)
            .putBoolean(KEY_PERSONALIZED_SUGGESTIONS, theme.personalizedSuggestions)
            .putString(KEY_DISMISS_BUTTON_MODE, theme.dismissButtonMode)
            .putString(KEY_KEY_SHAPE, theme.keyShape)
            .putInt(KEY_KEYBOARD_HEIGHT, theme.keyboardHeightPercent)
            .putFloat(KEY_BORDER_THICKNESS, theme.borderThickness)
            .putFloat(KEY_KEY_CORNER_RADIUS, theme.keyCornerRadius)
            .putFloat(KEY_KEY_SPACING, theme.keySpacing)
            .putFloat(KEY_KEYBOARD_OPACITY, theme.keyboardOpacity)
            .putFloat(KEY_BACKGROUND_BLUR, theme.backgroundBlur)
            .putBoolean(KEY_KEY_SHADOW_ENABLED, theme.keyShadowEnabled)
            .putFloat(KEY_KEY_OPACITY, theme.keyOpacity)
            .putBoolean(KEY_TEXT_SHADOW_ENABLED, theme.textShadowEnabled)

        if (theme.keyPressedColor != null) editor.putInt(KEY_KEY_PRESSED_COLOR, theme.keyPressedColor) else editor.remove(KEY_KEY_PRESSED_COLOR)
        if (theme.functionKeyColor != null) editor.putInt(KEY_FUNCTION_KEY_COLOR, theme.functionKeyColor) else editor.remove(KEY_FUNCTION_KEY_COLOR)
        if (theme.toolbarTextColor != null) editor.putInt(KEY_TOOLBAR_TEXT_COLOR, theme.toolbarTextColor) else editor.remove(KEY_TOOLBAR_TEXT_COLOR)
        if (theme.keyPopupColor != null) editor.putInt(KEY_KEY_POPUP_COLOR, theme.keyPopupColor) else editor.remove(KEY_KEY_POPUP_COLOR)
        if (theme.swipeTrailColor != null) editor.putInt(KEY_SWIPE_TRAIL_COLOR, theme.swipeTrailColor) else editor.remove(KEY_SWIPE_TRAIL_COLOR)

        editor.apply()

        _currentTheme.value = theme

        if (theme.isCustom) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getInstance(context)
                    db.customThemeDao().insertOrUpdateTheme(
                        CustomThemeEntity(
                            themeId = theme.id,
                            name = theme.name,
                            keyboardBgColor = theme.keyboardBgColor,
                            keyBgColor = theme.keyBgColor,
                            keyTextColor = theme.keyTextColor,
                            accentColor = theme.accentColor,
                            photoPath = theme.photoPath,
                            photoDarkness = theme.photoDarkness,
                            photoBlur = theme.photoBlur,
                            keyBordersMode = theme.keyBordersMode,
                            isFavorite = theme.isFavorite
                        )
                    )
                } catch (_: Exception) {}
            }
        }
    }

    fun getCustomThemesFlow(context: Context): Flow<List<CustomThemeEntity>> {
        return AppDatabase.getInstance(context).customThemeDao().getAllThemesFlow()
    }

    suspend fun deleteCustomTheme(context: Context, themeId: String) {
        AppDatabase.getInstance(context).customThemeDao().deleteThemeById(themeId)
    }

    fun setDismissButtonMode(context: Context, mode: String) {
        val current = getTheme(context)
        saveTheme(context, current.copy(dismissButtonMode = mode))
    }

    fun setKeyBordersMode(context: Context, mode: String) {
        val current = getTheme(context)
        saveTheme(context, current.copy(keyBordersMode = mode))
    }

    fun setNumberRow(context: Context, enabled: Boolean) {
        val current = getTheme(context)
        saveTheme(context, current.copy(showNumberRow = enabled))
    }

    fun setAutoCapitalization(context: Context, enabled: Boolean) {
        val current = getTheme(context)
        saveTheme(context, current.copy(autoCapitalization = enabled))
    }

    fun setShowAiToolbarLabels(context: Context, enabled: Boolean) {
        val current = getTheme(context)
        saveTheme(context, current.copy(showAiToolbarLabels = enabled))
    }

    fun setAutoNumPadForOtp(context: Context, enabled: Boolean) {
        val current = getTheme(context)
        saveTheme(context, current.copy(autoNumPadForOtp = enabled))
    }

    fun setNextWordPrediction(context: Context, enabled: Boolean) {
        val current = getTheme(context)
        saveTheme(context, current.copy(nextWordPrediction = enabled))
    }

    fun setPersonalizedSuggestions(context: Context, enabled: Boolean) {
        val current = getTheme(context)
        saveTheme(context, current.copy(personalizedSuggestions = enabled))
    }

    fun isSoundEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_SOUND_ENABLED, false)
    }

    fun setSoundEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }

    fun isVibrationEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_VIBRATION_ENABLED, true)
    }

    fun setVibrationEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_VIBRATION_ENABLED, enabled).apply()
    }
}
