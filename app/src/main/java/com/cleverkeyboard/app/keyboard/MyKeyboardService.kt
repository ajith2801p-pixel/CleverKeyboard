package com.cleverkeyboard.app.keyboard

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.graphics.Typeface
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.cleverkeyboard.app.MainActivity
import com.cleverkeyboard.app.R
import com.cleverkeyboard.app.voice.VoicePermissionActivity
import com.cleverkeyboard.app.network.AiService
import com.cleverkeyboard.app.data.ApiKeyManager
import com.cleverkeyboard.app.autocorrect.AutoCorrectManager
import com.cleverkeyboard.app.autocorrect.SuggestionItem
import com.cleverkeyboard.app.clipboard.ClipboardHistoryManager
import com.cleverkeyboard.app.clipboard.ClipboardItem
import com.cleverkeyboard.app.emoji.EmojiManager
import com.cleverkeyboard.app.data.KeyboardTheme
import com.cleverkeyboard.app.data.ThemeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.ArrayDeque

@Suppress("DEPRECATION")
class MyKeyboardService : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val aiService = AiService.getInstance()
    private val mainHandler = Handler(Looper.getMainLooper())

    private var keyboardView: CustomKeyboardView? = null
    private var qwertyKeyboard: Keyboard? = null
    private var symbolsKeyboard: Keyboard? = null
    private var symbolsShiftKeyboard: Keyboard? = null
    private var tamilKeyboard: Keyboard? = null
    private var tamilShiftKeyboard: Keyboard? = null

    // Undo Autocorrect state (Gboard style)
    private var lastAutocorrectOriginal: String? = null
    private var lastAutocorrectReplacement: String? = null

    // Shift & Auto-Capitalization state
    private var isShifted = false
    private var isShiftLocked = false
    private var isAutoCapitalized = false
    private var lastShiftPressTime = 0L
    private var lastSpacePressTime = 0L

    // UI elements from keyboard_view.xml
    private var keyboardBgImage: ImageView? = null
    private var keyboardBgOverlay: View? = null
    private var keyboardRoot: LinearLayout? = null
    private var aiToolbarContainer: LinearLayout? = null

    // Row 0 AI buttons & labels
    private var btnAiTranslate: View? = null
    private var btnAiSparkle: View? = null
    private var btnAiChat: View? = null
    private var btnClipboard: View? = null
    private var btnAiReply: View? = null
    private var btnEmoji: View? = null
    private var btnMoreDots: View? = null
    private var btnMicToolbar: View? = null

    private var textAiTranslate: TextView? = null
    private var textAiSparkle: TextView? = null
    private var textAiChat: TextView? = null
    private var textClipboard: TextView? = null
    private var textAiReply: TextView? = null
    private var textEmoji: TextView? = null
    private var textMoreDots: TextView? = null
    private var textMicToolbar: TextView? = null

    // Specialized Mode Panels
    private var numPadContainer: View? = null
    private var emojiContainer: View? = null
    private var clipboardContainer: View? = null
    private var moreMenuContainer: View? = null
    private var moreSubpanelContainer: LinearLayout? = null
    private var keyboardBottomDismissBar: LinearLayout? = null
    private var btnKeyboardDismiss: View? = null
    private var btnBottomSwitchKb: View? = null
    private var iconKeyboardDismiss: ImageView? = null
    private var iconBottomSwitchKb: ImageView? = null

    // Mode States
    private var isOneHanded = false
    private var oneHandedSideLeft = true
    private var isFloating = false

    // Undo / Redo Stacks
    private val undoStack = ArrayDeque<String>()
    private val redoStack = ArrayDeque<String>()

    // Clipboard listener
    private var clipboardManager: ClipboardManager? = null

    // Emoji search & category state
    private var currentEmojiCategory = 0
    private var isEmojiSearchMode = false
    private var emojiGrid: GridLayout? = null
    private var emojiCategoriesContainer: LinearLayout? = null
    private var emojiCategoriesScroll: View? = null
    private var emojiSearchInput: EditText? = null
    private var emojiBottomBar: View? = null

    // Clipboard view references
    private var clipboardItemsContainer: LinearLayout? = null
    private var clipboardEmptyView: View? = null
    private var clipboardScrollView: ScrollView? = null

    // Row 1 Suggestion Bar (5 Suggestions)
    private var suggestionBarContainer: LinearLayout? = null
    private var suggestion1: TextView? = null
    private var suggestion2: TextView? = null
    private var suggestion3: TextView? = null
    private var suggestion4: TextView? = null
    private var suggestion5: TextView? = null

    // Row 2 Number Row
    private var numberRowContainer: LinearLayout? = null

    // Sub-panels & Loaders
    private var aiLoadingContainer: LinearLayout? = null
    private var aiLoadingText: TextView? = null
    private var subOptionsPanel: LinearLayout? = null
    private var subOptionsContainer: LinearLayout? = null
    private var btnCloseSubPanel: Button? = null
    private var aiResultPanel: LinearLayout? = null
    private var aiResultText: TextView? = null
    private var btnAiCopy: Button? = null
    private var btnAiSend: Button? = null

    // AI Tone Selector state
    private var currentAiToneMode: String = "REPLY"
    private var currentAiToneInputText: String = ""
    private var currentAiToneResult: String = ""

    // Clipboard clear confirmation state
    private var lastClearAllClickTime: Long = 0L

    private var currentTheme: KeyboardTheme = KeyboardTheme.DARK_THEME
    private var audioManager: AudioManager? = null
    private var vibrator: Vibrator? = null

    // Voice Typing
    private var speechRecognizer: SpeechRecognizer? = null

    companion object {
        private const val TAG = "MyKeyboardService"
        private const val CODE_SYMBOL_SHIFT = -6
        private const val CODE_GLOBE = -100
        private const val CODE_MIC = -10
        private const val CODE_NUMPAD = -200
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        ThemeManager.init(this)

        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        try {
            clipboardManager?.addPrimaryClipChangedListener {
                val clip = clipboardManager?.primaryClip
                if (clip != null && clip.itemCount > 0) {
                    val text = clip.getItemAt(0)?.coerceToText(this)?.toString()
                    if (!text.isNullOrBlank()) {
                        val info = currentInputEditorInfo
                        val variation = (info?.inputType ?: 0) and EditorInfo.TYPE_MASK_VARIATION
                        val isPassword = variation == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD ||
                                variation == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                                variation == EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD
                        if (!isPassword) {
                            ClipboardHistoryManager.addItem(this, text)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding clipboard listener", e)
        }

        // Live observation of Theme & Key Borders changes
        serviceScope.launch {
            ThemeManager.currentTheme.collect { newTheme ->
                applyTheme(newTheme)
            }
        }

        // Preload dictionaries in background
        serviceScope.launch(Dispatchers.IO) {
            AutoCorrectManager.preload(this@MyKeyboardService)
        }
    }

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_view, null)

        // Background views
        keyboardBgImage = view.findViewById(R.id.keyboard_bg_image)
        keyboardBgOverlay = view.findViewById(R.id.keyboard_bg_overlay)
        keyboardRoot = view.findViewById(R.id.keyboard_root)

        // Row 0 AI Toolbar
        aiToolbarContainer = view.findViewById(R.id.ai_toolbar_container)
        btnAiTranslate = view.findViewById(R.id.btn_ai_translate)
        btnAiSparkle = view.findViewById(R.id.btn_ai_sparkle)
        btnAiChat = view.findViewById(R.id.btn_ai_chat)
        btnClipboard = view.findViewById(R.id.btn_clipboard)
        btnAiReply = view.findViewById(R.id.btn_ai_reply)
        btnEmoji = view.findViewById(R.id.btn_emoji)
        btnMoreDots = view.findViewById(R.id.btn_more_dots)
        btnMicToolbar = view.findViewById(R.id.btn_mic_toolbar)

        textAiTranslate = view.findViewById(R.id.text_ai_translate)
        textAiSparkle = view.findViewById(R.id.text_ai_sparkle)
        textAiChat = view.findViewById(R.id.text_ai_chat)
        textClipboard = view.findViewById(R.id.text_clipboard)
        textAiReply = view.findViewById(R.id.text_ai_reply)
        textEmoji = view.findViewById(R.id.text_emoji)
        textMoreDots = view.findViewById(R.id.text_more_dots)
        textMicToolbar = view.findViewById(R.id.text_mic_toolbar)

        // Specialized Panels
        numPadContainer = view.findViewById(R.id.num_pad_container)
        emojiContainer = view.findViewById(R.id.emoji_container)
        clipboardContainer = view.findViewById(R.id.clipboard_container)
        moreMenuContainer = view.findViewById(R.id.more_menu_container)
        moreSubpanelContainer = view.findViewById(R.id.more_subpanel_container)

        // Status & Sub-options
        aiLoadingContainer = view.findViewById(R.id.ai_loading_container)
        aiLoadingText = view.findViewById(R.id.ai_loading_text)
        subOptionsPanel = view.findViewById(R.id.sub_options_panel)
        subOptionsContainer = view.findViewById(R.id.sub_options_container)
        btnCloseSubPanel = view.findViewById(R.id.btn_close_sub_panel)
        aiResultPanel = view.findViewById(R.id.ai_result_panel)
        aiResultText = view.findViewById(R.id.ai_result_text)
        btnAiCopy = view.findViewById(R.id.btn_ai_copy)
        btnAiSend = view.findViewById(R.id.btn_ai_send)

        btnAiCopy?.setOnClickListener {
            playKeyFeedback()
            if (currentAiToneResult.isNotBlank()) {
                val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("AI Result", currentAiToneResult))
                ClipboardHistoryManager.addItem(this, currentAiToneResult)
                Toast.makeText(this, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
            }
        }

        btnAiSend?.setOnClickListener {
            playKeyFeedback()
            commitAiToneResult()
        }

        aiResultText?.setOnClickListener {
            playKeyFeedback()
            commitAiToneResult()
        }

        // Row 1 Suggestion Bar (5 Suggestions)
        suggestionBarContainer = view.findViewById(R.id.suggestion_bar_container)
        suggestion1 = view.findViewById(R.id.suggestion_1)
        suggestion2 = view.findViewById(R.id.suggestion_2)
        suggestion3 = view.findViewById(R.id.suggestion_3)
        suggestion4 = view.findViewById(R.id.suggestion_4)
        suggestion5 = view.findViewById(R.id.suggestion_5)

        // Row 2 Number Row
        numberRowContainer = view.findViewById(R.id.number_row_container)

        // Custom Keyboard View (Rows 3-6)
        keyboardView = view.findViewById(R.id.keyboard_view)

        // Initialize Keyboard layouts
        qwertyKeyboard = Keyboard(this, R.xml.qwerty)
        symbolsKeyboard = Keyboard(this, R.xml.symbols)
        symbolsShiftKeyboard = Keyboard(this, R.xml.symbols_shift)
        tamilKeyboard = Keyboard(this, R.xml.tamil)
        tamilShiftKeyboard = Keyboard(this, R.xml.tamil_shift)

        val activeLang = AutoCorrectManager.getActiveLanguage(this)
        keyboardView?.keyboard = if (activeLang == AutoCorrectManager.LANG_TA) tamilKeyboard else qwertyKeyboard
        keyboardView?.setOnKeyboardActionListener(this)

        keyboardView?.onSpaceLongPressListener = {
            showKeyboardPicker()
        }
        keyboardView?.onCursorMoveListener = { direction ->
            moveCursor(direction)
        }
        keyboardView?.onSwipeWordListener = { visitedChars ->
            val candidates = AutoCorrectManager.matchSwipeTrajectory(this, visitedChars)
            if (candidates.isNotEmpty()) {
                val topCandidate = candidates.first()
                currentInputConnection?.commitText(topCandidate + " ", 1)
                updateSuggestions()
            }
        }
        keyboardView?.onContinuousDeleteListener = {
            handleDelete()
        }
        keyboardView?.onKeySecondaryCharacter = { secondaryChar ->
            val ic = currentInputConnection
            if (ic != null) {
                lastAutocorrectOriginal = null
                lastAutocorrectReplacement = null
                ic.commitText(secondaryChar.toString(), 1)
                updateSuggestions()
            }
        }

        try {
            window?.window?.decorView?.setBackgroundColor(Color.BLACK)
        } catch (_: Exception) {}

        setupNumberRow(view)
        setupAiButtons()
        setupSuggestionBar()
        setupNumPad(view)
        setupEmojiKeyboard(view)
        setupClipboard(view)
        setupMoreMenu(view)
        setupBottomDismissBar(view)
        applyTheme(ThemeManager.getTheme(this))

        return view
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)

        // Refresh theme
        applyTheme(ThemeManager.getTheme(this))
        keyboardView?.isSwipeTypingEnabled = AutoCorrectManager.isSwipeTypingEnabled(this)

        // Security & Privacy Guard: Protect passwords, PINs and sensitive fields
        val isSensitive = com.cleverkeyboard.app.security.SensitiveFieldGuard.isSensitiveField(info)
        if (isSensitive) {
            suggestionBarContainer?.visibility = View.GONE
            btnAiTranslate?.isEnabled = false
            btnAiTranslate?.alpha = 0.35f
            btnAiSparkle?.isEnabled = false
            btnAiSparkle?.alpha = 0.35f
            btnAiChat?.isEnabled = false
            btnAiChat?.alpha = 0.35f
            btnAiReply?.isEnabled = false
            btnAiReply?.alpha = 0.35f
            btnClipboard?.isEnabled = false
            btnClipboard?.alpha = 0.35f
        } else {
            btnAiTranslate?.isEnabled = true
            btnAiTranslate?.alpha = 1.0f
            btnAiSparkle?.isEnabled = true
            btnAiSparkle?.alpha = 1.0f
            btnAiChat?.isEnabled = true
            btnAiChat?.alpha = 1.0f
            btnAiReply?.isEnabled = true
            btnAiReply?.alpha = 1.0f
            btnClipboard?.isEnabled = true
            btnClipboard?.alpha = 1.0f
        }

        // BUG 1 FIX: Default mode is ALWAYS full QWERTY keyboard.
        // Show Number Pad ONLY when TYPE_CLASS_NUMBER or TYPE_CLASS_PHONE is present,
        // and NEVER for TYPE_CLASS_TEXT or TYPE_TEXT_FLAG_MULTI_LINE.
        val theme = ThemeManager.getTheme(this)
        if (theme.autoNumPadForOtp && isNumericInputType(info)) {
            showNumPad()
        } else {
            showQwertyKeyboard()
        }
        hideSubPanel()

        // BUG 3 FIX: For multi-line fields (e.g. WhatsApp message box), Enter must insert newline.
        // Set imeOptions to IME_ACTION_NONE and show Enter icon as ↵.
        // Only for single-line fields, Enter should act as Done / Go / Search.
        val isMultiLine = (info?.inputType?.and(EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE) ?: 0) != 0
        if (isMultiLine) {
            info?.imeOptions = EditorInfo.IME_ACTION_NONE
            keyboardView?.isActionSearch = false
        } else {
            val action = info?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: EditorInfo.IME_ACTION_NONE
            keyboardView?.isActionSearch = (action == EditorInfo.IME_ACTION_SEARCH)
        }

        // Update language label on spacebar
        updateLanguageDisplay(showToast = false)

        // Auto-Capitalization for First Letter / Empty Field
        if (shouldAutoCapitalize(info)) {
            val textBefore = currentInputConnection?.getTextBeforeCursor(100, 0)?.toString() ?: ""
            val needsCapital = textBefore.isBlank() || textBefore.endsWith("\n") || Regex("[.?!]\\s+$").containsMatchIn(textBefore)
            setShiftState(shifted = needsCapital, auto = needsCapital)
        } else {
            setShiftState(shifted = false, auto = false)
        }

        updateSuggestions()
    }

    override fun onUpdateSelection(
        oldSelStart: Int,
        oldSelEnd: Int,
        newSelStart: Int,
        newSelEnd: Int,
        candidatesStart: Int,
        candidatesEnd: Int
    ) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd)
        updateSuggestions()
    }

    override fun onFinishInput() {
        super.onFinishInput()
        showQwertyKeyboard()
        deactivateEmojiSearchMode()
        resetDefaultSuggestions()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    private fun isNumericInputType(info: EditorInfo?): Boolean {
        if (info == null) return false
        val cls = info.inputType and EditorInfo.TYPE_MASK_CLASS
        // If it is text or multi-line, NEVER treat as numeric! Always QWERTY!
        if (cls == EditorInfo.TYPE_CLASS_TEXT) return false
        if ((info.inputType and EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE) != 0) return false

        return cls == EditorInfo.TYPE_CLASS_NUMBER || cls == EditorInfo.TYPE_CLASS_PHONE
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
        speechRecognizer = null
        serviceScope.cancel()
    }

    // =========================================================================
    // FIX 1: Auto Capitalization Logic
    // =========================================================================

    private fun shouldAutoCapitalize(info: EditorInfo?): Boolean {
        if (info == null) return false
        val theme = ThemeManager.getTheme(this)
        if (!theme.autoCapitalization) return false

        // Do not auto-capitalize on Tamil keyboard
        val lang = AutoCorrectManager.getActiveLanguage(this)
        if (lang == AutoCorrectManager.LANG_TA) return false

        val inputType = info.inputType
        val classType = inputType and EditorInfo.TYPE_MASK_CLASS
        if (classType != EditorInfo.TYPE_CLASS_TEXT) return false

        val variation = inputType and EditorInfo.TYPE_MASK_VARIATION
        if (variation == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD ||
            variation == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
            variation == EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
            variation == EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS ||
            variation == EditorInfo.TYPE_TEXT_VARIATION_URI) {
            return false
        }

        return true
    }

    private fun setShiftState(shifted: Boolean, auto: Boolean = false) {
        isShifted = shifted
        isAutoCapitalized = auto
        if (!shifted) isShiftLocked = false
        val activeLang = AutoCorrectManager.getActiveLanguage(this)
        if (activeLang == AutoCorrectManager.LANG_TA) {
            keyboardView?.keyboard = if (shifted) tamilShiftKeyboard else tamilKeyboard
        } else {
            qwertyKeyboard?.isShifted = shifted
            keyboardView?.keyboard = qwertyKeyboard
            keyboardView?.isShifted = shifted
        }
        keyboardView?.isShiftLocked = isShiftLocked
        keyboardView?.invalidate()
    }

    private fun handleShift() {
        val now = System.currentTimeMillis()
        if (isShiftLocked) {
            // Caps Lock is active -> Tap Shift toggles directly to Lowercase
            isShiftLocked = false
            setShiftState(shifted = false, auto = false)
        } else if (isAutoCapitalized && isShifted) {
            // Tapping Shift while auto-capitalized turns off capitalization to Lowercase
            setShiftState(shifted = false, auto = false)
        } else if (isShifted) {
            // Single-shot Shift is active:
            if (now - lastShiftPressTime < 450) {
                // Double tap within 450ms activates Caps Lock
                isShiftLocked = true
                setShiftState(shifted = true, auto = false)
            } else {
                // Tapping again toggles off to Lowercase
                setShiftState(shifted = false, auto = false)
            }
        } else {
            // Lowercase -> Single tap activates One-shot Uppercase
            setShiftState(shifted = true, auto = false)
        }
        lastShiftPressTime = now
    }

    // =========================================================================
    // Theme & Borders Handling
    // =========================================================================

    private fun applyTheme(theme: KeyboardTheme) {
        currentTheme = theme
        val isPhotoTheme = !theme.photoPath.isNullOrBlank() && File(theme.photoPath ?: "").exists()
        val bordersActive = theme.isBordersActive()
        val density = resources.displayMetrics.density

        // 1. Photo Theme handling
        if (isPhotoTheme) {
            val photoFile = File(theme.photoPath!!)
            try {
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                if (bitmap != null) {
                    keyboardBgImage?.setImageBitmap(bitmap)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && theme.photoBlur > 0.5f) {
                        val radius = theme.photoBlur.coerceIn(1f, 100f)
                        keyboardBgImage?.setRenderEffect(android.graphics.RenderEffect.createBlurEffect(radius, radius, android.graphics.Shader.TileMode.CLAMP))
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        keyboardBgImage?.setRenderEffect(null)
                    }
                    keyboardBgImage?.visibility = View.VISIBLE
                    keyboardBgOverlay?.visibility = View.VISIBLE
                    keyboardBgOverlay?.setBackgroundColor(theme.keyboardBgColor)
                    keyboardBgOverlay?.alpha = theme.photoDarkness.coerceIn(0f, 0.80f)
                    keyboardRoot?.setBackgroundColor(Color.TRANSPARENT)
                } else {
                    keyboardBgImage?.visibility = View.GONE
                    keyboardBgOverlay?.visibility = View.GONE
                    keyboardRoot?.setBackgroundColor(theme.keyboardBgColor)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error applying photo background", e)
                keyboardBgImage?.visibility = View.GONE
                keyboardBgOverlay?.visibility = View.GONE
                if (theme.hasGradient) {
                    val grad = GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        intArrayOf(theme.gradientStartColor!!, theme.gradientEndColor!!)
                    )
                    keyboardRoot?.background = grad
                } else {
                    keyboardRoot?.setBackgroundColor(theme.keyboardBgColor)
                }
            }
        } else {
            keyboardBgImage?.visibility = View.GONE
            keyboardBgOverlay?.visibility = View.GONE
            if (theme.hasGradient) {
                val grad = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(theme.gradientStartColor!!, theme.gradientEndColor!!)
                )
                keyboardRoot?.background = grad
            } else {
                keyboardRoot?.setBackgroundColor(theme.keyboardBgColor)
            }
        }

        // 2. Number Row visibility (BUG 2: only show in QWERTY mode, hide in ?123 symbol mode)
        val isSymbolsActive = keyboardView?.keyboard != null && keyboardView?.keyboard != qwertyKeyboard
        numberRowContainer?.visibility = if (theme.showNumberRow && !isSymbolsActive && !isEmojiSearchMode) View.VISIBLE else View.GONE

        // 2b. AI Toolbar styling & Labels visibility
        val showLabels = theme.showAiToolbarLabels
        val toolbarHeight = if (showLabels) (52f * density).toInt() else (38f * density).toInt()
        aiToolbarContainer?.layoutParams?.height = toolbarHeight
        aiToolbarContainer?.requestLayout()

        val labelsVisibility = if (showLabels) View.VISIBLE else View.GONE
        val toolbarLabels = listOf(
            textAiTranslate, textAiSparkle, textAiChat, textClipboard, textAiReply,
            textEmoji, textMoreDots, textMicToolbar
        )

        val labelTextColor = when {
            isPhotoTheme -> Color.WHITE
            isColorLight(theme.keyboardBgColor) -> Color.parseColor("#333333")
            else -> Color.WHITE
        }

        for (label in toolbarLabels) {
            label?.visibility = labelsVisibility
            label?.setTextColor(labelTextColor)
            if (isPhotoTheme) {
                label?.setShadowLayer(4f * density, 0f, 1.5f * density, Color.argb(220, 0, 0, 0))
            } else if (!isColorLight(theme.keyboardBgColor)) {
                label?.setShadowLayer(3f * density, 0f, 1f * density, Color.argb(160, 0, 0, 0))
            } else {
                label?.setShadowLayer(0f, 0f, 0f, 0)
            }
        }

        // 3. Key Borders styling for Number Row
        val numTextColor = if (!bordersActive) Color.WHITE else theme.keyTextColor

        val numIds = listOf(
            R.id.btn_num_1 to R.id.text_num_1,
            R.id.btn_num_2 to R.id.text_num_2,
            R.id.btn_num_3 to R.id.text_num_3,
            R.id.btn_num_4 to R.id.text_num_4,
            R.id.btn_num_5 to R.id.text_num_5,
            R.id.btn_num_6 to R.id.text_num_6,
            R.id.btn_num_7 to R.id.text_num_7,
            R.id.btn_num_8 to R.id.text_num_8,
            R.id.btn_num_9 to R.id.text_num_9,
            R.id.btn_num_0 to R.id.text_num_0
        )

        for ((btnId, txtId) in numIds) {
            val btn = keyboardRoot?.findViewById<View>(btnId)
            val txt = keyboardRoot?.findViewById<TextView>(txtId)
            val bgDrawable: Drawable = if (bordersActive) {
                GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 8f * density
                    val bg = theme.keyBgColor
                    setColor(bg)
                    val stroke = if (isColorLight(theme.keyBgColor)) Color.argb(50, 0, 0, 0) else Color.argb(70, 120, 120, 120)
                    setStroke((1f * density).toInt(), stroke)
                }
            } else {
                ColorDrawable(Color.TRANSPARENT)
            }
            btn?.background = bgDrawable
            txt?.setTextColor(numTextColor)
            txt?.setShadowLayer(0f, 0f, 0f, 0)
            txt?.typeface = if (isPhotoTheme) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        // 4. Suggestion Bar styling (5 suggestions)
        val suggViews = listOf(suggestion1, suggestion2, suggestion3, suggestion4, suggestion5)
        for (sugg in suggViews) {
            val bgDrawable: Drawable = if (bordersActive) {
                GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 8f * density
                    val bg = theme.keyBgColor
                    setColor(bg)
                    val stroke = if (isColorLight(theme.keyBgColor)) Color.argb(50, 0, 0, 0) else Color.argb(70, 120, 120, 120)
                    setStroke((1f * density).toInt(), stroke)
                }
            } else {
                ColorDrawable(Color.TRANSPARENT)
            }
            sugg?.background = bgDrawable
            sugg?.setTextColor(numTextColor)
            sugg?.setShadowLayer(0f, 0f, 0f, 0)
            sugg?.typeface = if (isPhotoTheme) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        keyboardView?.applyTheme(theme)
        updateLanguageDisplay(showToast = false)

        // 5. Bottom Dismiss Bar visibility & styling
        val showDismiss = theme.shouldShowDismissButton(this)
        keyboardBottomDismissBar?.visibility = if (showDismiss) View.VISIBLE else View.GONE
        val dismissIconColor = if (isColorLight(theme.keyboardBgColor) && theme.photoPath == null) Color.parseColor("#444444") else Color.parseColor("#CCCCCC")
        iconKeyboardDismiss?.setColorFilter(dismissIconColor)
        iconBottomSwitchKb?.setColorFilter(dismissIconColor)
    }

    private fun isColorLight(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness < 0.5
    }

    // =========================================================================
    // Row 2: Number Row Key Listeners
    // =========================================================================

    private fun setupNumberRow(view: View) {
        val numberMap = listOf(
            Triple(R.id.btn_num_1, "1", "!"),
            Triple(R.id.btn_num_2, "2", "@"),
            Triple(R.id.btn_num_3, "3", "#"),
            Triple(R.id.btn_num_4, "4", "$"),
            Triple(R.id.btn_num_5, "5", "%"),
            Triple(R.id.btn_num_6, "6", "^"),
            Triple(R.id.btn_num_7, "7", "&"),
            Triple(R.id.btn_num_8, "8", "*"),
            Triple(R.id.btn_num_9, "9", "("),
            Triple(R.id.btn_num_0, "0", ")")
        )

        for ((btnId, num, sym) in numberMap) {
            val btn = view.findViewById<View>(btnId) ?: continue
            btn.setOnClickListener {
                playKeyFeedback()
                currentInputConnection?.commitText(num, 1)
                updateSuggestions()
            }
            btn.setOnLongClickListener {
                playKeyFeedback()
                currentInputConnection?.commitText(sym, 1)
                updateSuggestions()
                true
            }
        }
    }

    // =========================================================================
    // Row 1: Suggestion Bar & Language Toggles
    // =========================================================================

    private fun getDefaultSuggestions(): List<String> {
        return when (AutoCorrectManager.getActiveLanguage(this)) {
            AutoCorrectManager.LANG_TA -> listOf("ஆமாம்", "நான்", "நீங்கள்", "என்ன", "இல்லை")
            AutoCorrectManager.LANG_TANG -> listOf("seri", "naan", "enna", "eppadi", "apdiya")
            else -> listOf("and", "I", "you", "the", "to")
        }
    }

    private fun resetDefaultSuggestions() {
        val defaults = getDefaultSuggestions()
        val views = listOf(suggestion1, suggestion2, suggestion3, suggestion4, suggestion5)
        for (i in views.indices) {
            val v = views[i]
            v?.text = defaults.getOrElse(i) { "" }
            v?.visibility = View.VISIBLE
            if (i == 2) {
                v?.setTypeface(null, android.graphics.Typeface.BOLD)
            } else {
                v?.setTypeface(null, android.graphics.Typeface.NORMAL)
            }
            val numTextColor = if (!currentTheme.photoPath.isNullOrBlank()) Color.WHITE else currentTheme.keyTextColor
            v?.setTextColor(numTextColor)
        }
    }

    private fun setupSuggestionBar() {
        val views = listOf(suggestion1, suggestion2, suggestion3, suggestion4, suggestion5)
        for (v in views) {
            v?.setOnClickListener {
                val text = v.text?.toString() ?: ""
                if (text.isNotEmpty()) {
                    playKeyFeedback()
                    handleSuggestionCommit(text)
                }
            }
        }

        resetDefaultSuggestions()
    }

    private fun handleSuggestionCommit(chosenText: String) {
        val ic = currentInputConnection ?: return
        val word = getCurrentWordFromInput()
        if (word.isNotEmpty()) {
            ic.deleteSurroundingText(word.length, 0)
        }
        ic.commitText(chosenText + " ", 1)
        AutoCorrectManager.recordWordSelection(this, chosenText, currentInputEditorInfo)
        updateSuggestions()

        if (shouldAutoCapitalize(currentInputEditorInfo)) {
            setShiftState(shifted = false, auto = false)
        }
    }

    private fun updateSuggestions() {
        val info = currentInputEditorInfo
        if (!com.cleverkeyboard.app.security.SensitiveFieldGuard.isSuggestionsAllowed(info)) {
            suggestionBarContainer?.visibility = View.GONE
            return
        }
        val ic = currentInputConnection ?: return
        val before = ic.getTextBeforeCursor(100, 0)?.toString() ?: ""
        val suggestions = AutoCorrectManager.getDynamicSuggestions(this, before)

        val views = listOf(suggestion1, suggestion2, suggestion3, suggestion4, suggestion5)
        for (i in views.indices) {
            val item = suggestions.getOrNull(i)
            val v = views[i]
            if (item != null) {
                v?.text = item.text
                v?.visibility = View.VISIBLE
                if (item.isPrimary || i == 2) {
                    v?.setTypeface(null, android.graphics.Typeface.BOLD)
                    if (item.isCorrection) {
                        v?.setTextColor(currentTheme.accentColor)
                    } else {
                        val numTextColor = if (!currentTheme.photoPath.isNullOrBlank()) Color.WHITE else currentTheme.keyTextColor
                        v?.setTextColor(numTextColor)
                    }
                } else {
                    v?.setTypeface(null, android.graphics.Typeface.NORMAL)
                    val numTextColor = if (!currentTheme.photoPath.isNullOrBlank()) Color.WHITE else currentTheme.keyTextColor
                    v?.setTextColor(numTextColor)
                }
            } else {
                v?.visibility = View.GONE
            }
        }
    }

    private fun getCurrentWordFromInput(): String {
        val before = currentInputConnection?.getTextBeforeCursor(50, 0)?.toString() ?: ""
        val match = Regex("[\\p{L}\\p{M}\\p{N}']+$").find(before)
        return match?.value ?: ""
    }

    private fun updateLanguageDisplay(showToast: Boolean = false) {
        val lang = AutoCorrectManager.getActiveLanguage(this)
        val label = when (lang) {
            AutoCorrectManager.LANG_EN -> "English (India)"
            AutoCorrectManager.LANG_TA -> "தமிழ்"
            AutoCorrectManager.LANG_TANG -> "Tanglish"
            else -> "English (India)"
        }
        keyboardView?.spaceLabel = label
        if (lang == AutoCorrectManager.LANG_TA) {
            keyboardView?.keyboard = if (isShifted) tamilShiftKeyboard else tamilKeyboard
        } else {
            keyboardView?.keyboard = qwertyKeyboard
        }
        resetDefaultSuggestions()
        keyboardView?.invalidate()
        if (showToast) {
            Toast.makeText(this, label, Toast.LENGTH_SHORT).show()
        }
    }

    // =========================================================================
    // Row 0: AI Toolbar Actions
    // =========================================================================

    private fun setupAiButtons() {
        btnAiTranslate?.setOnClickListener {
            showTranslateOptions()
        }

        btnAiSparkle?.setOnClickListener {
            showToneSelector("WRITE")
        }

        btnAiChat?.setOnClickListener {
            hideSubPanel()
            val text = getTargetText() ?: return@setOnClickListener
            val apiKey = checkOrPromptApiKey() ?: return@setOnClickListener
            executeAiTask(text, apiKey) { input, key ->
                aiService.fixGrammar(input, key)
            }
        }

        btnClipboard?.setOnClickListener {
            showClipboard()
        }

        btnAiReply?.setOnClickListener {
            showToneSelector("REPLY")
        }

        btnEmoji?.setOnClickListener {
            showEmojiKeyboard()
        }

        btnMoreDots?.setOnClickListener {
            showMoreMenu()
        }

        btnMicToolbar?.setOnClickListener {
            startVoiceTyping()
        }
        btnMicToolbar?.setOnLongClickListener {
            playKeyFeedback()
            showVoiceModePicker()
            true
        }

        btnCloseSubPanel?.setOnClickListener {
            hideSubPanel()
        }

        setupAiButtonTooltips()
    }

    private fun setupAiButtonTooltips() {
        val tooltips = listOf(
            btnAiTranslate to "Translate - Text ah English, Tamil, Hindi, Spanish ku translate pannum",
            btnAiSparkle to "AI Write - Tone change pannum: Professional, Casual, Polite, Funny",
            btnAiChat to "Spelling & Grammar ah correct pannum",
            btnClipboard to "Clipboard - Copied text history ah paste pannum",
            btnAiReply to "AI Reply - Instant smart replies automatically generate pannum",
            btnEmoji to "Emoji - Emoji keyboard open pannum",
            btnMoreDots to "More - Settings, Themes, Clipboard & Customization open pannum",
            btnMicToolbar to "Voice - Tanglish speech ah English & Tamil text ah convert pannum"
        )

        for ((btn, tooltip) in tooltips) {
            btn?.setOnLongClickListener {
                playKeyFeedback()
                showStatusBanner("ℹ️ $tooltip", 3500L)
                Toast.makeText(this@MyKeyboardService, tooltip, Toast.LENGTH_SHORT).show()
                true
            }
        }
    }

    private val hideLoadingRunnable = Runnable {
        aiLoadingContainer?.visibility = View.GONE
    }

    private fun showStatusBanner(text: String, autoHideMs: Long = 2000L) {
        mainHandler.post {
            aiLoadingContainer?.visibility = View.VISIBLE
            aiLoadingText?.text = text
            mainHandler.removeCallbacks(hideLoadingRunnable)
            if (autoHideMs > 0) {
                mainHandler.postDelayed(hideLoadingRunnable, autoHideMs)
            }
        }
    }

    private fun getTargetText(): String? {
        val ic = currentInputConnection
        if (ic == null) {
            showStatusBanner("⚠️ Input connection lost", 2000L)
            return null
        }

        val selected = ic.getSelectedText(0)?.toString()
        if (!selected.isNullOrBlank()) {
            return selected.trim()
        }

        val before = ic.getTextBeforeCursor(1000, 0)?.toString()?.trim()
        if (!before.isNullOrBlank()) {
            val lastLine = before.lines().lastOrNull { it.isNotBlank() }?.trim()
            if (!lastLine.isNullOrBlank()) {
                return lastLine
            }
            return before
        }

        try {
            val extracted = ic.getExtractedText(ExtractedTextRequest(), 0)
            val fullText = extracted?.text?.toString()?.trim()
            if (!fullText.isNullOrBlank()) {
                val lastLine = fullText.lines().lastOrNull { it.isNotBlank() }?.trim()
                return lastLine ?: fullText
            }
        } catch (_: Exception) {}

        Toast.makeText(this, getString(R.string.select_text_prompt), Toast.LENGTH_SHORT).show()
        showStatusBanner("⚠️ Please select or type text first", 2500L)
        return null
    }

    private fun executeAiTask(
        inputText: String,
        apiKey: String,
        task: suspend (input: String, key: String) -> Result<String>
    ) {
        showStatusBanner("✨ Generating with Gemini...", 0L)

        serviceScope.launch(Dispatchers.IO) {
            try {
                val callResult = task(inputText, apiKey)
                val result = callResult.getOrNull() ?: ""

                withContext(Dispatchers.Main) {
                    mainHandler.post {
                        mainHandler.removeCallbacks(hideLoadingRunnable)
                        aiLoadingContainer?.visibility = View.GONE

                        if (result.isNotBlank()) {
                            val ic = currentInputConnection
                            if (ic != null) {
                                ic.beginBatchEdit()
                                try {
                                    val sel = ic.getSelectedText(0)?.toString()
                                    if (!sel.isNullOrEmpty()) {
                                        ic.commitText("", 1)
                                    } else {
                                        val before = ic.getTextBeforeCursor(1000, 0)?.toString() ?: ""
                                        if (before.isNotEmpty()) {
                                            ic.deleteSurroundingText(before.length, 0)
                                        }
                                    }
                                    ic.commitText(result, 1)
                                    ic.finishComposingText()
                                } finally {
                                    ic.endBatchEdit()
                                }
                                Toast.makeText(this@MyKeyboardService, "Replaced with AI result", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@MyKeyboardService, "AI Failed, Try Again", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "AI Task failed", e)
                withContext(Dispatchers.Main) {
                    mainHandler.post {
                        mainHandler.removeCallbacks(hideLoadingRunnable)
                        aiLoadingContainer?.visibility = View.GONE
                        Toast.makeText(this@MyKeyboardService, "AI Failed, Try Again", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showToneOptions() {
        showToneSelector("WRITE")
    }

    private fun showToneSelector(mode: String) {
        val text = getTargetText() ?: return
        val apiKey = checkOrPromptApiKey() ?: return

        currentAiToneMode = mode
        currentAiToneInputText = text

        val tones = listOf(
            "Normal",
            "Flirty",
            "Funny",
            "Cute",
            "Cool",
            "Formal",
            "Persuasive",
            "Empathetic",
            "Sarcastic",
            "Humorous",
            "Mysterious",
            "Motivational",
            "Assertive"
        )
        val prefs = getSharedPreferences("clever_keyboard_prefs", Context.MODE_PRIVATE)
        var selectedTone = prefs.getString("saved_ai_reply_tone", "Normal") ?: "Normal"
        if (!tones.any { it.equals(selectedTone, ignoreCase = true) }) {
            selectedTone = "Normal"
        }

        renderTonePills(tones, selectedTone, apiKey)
        subOptionsPanel?.visibility = View.VISIBLE
        aiResultPanel?.visibility = View.VISIBLE

        executeToneAiTask(currentAiToneInputText, selectedTone, currentAiToneMode, apiKey)
    }

    private fun renderTonePills(tones: List<String>, selectedTone: String, apiKey: String) {
        val container = subOptionsContainer ?: return
        container.removeAllViews()
        val density = resources.displayMetrics.density

        val label = TextView(this).apply {
            text = if (currentAiToneMode == "REPLY") "Tone:" else "Tone:"
            textSize = 11f
            setTextColor(Color.parseColor("#AAAAAA"))
            setPadding((4 * density).toInt(), 0, (6 * density).toInt(), 0)
        }
        container.addView(label)

        for (tone in tones) {
            val isSelected = tone.equals(selectedTone, ignoreCase = true)
            val pill = Button(this).apply {
                text = tone
                textSize = 11f
                isAllCaps = false
                setTextColor(Color.WHITE)
                val bg = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 14f * density
                    if (isSelected) {
                        val pillColor = when (tone.lowercase()) {
                            "flirty" -> Color.parseColor("#E91E63") // Pink
                            "funny" -> Color.parseColor("#FB8C00") // Amber
                            "cute" -> Color.parseColor("#F06292") // Soft Rose
                            "cool" -> Color.parseColor("#00ACC1") // Cyan
                            "formal" -> Color.parseColor("#3949AB") // Indigo/Navy
                            "persuasive" -> Color.parseColor("#8E24AA") // Purple
                            "empathetic" -> Color.parseColor("#00897B") // Teal
                            "sarcastic" -> Color.parseColor("#D81B60") // Ruby Magenta
                            "humorous" -> Color.parseColor("#F57C00") // Warm Orange
                            "mysterious" -> Color.parseColor("#5E35B1") // Deep Violet
                            "motivational" -> Color.parseColor("#43A047") // Emerald Green
                            "assertive" -> Color.parseColor("#E53935") // Crimson Red
                            else -> Color.parseColor("#5B5BF0") // Accent Blue for Normal
                        }
                        setColor(pillColor)
                    } else {
                        setColor(Color.parseColor("#2C2C32"))
                    }
                }
                background = bg
                setPadding((12 * density).toInt(), 0, (12 * density).toInt(), 0)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    (28 * density).toInt()
                ).apply {
                    marginEnd = (6 * density).toInt()
                }
                layoutParams = params

                setOnClickListener {
                    playKeyFeedback()
                    getSharedPreferences("clever_keyboard_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .putString("saved_ai_reply_tone", tone)
                        .apply()

                    renderTonePills(tones, tone, apiKey)
                    executeToneAiTask(currentAiToneInputText, tone, currentAiToneMode, apiKey)
                }
            }
            container.addView(pill)
        }
    }

    private fun executeToneAiTask(inputText: String, tone: String, mode: String, apiKey: String) {
        val actionDesc = if (mode == "REPLY") "reply" else "rewrite"
        aiResultText?.text = "✨ Generating $tone $actionDesc..."
        btnAiCopy?.isEnabled = false
        btnAiSend?.isEnabled = false
        btnAiCopy?.alpha = 0.5f
        btnAiSend?.alpha = 0.5f

        serviceScope.launch(Dispatchers.IO) {
            val callResult = if (mode == "REPLY") {
                aiService.generateReplyWithTone(inputText, tone, apiKey)
            } else {
                aiService.rewriteWithTone(inputText, tone, apiKey)
            }
            val result = callResult.getOrNull() ?: ""

            withContext(Dispatchers.Main) {
                if (result.isNotBlank()) {
                    currentAiToneResult = result
                    aiResultText?.text = result
                    btnAiCopy?.isEnabled = true
                    btnAiSend?.isEnabled = true
                    btnAiCopy?.alpha = 1.0f
                    btnAiSend?.alpha = 1.0f
                } else {
                    val err = callResult.exceptionOrNull()?.message ?: "AI generation failed"
                    aiResultText?.text = "⚠️ $err. Tap tone to retry."
                    btnAiCopy?.isEnabled = false
                    btnAiSend?.isEnabled = false
                }
            }
        }
    }

    private fun commitAiToneResult() {
        if (currentAiToneResult.isBlank()) return
        val ic = currentInputConnection ?: return
        ic.beginBatchEdit()
        try {
            val sel = ic.getSelectedText(0)?.toString()
            if (!sel.isNullOrEmpty()) {
                ic.commitText("", 1)
            } else if (currentAiToneMode == "WRITE") {
                val before = ic.getTextBeforeCursor(1000, 0)?.toString() ?: ""
                if (before.isNotEmpty()) {
                    ic.deleteSurroundingText(before.length, 0)
                }
            }
            ic.commitText(currentAiToneResult, 1)
            ic.finishComposingText()
        } finally {
            ic.endBatchEdit()
        }
        Toast.makeText(this, "Sent!", Toast.LENGTH_SHORT).show()
        hideSubPanel()
    }

    private fun showTranslateOptions() {
        val languages = listOf(
            Pair("⇄ En → Tamil", "Tamil"),
            Pair("⇄ Ta → English", "English"),
            Pair("En → Tanglish", "Tanglish"),
            Pair("En → Hindi", "Hindi"),
            Pair("En → Telugu", "Telugu"),
            Pair("En → Kannada", "Kannada"),
            Pair("En → Malayalam", "Malayalam"),
            Pair("En → Bengali", "Bengali"),
            Pair("En → Marathi", "Marathi"),
            Pair("En → Gujarati", "Gujarati"),
            Pair("En → Punjabi", "Punjabi"),
            Pair("En → Urdu", "Urdu"),
            Pair("En → Spanish", "Spanish"),
            Pair("En → French", "French"),
            Pair("En → German", "German"),
            Pair("En → Portuguese", "Portuguese"),
            Pair("En → Italian", "Italian"),
            Pair("En → Japanese", "Japanese"),
            Pair("En → Korean", "Korean"),
            Pair("En → Chinese (Simp)", "Chinese Simplified"),
            Pair("En → Chinese (Trad)", "Chinese Traditional"),
            Pair("En → Arabic", "Arabic"),
            Pair("En → Russian", "Russian"),
            Pair("En → Indonesian", "Indonesian"),
            Pair("En → Thai", "Thai"),
            Pair("En → Vietnamese", "Vietnamese"),
            Pair("En → Turkish", "Turkish"),
            Pair("En → Dutch", "Dutch"),
            Pair("En → Polish", "Polish"),
            Pair("En → Hebrew", "Hebrew"),
            Pair("En → Swahili", "Swahili"),
            Pair("En → Latin", "Latin")
        )
        displaySubOptions("Translate (32 Languages):", languages) { targetLang ->
            val text = getTargetText() ?: return@displaySubOptions
            val apiKey = checkOrPromptApiKey() ?: return@displaySubOptions
            executeAiTask(text, apiKey) { input, key ->
                aiService.translate(input, targetLang, key)
            }
        }
    }

    private fun processSmartReply() {
        val text = getTargetText() ?: return
        val apiKey = checkOrPromptApiKey() ?: return

        showStatusBanner("✨ Generating smart replies...", 0L)

        serviceScope.launch(Dispatchers.IO) {
            try {
                val result = aiService.smartReply(text, apiKey)
                val replies = result.getOrNull() ?: emptyList()

                withContext(Dispatchers.Main) {
                    mainHandler.removeCallbacks(hideLoadingRunnable)
                    aiLoadingContainer?.visibility = View.GONE

                    if (replies.isNotEmpty()) {
                        val pairs = replies.map { Pair(it, it) }
                        displaySubOptions("Smart Replies:", pairs) { chosenReply ->
                            val ic = currentInputConnection
                            if (ic != null) {
                                ic.beginBatchEdit()
                                try {
                                    val sel = ic.getSelectedText(0)?.toString()
                                    if (!sel.isNullOrEmpty()) {
                                        ic.commitText("", 1)
                                    }
                                    ic.commitText(chosenReply, 1)
                                    ic.finishComposingText()
                                } finally {
                                    ic.endBatchEdit()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this@MyKeyboardService, "AI Failed, Try Again", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mainHandler.removeCallbacks(hideLoadingRunnable)
                    aiLoadingContainer?.visibility = View.GONE
                    Toast.makeText(this@MyKeyboardService, "AI Failed, Try Again", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLengthOptions() {
        val options = listOf(
            Pair("Make Shorter", "short"),
            Pair("Make Longer", "long")
        )
        displaySubOptions("Length:", options) { mode ->
            val text = getTargetText() ?: return@displaySubOptions
            val apiKey = checkOrPromptApiKey() ?: return@displaySubOptions
            executeAiTask(text, apiKey) { input, key ->
                aiService.changeLength(input, mode == "long", key)
            }
        }
    }

    private var selectedVoiceLanguage: String? = null

    private fun showVoiceModePicker() {
        val options = listOf(
            Pair("Auto Detect (From Keyboard)", null),
            Pair("English (India)", "en-IN"),
            Pair("English (US)", "en-US"),
            Pair("Tamil (தமிழ்)", "ta-IN"),
            Pair("Tanglish (AI Convert)", "ta-IN")
        )
        displaySubOptions("Voice Mode:", options) { lang ->
            selectedVoiceLanguage = lang
            val label = when (lang) {
                "en-IN" -> "English (India)"
                "en-US" -> "English (US)"
                "ta-IN" -> "Tamil"
                else -> "Auto Detect"
            }
            Toast.makeText(this, "Voice Mode: $label", Toast.LENGTH_SHORT).show()
            startVoiceTyping()
        }
    }

    private fun startVoiceTyping() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(this, VoicePermissionActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            showStatusBanner("🎙️ Grant microphone permission to use voice typing", 3000L)
            return
        }

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            showStatusBanner("⚠️ Speech recognition not available", 2500L)
            Toast.makeText(this, "Speech recognition not available", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val activeLang = AutoCorrectManager.getActiveLanguage(this)
            val recognitionLang = selectedVoiceLanguage ?: when (activeLang) {
                AutoCorrectManager.LANG_TA -> "ta-IN"
                AutoCorrectManager.LANG_TANG -> "ta-IN"
                else -> "en-IN"
            }

            val promptTitle = when (recognitionLang) {
                "ta-IN" -> "🎙️ Listening Tamil... பேசுங்கள்"
                "en-US" -> "🎙️ Listening English (US)..."
                else -> "🎙️ Listening... Speak English / Tanglish"
            }

            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    showStatusBanner(promptTitle, 0L)
                }
                override fun onBeginningOfSpeech() {
                    showStatusBanner("🎙️ Listening...", 0L)
                }
                override fun onRmsChanged(rmsdB: Float) {
                    val level = (rmsdB / 2f).toInt().coerceIn(1, 6)
                    val wave = when (level) {
                        1 -> " ▂ "
                        2 -> " ▂▃ "
                        3 -> " ▂▃▅ "
                        4 -> " ▂▃▅▆ "
                        5 -> " ▂▃▅▆▇ "
                        else -> " ▂▃▅▆▇█ "
                    }
                    showStatusBanner("🎙️ Listening $wave", 0L)
                }
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    showStatusBanner("⏳ Processing speech...", 0L)
                }
                override fun onError(error: Int) {
                    val msg = when (error) {
                        SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
                        SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network error"
                        else -> "Voice input error ($error)"
                    }
                    showStatusBanner("⚠️ $msg", 2500L)
                }
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val spokenText = matches?.firstOrNull()?.trim()
                    if (spokenText.isNullOrBlank()) {
                        showStatusBanner("⚠️ No speech recognized", 2000L)
                        return
                    }
                    processVoiceResult(spokenText)
                }
                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val live = matches?.firstOrNull()?.trim()
                    if (!live.isNullOrBlank()) {
                        showStatusBanner("🎙️ $live", 0L)
                    }
                }
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, recognitionLang)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }
            speechRecognizer?.startListening(intent)
            showStatusBanner(promptTitle, 0L)
        } catch (e: Exception) {
            showStatusBanner("⚠️ Failed to start mic: ${e.message}", 2500L)
        }
    }

    private fun processVoiceResult(spokenText: String) {
        val hasTamil = spokenText.any { it in '\u0B80'..'\u0BFF' }
        if (hasTamil) {
            currentInputConnection?.commitText(spokenText + " ", 1)
            showStatusBanner("✓ Tamil text inserted", 1500L)
            return
        }

        val apiKey = ApiKeyManager.getApiKey(this)
        if (apiKey.isNullOrBlank()) {
            currentInputConnection?.commitText(spokenText + " ", 1)
            showStatusBanner("✓ Text inserted (Add Gemini Key for Tanglish conversion)", 2500L)
            return
        }

        showStatusBanner("✨ Converting Tanglish speech...", 0L)

        serviceScope.launch(Dispatchers.IO) {
            val result = aiService.convertTanglish(spokenText, apiKey)
            val conversion = result.getOrNull()
            val english = conversion?.english ?: ""
            val tamil = conversion?.tamil ?: ""

            withContext(Dispatchers.Main) {
                mainHandler.removeCallbacks(hideLoadingRunnable)
                aiLoadingContainer?.visibility = View.GONE

                val options = mutableListOf<Pair<String, String>>()
                if (english.isNotBlank()) options.add(Pair("English: $english", english))
                if (tamil.isNotBlank()) options.add(Pair("Tamil: $tamil", tamil))

                if (options.isNotEmpty()) {
                    displaySubOptions("Voice Result - Tap to insert:", options) { chosenText ->
                        currentInputConnection?.commitText(chosenText + " ", 1)
                    }
                } else {
                    currentInputConnection?.commitText(spokenText + " ", 1)
                }
            }
        }
    }

    private fun <T> displaySubOptions(
        header: String,
        items: List<Pair<String, T>>,
        onSelected: (T) -> Unit
    ) {
        val container = subOptionsContainer ?: return
        container.removeAllViews()

        val label = TextView(this).apply {
            text = header
            textSize = 12f
            setTextColor(Color.parseColor("#AAAAAA"))
            setPadding(8, 0, 12, 0)
        }
        container.addView(label)

        for ((title, value) in items) {
            val chip = Button(this).apply {
                text = title
                textSize = 12f
                setTextColor(Color.WHITE)
                background = ContextCompat.getDrawable(this@MyKeyboardService, R.drawable.key_background)
                setPadding(24, 0, 24, 0)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 12
                }
                layoutParams = params

                setOnClickListener {
                    hideSubPanel()
                    onSelected(value)
                }
            }
            container.addView(chip)
        }

        aiResultPanel?.visibility = View.GONE
        subOptionsPanel?.visibility = View.VISIBLE
    }

    private fun hideSubPanel() {
        subOptionsPanel?.visibility = View.GONE
        aiResultPanel?.visibility = View.GONE
        subOptionsContainer?.removeAllViews()
        currentAiToneResult = ""
    }

    private fun checkOrPromptApiKey(): String? {
        val key = ApiKeyManager.getApiKey(this)
        if (key.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.no_api_key_error), Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
            return null
        }
        return key
    }

    // =========================================================================
    // Keyboard Event Callbacks
    // =========================================================================

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val ic = currentInputConnection ?: return
        playKeyFeedback()

        // BUG 4 FIX: When in Emoji Search mode, route typing into emojiSearchInput
        if (isEmojiSearchMode) {
            when (primaryCode) {
                // Delete Key: delete last char in search input without abruptly exiting search
                Keyboard.KEYCODE_DELETE, -5 -> {
                    val text = emojiSearchInput?.text?.toString() ?: ""
                    if (text.isNotEmpty()) {
                        emojiSearchInput?.setText(text.substring(0, text.length - 1))
                        emojiSearchInput?.setSelection(emojiSearchInput?.text?.length ?: 0)
                    }
                    return
                }

                // Mode Change Key (?123 / ABC)
                Keyboard.KEYCODE_MODE_CHANGE, -2 -> {
                    deactivateEmojiSearchMode()
                    showQwertyKeyboard()
                    return
                }

                // Shift Key
                Keyboard.KEYCODE_SHIFT, -1 -> {
                    handleShift()
                    return
                }

                // Space Key
                32 -> {
                    emojiSearchInput?.append(" ")
                    emojiSearchInput?.setSelection(emojiSearchInput?.text?.length ?: 0)
                    return
                }

                // Enter Key / Done: close search and return to QWERTY
                Keyboard.KEYCODE_DONE, -4 -> {
                    deactivateEmojiSearchMode()
                    showQwertyKeyboard()
                    return
                }

                // Normal character / letter
                else -> {
                    if (primaryCode > 0) {
                        val char = if (isShifted) primaryCode.toChar().uppercaseChar() else primaryCode.toChar().lowercaseChar()
                        emojiSearchInput?.append(char.toString())
                        emojiSearchInput?.setSelection(emojiSearchInput?.text?.length ?: 0)
                    }
                    return
                }
            }
        }

        when (primaryCode) {
            // Delete Key
            Keyboard.KEYCODE_DELETE, -5 -> {
                handleDelete()
            }

            // Shift Key
            Keyboard.KEYCODE_SHIFT, -1 -> {
                handleShift()
            }

            // Enter / Search Key (-4)
            Keyboard.KEYCODE_DONE, -4 -> {
                handleEnter()
            }

            // Mode Change Key (?123 / ABC)
            Keyboard.KEYCODE_MODE_CHANGE, -2 -> {
                resetDefaultSuggestions()
                toggleSymbolsMode()
            }

            // Additional Symbols Shift (=< / ?123)
            CODE_SYMBOL_SHIFT -> {
                resetDefaultSuggestions()
                toggleSymbolsShiftMode()
            }

            // Globe Key (-100): Cycle Language
            CODE_GLOBE -> {
                val current = AutoCorrectManager.getActiveLanguage(this)
                val next = when (current) {
                    AutoCorrectManager.LANG_EN -> AutoCorrectManager.LANG_TA
                    AutoCorrectManager.LANG_TA -> AutoCorrectManager.LANG_TANG
                    else -> AutoCorrectManager.LANG_EN
                }
                AutoCorrectManager.setActiveLanguage(this, next)
                updateLanguageDisplay(showToast = true)
            }

            // Voice Typing Key (-10)
            CODE_MIC -> {
                resetDefaultSuggestions()
                startVoiceTyping()
            }

            // NumPad Key (-200) from Symbols page
            CODE_NUMPAD -> {
                resetDefaultSuggestions()
                showNumPad()
            }

            // Spacebar (32): Auto-correct typo + Auto Capitalization check + Double-space period
            32 -> {
                val now = System.currentTimeMillis()
                val theme = ThemeManager.getTheme(this)

                // Double space to period (". ")
                if (theme.isDoubleSpacePeriodEnabled && (now - lastSpacePressTime < 400)) {
                    val before = ic.getTextBeforeCursor(2, 0)?.toString() ?: ""
                    if (before.endsWith(" ") && !before.endsWith(". ") && !before.endsWith("? ") && !before.endsWith("! ")) {
                        ic.deleteSurroundingText(1, 0)
                        ic.commitText(". ", 1)
                        lastSpacePressTime = 0L
                        if (shouldAutoCapitalize(currentInputEditorInfo)) {
                            setShiftState(shifted = true, auto = true)
                        }
                        return
                    }
                }
                lastSpacePressTime = now

                val word = getCurrentWordFromInput()
                val allowed = AutoCorrectManager.isAutoCorrectEnabled(this) &&
                        AutoCorrectManager.isAutoCorrectAllowed(currentInputEditorInfo)
                val correction = if (allowed && word.isNotEmpty()) {
                    AutoCorrectManager.getCorrection(this, word)
                } else null

                if (correction != null && !correction.equals(word, ignoreCase = false)) {
                    lastAutocorrectOriginal = word
                    lastAutocorrectReplacement = correction
                    ic.deleteSurroundingText(word.length, 0)
                    ic.commitText(correction + " ", 1)
                } else {
                    lastAutocorrectOriginal = null
                    lastAutocorrectReplacement = null
                    ic.commitText(" ", 1)
                }

                resetDefaultSuggestions()

                // FIX 1: Auto-capitalize after ". ", "? ", "! "
                if (shouldAutoCapitalize(currentInputEditorInfo)) {
                    val before = ic.getTextBeforeCursor(20, 0)?.toString() ?: ""
                    if (Regex("[.?!]\\s+$").containsMatchIn(before)) {
                        setShiftState(shifted = true, auto = true)
                    } else {
                        setShiftState(shifted = false, auto = false)
                    }
                }
            }

            // Normal Character
            else -> {
                lastAutocorrectOriginal = null
                lastAutocorrectReplacement = null
                val code = primaryCode
                val char = code.toChar()
                val textToCommit = if (isShifted) {
                    char.uppercaseChar().toString()
                } else {
                    char.lowercaseChar().toString()
                }

                ic.commitText(textToCommit, 1)

                // Gboard Rule: If Shift is active (one-shot or auto-capitalized) and not locked, return to lowercase
                if (isShifted && !isShiftLocked) {
                    setShiftState(shifted = false, auto = false)
                }

                if (char.isLetter() || Character.isLetter(code) || code > 127) {
                    updateSuggestions()
                } else {
                    resetDefaultSuggestions()
                }
            }
        }
    }

    private fun toggleSymbolsMode() {
        val theme = ThemeManager.getTheme(this)
        val activeLang = AutoCorrectManager.getActiveLanguage(this)
        val defaultTextKeyboard = if (activeLang == AutoCorrectManager.LANG_TA) tamilKeyboard else qwertyKeyboard

        if (keyboardView?.keyboard == symbolsKeyboard || keyboardView?.keyboard == symbolsShiftKeyboard) {
            keyboardView?.keyboard = defaultTextKeyboard
            numberRowContainer?.visibility = if (theme.showNumberRow) View.VISIBLE else View.GONE
        } else {
            keyboardView?.keyboard = symbolsKeyboard
            numberRowContainer?.visibility = View.GONE
        }
        isShifted = false
        isShiftLocked = false
        keyboardView?.isShifted = false
        keyboardView?.isShiftLocked = false
        keyboardView?.invalidate()
    }

    private fun toggleSymbolsShiftMode() {
        if (keyboardView?.keyboard == symbolsKeyboard) {
            keyboardView?.keyboard = symbolsShiftKeyboard
        } else {
            keyboardView?.keyboard = symbolsKeyboard
        }
        numberRowContainer?.visibility = View.GONE
        keyboardView?.invalidate()
    }

    private fun playKeyFeedback() {
        if (ThemeManager.isSoundEnabled(this)) {
            try {
                audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD)
            } catch (_: Exception) {}
        }

        if (ThemeManager.isVibrationEnabled(this)) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(15)
                }
            } catch (_: Exception) {}
        }
    }

    // =========================================================================
    // Panel Navigation & Specialized Mode Panels
    // =========================================================================

    private fun showQwertyKeyboard() {
        deactivateEmojiSearchMode()
        numPadContainer?.visibility = View.GONE
        emojiContainer?.visibility = View.GONE
        clipboardContainer?.visibility = View.GONE
        moreMenuContainer?.visibility = View.GONE
        keyboardView?.visibility = View.VISIBLE
        val activeLang = AutoCorrectManager.getActiveLanguage(this)
        val defaultTextKeyboard = if (activeLang == AutoCorrectManager.LANG_TA) tamilKeyboard else qwertyKeyboard
        keyboardView?.keyboard = defaultTextKeyboard
        val theme = ThemeManager.getTheme(this)
        numberRowContainer?.visibility = if (theme.showNumberRow) View.VISIBLE else View.GONE
        suggestionBarContainer?.visibility = View.VISIBLE
    }

    private fun showNumPad() {
        deactivateEmojiSearchMode()
        emojiContainer?.visibility = View.GONE
        clipboardContainer?.visibility = View.GONE
        moreMenuContainer?.visibility = View.GONE
        keyboardView?.visibility = View.GONE
        numberRowContainer?.visibility = View.GONE
        numPadContainer?.visibility = View.VISIBLE
    }

    private fun showEmojiKeyboard() {
        deactivateEmojiSearchMode()
        numPadContainer?.visibility = View.GONE
        clipboardContainer?.visibility = View.GONE
        moreMenuContainer?.visibility = View.GONE
        keyboardView?.visibility = View.GONE
        numberRowContainer?.visibility = View.GONE
        suggestionBarContainer?.visibility = View.GONE
        emojiContainer?.visibility = View.VISIBLE

        val density = resources.displayMetrics.density
        val lp = emojiContainer?.layoutParams
        if (lp != null) {
            lp.height = (310 * density).toInt()
            emojiContainer?.layoutParams = lp
        }
        emojiBottomBar?.visibility = View.VISIBLE
        emojiCategoriesScroll?.visibility = View.VISIBLE
        emojiCategoriesContainer?.visibility = View.VISIBLE

        setupEmojiCategories()
        loadEmojiGrid(currentEmojiCategory)
    }

    private fun activateEmojiSearchMode() {
        if (isEmojiSearchMode) return
        isEmojiSearchMode = true

        val density = resources.displayMetrics.density

        // Hide number row and suggestions
        numberRowContainer?.visibility = View.GONE
        suggestionBarContainer?.visibility = View.GONE

        // Make emoji container compact so QWERTY keyboard fits below
        val lp = emojiContainer?.layoutParams
        if (lp != null) {
            lp.height = (115 * density).toInt()
            emojiContainer?.layoutParams = lp
        }

        emojiBottomBar?.visibility = View.GONE

        // Show QWERTY keyboard view below emoji search
        keyboardView?.visibility = View.VISIBLE
        keyboardView?.keyboard = qwertyKeyboard
        keyboardView?.invalidate()

        emojiSearchInput?.isFocusableInTouchMode = true
        emojiSearchInput?.requestFocus()
    }

    private fun deactivateEmojiSearchMode() {
        if (!isEmojiSearchMode) return
        isEmojiSearchMode = false

        val density = resources.displayMetrics.density

        val lp = emojiContainer?.layoutParams
        if (lp != null) {
            lp.height = (250 * density).toInt()
            emojiContainer?.layoutParams = lp
        }

        emojiBottomBar?.visibility = View.VISIBLE
        emojiCategoriesScroll?.visibility = View.VISIBLE
        emojiCategoriesContainer?.visibility = View.VISIBLE

        emojiSearchInput?.text?.clear()
        emojiSearchInput?.clearFocus()
    }

    private fun showClipboard() {
        deactivateEmojiSearchMode()
        numPadContainer?.visibility = View.GONE
        emojiContainer?.visibility = View.GONE
        moreMenuContainer?.visibility = View.GONE
        keyboardView?.visibility = View.GONE
        numberRowContainer?.visibility = View.GONE
        clipboardContainer?.visibility = View.VISIBLE
        populateClipboardList()
    }

    private fun showMoreMenu() {
        deactivateEmojiSearchMode()
        numPadContainer?.visibility = View.GONE
        emojiContainer?.visibility = View.GONE
        clipboardContainer?.visibility = View.GONE
        keyboardView?.visibility = View.GONE
        numberRowContainer?.visibility = View.GONE
        moreMenuContainer?.visibility = View.VISIBLE
        populateMoreMenu()
    }

    // -------------------------------------------------------------------------
    // Helper Delete and Enter
    // -------------------------------------------------------------------------

    private fun handleDelete() {
        val ic = currentInputConnection ?: return
        val selected = ic.getSelectedText(0)

        // Undo Autocorrect (Gboard behavior):
        // If user just pressed space and a correction was applied, tapping backspace reverts to original typed word!
        val orig = lastAutocorrectOriginal
        val repl = lastAutocorrectReplacement
        if (selected.isNullOrEmpty() && orig != null && repl != null) {
            val before = ic.getTextBeforeCursor(repl.length + 1, 0)?.toString() ?: ""
            if (before == "$repl " || before == repl) {
                ic.deleteSurroundingText(before.length, 0)
                ic.commitText(orig, 1)
                lastAutocorrectOriginal = null
                lastAutocorrectReplacement = null
                updateSuggestions()
                return
            }
        }
        lastAutocorrectOriginal = null
        lastAutocorrectReplacement = null

        if (selected.isNullOrEmpty()) {
            ic.deleteSurroundingText(1, 0)
        } else {
            ic.commitText("", 1)
        }
        updateSuggestions()

        if (!isShiftLocked && shouldAutoCapitalize(currentInputEditorInfo)) {
            val before = ic.getTextBeforeCursor(20, 0)?.toString() ?: ""
            val isSentenceStart = before.isBlank() || before.endsWith("\n") || Regex("[.?!]\\s+$").containsMatchIn(before)
            if (isSentenceStart) {
                setShiftState(shifted = true, auto = true)
            } else if (isAutoCapitalized) {
                setShiftState(shifted = false, auto = false)
            }
        }
    }

    private fun handleEnter() {
        resetDefaultSuggestions()
        val ic = currentInputConnection ?: return
        val info = currentInputEditorInfo
        val isMultiLine = (info?.inputType?.and(EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE) ?: 0) != 0

        if (isMultiLine) {
            // BUG 3 FIX: In multi-line fields (e.g. WhatsApp), Enter must insert newline
            ic.commitText("\n", 1)
        } else {
            val action = info?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: EditorInfo.IME_ACTION_NONE
            if (action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
                val handled = sendDefaultEditorAction(true)
                if (!handled) {
                    if (!ic.performEditorAction(action)) {
                        sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER)
                    }
                }
            } else {
                sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER)
            }
        }

        if (shouldAutoCapitalize(info)) {
            setShiftState(shifted = true, auto = true)
        }
    }

    // -------------------------------------------------------------------------
    // NumPad Implementation
    // -------------------------------------------------------------------------

    private fun setupNumPad(view: View) {
        val numKeys = listOf(
            R.id.num_key_1 to "1",
            R.id.num_key_2 to "2",
            R.id.num_key_3 to "3",
            R.id.num_key_4 to "4",
            R.id.num_key_5 to "5",
            R.id.num_key_6 to "6",
            R.id.num_key_7 to "7",
            R.id.num_key_8 to "8",
            R.id.num_key_9 to "9",
            R.id.num_key_0 to "0",
            R.id.num_key_plus to "+"
        )

        for ((id, char) in numKeys) {
            val btn = view.findViewById<Button>(id) ?: continue
            btn.setOnClickListener {
                playKeyFeedback()
                currentInputConnection?.commitText(char, 1)
                updateSuggestions()
            }
            if (char == "0") {
                btn.setOnLongClickListener {
                    playKeyFeedback()
                    currentInputConnection?.commitText("+", 1)
                    true
                }
            } else if (char == "1") {
                btn.setOnLongClickListener {
                    playKeyFeedback()
                    currentInputConnection?.commitText(".", 1)
                    true
                }
            }
        }

        view.findViewById<View>(R.id.num_key_backspace)?.setOnClickListener {
            playKeyFeedback()
            handleDelete()
        }

        view.findViewById<Button>(R.id.num_key_abc)?.setOnClickListener {
            playKeyFeedback()
            showQwertyKeyboard()
        }

        view.findViewById<Button>(R.id.num_key_space)?.setOnClickListener {
            playKeyFeedback()
            currentInputConnection?.commitText(" ", 1)
        }

        view.findViewById<View>(R.id.num_key_enter)?.setOnClickListener {
            playKeyFeedback()
            handleEnter()
        }
    }

    // -------------------------------------------------------------------------
    // Emoji Keyboard Implementation
    // -------------------------------------------------------------------------

    private fun setupEmojiKeyboard(view: View) {
        emojiGrid = view.findViewById(R.id.emoji_grid)
        emojiCategoriesContainer = view.findViewById(R.id.emoji_categories_container)
        emojiCategoriesScroll = view.findViewById(R.id.emoji_categories_scroll)
        emojiSearchInput = view.findViewById(R.id.emoji_search_input)
        emojiBottomBar = view.findViewById(R.id.emoji_bottom_bar)

        val clearSearchBtn = view.findViewById<ImageView>(R.id.btn_emoji_clear_search)
        clearSearchBtn?.setOnClickListener {
            playKeyFeedback()
            emojiSearchInput?.text?.clear()
            loadEmojiGrid(currentEmojiCategory)
        }

        emojiSearchInput?.setOnClickListener {
            activateEmojiSearchMode()
        }

        emojiSearchInput?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                activateEmojiSearchMode()
            }
        }

        emojiSearchInput?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim() ?: ""
                clearSearchBtn?.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
                if (query.isNotEmpty()) {
                    emojiCategoriesScroll?.visibility = View.GONE
                    val searchResults = EmojiManager.searchEmojis(query)
                    renderEmojisInGrid(searchResults)
                } else {
                    if (!isEmojiSearchMode) {
                        emojiCategoriesScroll?.visibility = View.VISIBLE
                    }
                    loadEmojiGrid(currentEmojiCategory)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        view.findViewById<Button>(R.id.btn_emoji_back_abc)?.setOnClickListener {
            playKeyFeedback()
            showQwertyKeyboard()
        }

        view.findViewById<Button>(R.id.btn_emoji_space)?.setOnClickListener {
            playKeyFeedback()
            currentInputConnection?.commitText(" ", 1)
        }

        view.findViewById<View>(R.id.btn_emoji_backspace)?.setOnClickListener {
            playKeyFeedback()
            handleDelete()
        }
    }

    private fun setupEmojiCategories() {
        val container = emojiCategoriesContainer ?: return
        container.removeAllViews()
        val density = resources.displayMetrics.density

        EmojiManager.CATEGORIES.forEachIndexed { index, catName ->
            val tab = TextView(this).apply {
                text = catName
                textSize = 12f
                setTextColor(if (index == currentEmojiCategory) Color.WHITE else Color.parseColor("#888888"))
                setPadding((10 * density).toInt(), (4 * density).toInt(), (10 * density).toInt(), (4 * density).toInt())
                background = if (index == currentEmojiCategory) {
                    GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = 12f * density
                        setColor(Color.parseColor("#333338"))
                    }
                } else null
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    currentEmojiCategory = index
                    setupEmojiCategories()
                    loadEmojiGrid(index)
                }
            }
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = (6 * density).toInt()
            }
            container.addView(tab, lp)
        }
    }

    private fun loadEmojiGrid(categoryIndex: Int) {
        val emojis = EmojiManager.getEmojisForCategory(this, categoryIndex)
        renderEmojisInGrid(emojis)
    }

    private fun renderEmojisInGrid(emojis: List<String>) {
        val grid = emojiGrid ?: return
        grid.removeAllViews()
        val density = resources.displayMetrics.density

        for (emoji in emojis) {
            val tv = TextView(this).apply {
                text = emoji
                textSize = 23f
                gravity = Gravity.CENTER
                setPadding((2 * density).toInt(), (2 * density).toInt(), (2 * density).toInt(), (2 * density).toInt())
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    playKeyFeedback()
                    currentInputConnection?.commitText(emoji, 1)
                    EmojiManager.addRecentEmoji(this@MyKeyboardService, emoji)
                }
            }
            val param = GridLayout.LayoutParams().apply {
                width = 0
                height = (38 * density).toInt()
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }
            grid.addView(tv, param)
        }
    }

    // -------------------------------------------------------------------------
    // Clipboard Implementation
    // -------------------------------------------------------------------------

    private fun setupClipboard(view: View) {
        clipboardItemsContainer = view.findViewById(R.id.clipboard_items_container)
        clipboardEmptyView = view.findViewById(R.id.clipboard_empty_view)
        clipboardScrollView = view.findViewById(R.id.clipboard_scroll_view)

        view.findViewById<Button>(R.id.btn_clipboard_clear_all)?.setOnClickListener {
            playKeyFeedback()
            val now = System.currentTimeMillis()
            if (now - lastClearAllClickTime < 3000L) {
                ClipboardHistoryManager.clearAll(this)
                populateClipboardList()
                Toast.makeText(this, "Cleared unpinned items (Pinned kept 📌)", Toast.LENGTH_SHORT).show()
                lastClearAllClickTime = 0L
            } else {
                lastClearAllClickTime = now
                Toast.makeText(this, "Tap Clear All again to confirm", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.btn_clipboard_close)?.setOnClickListener {
            playKeyFeedback()
            showQwertyKeyboard()
        }
    }

    private fun populateClipboardList() {
        val items = ClipboardHistoryManager.getItems(this)
        if (items.isEmpty()) {
            clipboardEmptyView?.visibility = View.VISIBLE
            clipboardScrollView?.visibility = View.GONE
            return
        }

        clipboardEmptyView?.visibility = View.GONE
        clipboardScrollView?.visibility = View.VISIBLE
        val container = clipboardItemsContainer ?: return
        container.removeAllViews()
        val density = resources.displayMetrics.density

        for (item in items) {
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 8f * density
                    setColor(Color.parseColor("#222226"))
                }
                setPadding((10 * density).toInt(), (8 * density).toInt(), (10 * density).toInt(), (8 * density).toInt())
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    playKeyFeedback()
                    currentInputConnection?.commitText(item.text, 1)
                    Toast.makeText(this@MyKeyboardService, "Pasted!", Toast.LENGTH_SHORT).show()
                }
                setOnLongClickListener {
                    playKeyFeedback()
                    ClipboardHistoryManager.togglePin(this@MyKeyboardService, item.id)
                    populateClipboardList()
                    val msg = if (!item.isPinned) "Pinned to top 📌" else "Unpinned"
                    Toast.makeText(this@MyKeyboardService, msg, Toast.LENGTH_SHORT).show()
                    true
                }
            }

            val contentLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val metaRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }

            val timeTv = TextView(this).apply {
                text = ClipboardHistoryManager.formatRelativeTime(item.timestamp)
                textSize = 10f
                setTextColor(Color.parseColor("#888888"))
            }
            metaRow.addView(timeTv)

            if (item.isSensitive) {
                val sensitiveBadgeTv = TextView(this).apply {
                    text = "  ${ClipboardHistoryManager.formatSensitiveBadge(item.timestamp)}"
                    textSize = 10f
                    setTextColor(Color.parseColor("#FFA726"))
                }
                metaRow.addView(sensitiveBadgeTv)
            }

            if (item.isPinned) {
                val pinBadgeTv = TextView(this).apply {
                    text = "  📌 Pinned"
                    textSize = 10f
                    setTextColor(Color.parseColor("#6C63FF"))
                }
                metaRow.addView(pinBadgeTv)
            }

            val textTv = TextView(this).apply {
                text = item.text
                textSize = 13f
                setTextColor(Color.WHITE)
                maxLines = 2
                ellipsize = TextUtils.TruncateAt.END
            }

            contentLayout.addView(metaRow)
            contentLayout.addView(textTv)
            card.addView(contentLayout)

            // Pin Button
            val pinBtn = TextView(this).apply {
                text = if (item.isPinned) "📌" else "📍"
                textSize = 16f
                setPadding((6 * density).toInt(), (4 * density).toInt(), (6 * density).toInt(), (4 * density).toInt())
                isClickable = true
                setOnClickListener {
                    playKeyFeedback()
                    ClipboardHistoryManager.togglePin(this@MyKeyboardService, item.id)
                    populateClipboardList()
                    val msg = if (!item.isPinned) "Pinned to top 📌" else "Unpinned"
                    Toast.makeText(this@MyKeyboardService, msg, Toast.LENGTH_SHORT).show()
                }
            }
            card.addView(pinBtn)

            // Delete Button
            val delBtn = TextView(this).apply {
                text = "✕"
                textSize = 14f
                setTextColor(Color.parseColor("#FF6B6B"))
                setPadding((6 * density).toInt(), (4 * density).toInt(), (6 * density).toInt(), (4 * density).toInt())
                isClickable = true
                setOnClickListener {
                    playKeyFeedback()
                    ClipboardHistoryManager.deleteItem(this@MyKeyboardService, item.id)
                    populateClipboardList()
                }
            }
            card.addView(delBtn)

            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = (6 * density).toInt()
            }
            container.addView(card, lp)
        }
    }

    // -------------------------------------------------------------------------
    // More Menu Implementation (All 14 Tools)
    // -------------------------------------------------------------------------

    private fun setupMoreMenu(view: View) {
        view.findViewById<Button>(R.id.btn_more_menu_close)?.setOnClickListener {
            playKeyFeedback()
            showQwertyKeyboard()
        }
    }

    private fun populateMoreMenu() {
        val row1 = keyboardRoot?.findViewById<LinearLayout>(R.id.more_row_1) ?: return
        val row2 = keyboardRoot?.findViewById<LinearLayout>(R.id.more_row_2) ?: return
        val subpanel = keyboardRoot?.findViewById<LinearLayout>(R.id.more_subpanel_container) ?: return
        row1.removeAllViews()
        row2.removeAllViews()
        subpanel.visibility = View.GONE
        subpanel.removeAllViews()

        val density = resources.displayMetrics.density

        // Row 1: 7 tools
        val toolsRow1 = listOf(
            Triple("Undo", "↩") {
                currentInputConnection?.performContextMenuAction(android.R.id.undo)
            },
            Triple("Redo", "↪") {
                currentInputConnection?.performContextMenuAction(android.R.id.redo)
            },
            Triple("Settings", "⚙") {
                val intent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            },
            Triple("Language", "🌐") {
                showLanguageSubpanel(subpanel)
            },
            Triple("Switch KB", "⌨") {
                showKeyboardPicker()
            },
            Triple("Themes", "🎨") {
                showThemesSubpanel(subpanel)
            },
            Triple("AutoCorrect", "✓") {
                val enabled = !AutoCorrectManager.isAutoCorrectEnabled(this)
                AutoCorrectManager.setAutoCorrectEnabled(this, enabled)
                Toast.makeText(this, "Auto Correct: " + (if (enabled) "ON" else "OFF"), Toast.LENGTH_SHORT).show()
            },
            Triple("Dictionary", "📖") {
                showDictionarySubpanel(subpanel)
            },
            Triple("One-Handed", "📱") {
                toggleOneHanded()
            }
        )

        // Row 2: 7 tools
        val toolsRow2 = listOf(
            Triple("Cursor", "✥") {
                showCursorSubpanel(subpanel)
            },
            Triple("Copy", "📄") {
                currentInputConnection?.performContextMenuAction(android.R.id.copy)
                Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
            },
            Triple("Cut", "✂") {
                currentInputConnection?.performContextMenuAction(android.R.id.cut)
            },
            Triple("Clipboard", "📋") {
                showClipboard()
            },
            Triple("Select All", "🔲") {
                currentInputConnection?.performContextMenuAction(android.R.id.selectAll)
            },
            Triple("Clear Clips", "🗑") {
                ClipboardHistoryManager.clearAll(this)
                Toast.makeText(this, "Clipboard cleared", Toast.LENGTH_SHORT).show()
            },
            Triple("Num Pad", "🔢") {
                showNumPad()
            }
        )

        for ((name, icon, action) in toolsRow1) {
            row1.addView(createMoreToolButton(name, icon, action, density))
        }

        for ((name, icon, action) in toolsRow2) {
            row2.addView(createMoreToolButton(name, icon, action, density))
        }
    }

    private fun createMoreToolButton(name: String, icon: String, action: () -> Any?, density: Float): View {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 8f * density
                setColor(Color.parseColor("#26262A"))
            }
            setPadding((12 * density).toInt(), (6 * density).toInt(), (12 * density).toInt(), (6 * density).toInt())
            isClickable = true
            isFocusable = true
            setOnClickListener {
                playKeyFeedback()
                action()
            }
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                (44 * density).toInt()
            ).apply {
                marginEnd = (6 * density).toInt()
            }
            layoutParams = lp

            val iconTv = TextView(this@MyKeyboardService).apply {
                text = icon
                textSize = 14f
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
            }
            val textTv = TextView(this@MyKeyboardService).apply {
                text = name
                textSize = 9f
                gravity = Gravity.CENTER
                setTextColor(Color.parseColor("#CCCCCC"))
            }
            addView(iconTv)
            addView(textTv)
        }
    }

    private fun showCursorSubpanel(subpanel: LinearLayout) {
        subpanel.visibility = View.VISIBLE
        subpanel.removeAllViews()
        val density = resources.displayMetrics.density

        val arrows = listOf(
            "◀" to KeyEvent.KEYCODE_DPAD_LEFT,
            "▲" to KeyEvent.KEYCODE_DPAD_UP,
            "▼" to KeyEvent.KEYCODE_DPAD_DOWN,
            "▶" to KeyEvent.KEYCODE_DPAD_RIGHT
        )

        for ((label, keyCode) in arrows) {
            val btn = Button(this).apply {
                text = label
                textSize = 14f
                setTextColor(Color.WHITE)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 6f * density
                    setColor(Color.parseColor("#3A3A3E"))
                }
                setOnClickListener {
                    playKeyFeedback()
                    sendDownUpKeyEvents(keyCode)
                }
            }
            val lp = LinearLayout.LayoutParams((44 * density).toInt(), (34 * density).toInt()).apply {
                marginEnd = (6 * density).toInt()
            }
            subpanel.addView(btn, lp)
        }

        val doneBtn = Button(this).apply {
            text = "Done"
            textSize = 11f
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 6f * density
                setColor(Color.parseColor("#5B5BF0"))
            }
            setOnClickListener {
                playKeyFeedback()
                subpanel.visibility = View.GONE
            }
        }
        val doneLp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (34 * density).toInt())
        subpanel.addView(doneBtn, doneLp)
    }

    private fun showLanguageSubpanel(subpanel: LinearLayout) {
        subpanel.visibility = View.VISIBLE
        subpanel.removeAllViews()
        val density = resources.displayMetrics.density

        val langs = listOf(
            AutoCorrectManager.LANG_EN to "English",
            AutoCorrectManager.LANG_TA to "Tamil",
            AutoCorrectManager.LANG_TANG to "Tanglish"
        )

        for ((code, label) in langs) {
            val btn = Button(this).apply {
                text = label
                textSize = 11f
                setTextColor(Color.WHITE)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 6f * density
                    setColor(Color.parseColor("#3A3A3E"))
                }
                setOnClickListener {
                    playKeyFeedback()
                    AutoCorrectManager.setActiveLanguage(this@MyKeyboardService, code)
                    updateLanguageDisplay(showToast = true)
                    subpanel.visibility = View.GONE
                }
            }
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (34 * density).toInt()).apply {
                marginEnd = (6 * density).toInt()
            }
            subpanel.addView(btn, lp)
        }
    }

    private fun showThemesSubpanel(subpanel: LinearLayout) {
        subpanel.visibility = View.VISIBLE
        subpanel.removeAllViews()
        val density = resources.displayMetrics.density

        val themes = listOf(
            KeyboardTheme.DARK_THEME to "Dark",
            KeyboardTheme.LIGHT_THEME to "Light",
            KeyboardTheme.SOLID_BLUE to "Blue",
            KeyboardTheme.SOLID_PINK to "Pink"
        )

        for ((th, label) in themes) {
            val btn = Button(this).apply {
                text = label
                textSize = 11f
                setTextColor(Color.WHITE)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 6f * density
                    setColor(Color.parseColor("#3A3A3E"))
                }
                setOnClickListener {
                    playKeyFeedback()
                    ThemeManager.saveTheme(this@MyKeyboardService, th)
                    applyTheme(th)
                    subpanel.visibility = View.GONE
                }
            }
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (34 * density).toInt()).apply {
                marginEnd = (6 * density).toInt()
            }
            subpanel.addView(btn, lp)
        }
    }

    private fun showDictionarySubpanel(subpanel: LinearLayout) {
        subpanel.visibility = View.VISIBLE
        subpanel.removeAllViews()
        val density = resources.displayMetrics.density

        val targetWord = getCurrentWordFromInput().ifBlank {
            getTargetText()?.split(Regex("\\s+"))?.firstOrNull() ?: ""
        }

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 8f * density
                setColor(Color.parseColor("#1C1C22"))
            }
            setPadding((12 * density).toInt(), (8 * density).toInt(), (12 * density).toInt(), (8 * density).toInt())
        }

        val loadingTv = TextView(this).apply {
            text = "📖 Looking up definition..."
            textSize = 12f
            setTextColor(Color.WHITE)
        }
        card.addView(loadingTv)
        subpanel.addView(card, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

        serviceScope.launch(Dispatchers.IO) {
            val apiKey = ApiKeyManager.getApiKey(this@MyKeyboardService)
            val def = if (targetWord.isNotBlank()) {
                com.cleverkeyboard.app.data.DictionaryManager.lookupWord(targetWord, apiKey)
            } else {
                com.cleverkeyboard.app.data.DictionaryManager.getWordOfTheDay()
            }

            withContext(Dispatchers.Main) {
                card.removeAllViews()

                val headerLayout = LinearLayout(this@MyKeyboardService).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                }

                val titleTv = TextView(this@MyKeyboardService).apply {
                    text = "${def.word.replaceFirstChar { it.uppercase() }} "
                    textSize = 14f
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(Color.WHITE)
                }
                headerLayout.addView(titleTv)

                val posTv = TextView(this@MyKeyboardService).apply {
                    text = "(${def.partOfSpeech})"
                    textSize = 11f
                    setTextColor(Color.parseColor("#9999FF"))
                }
                headerLayout.addView(posTv)
                card.addView(headerLayout)

                val defTv = TextView(this@MyKeyboardService).apply {
                    text = def.definition
                    textSize = 12f
                    setTextColor(Color.parseColor("#E0E0E0"))
                    setPadding(0, (4 * density).toInt(), 0, (4 * density).toInt())
                }
                card.addView(defTv)

                if (def.synonyms.isNotEmpty()) {
                    val synTv = TextView(this@MyKeyboardService).apply {
                        text = "Synonyms: ${def.synonyms.joinToString(", ")}"
                        textSize = 11f
                        setTextColor(Color.parseColor("#80D8FF"))
                    }
                    card.addView(synTv)
                }

                if (def.example.isNotBlank()) {
                    val exTv = TextView(this@MyKeyboardService).apply {
                        text = "\"${def.example}\""
                        textSize = 11f
                        setTextColor(Color.parseColor("#B0BEC5"))
                        setTypeface(null, Typeface.ITALIC)
                        setPadding(0, (2 * density).toInt(), 0, (6 * density).toInt())
                    }
                    card.addView(exTv)
                }

                // Action buttons row
                val actionsLayout = LinearLayout(this@MyKeyboardService).apply {
                    orientation = LinearLayout.HORIZONTAL
                }

                val insertBtn = Button(this@MyKeyboardService).apply {
                    text = "Insert Word"
                    textSize = 10f
                    setTextColor(Color.WHITE)
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = 6f * density
                        setColor(Color.parseColor("#5B5BF0"))
                    }
                    setOnClickListener {
                        playKeyFeedback()
                        currentInputConnection?.commitText(def.word + " ", 1)
                        subpanel.visibility = View.GONE
                    }
                }
                actionsLayout.addView(insertBtn, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (30 * density).toInt()).apply {
                    marginEnd = (6 * density).toInt()
                })

                val wotdBtn = Button(this@MyKeyboardService).apply {
                    text = "Word of the Day"
                    textSize = 10f
                    setTextColor(Color.WHITE)
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = 6f * density
                        setColor(Color.parseColor("#3A3A3E"))
                    }
                    setOnClickListener {
                        playKeyFeedback()
                        val wotd = com.cleverkeyboard.app.data.DictionaryManager.getWordOfTheDay()
                        titleTv.text = "${wotd.word} "
                        posTv.text = "(${wotd.partOfSpeech})"
                        defTv.text = wotd.definition
                        insertBtn.setOnClickListener {
                            playKeyFeedback()
                            currentInputConnection?.commitText(wotd.word + " ", 1)
                            subpanel.visibility = View.GONE
                        }
                    }
                }
                actionsLayout.addView(wotdBtn, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (30 * density).toInt()))

                card.addView(actionsLayout)
            }
        }
    }

    private fun toggleOneHanded() {
        isOneHanded = !isOneHanded
        val root = keyboardRoot ?: return
        val lp = root.layoutParams as? FrameLayout.LayoutParams ?: return
        if (isOneHanded) {
            val width = (resources.displayMetrics.widthPixels * 0.78f).toInt()
            lp.width = width
            lp.gravity = if (oneHandedSideLeft) Gravity.START or Gravity.BOTTOM else Gravity.END or Gravity.BOTTOM
            Toast.makeText(this, "One-Handed ON", Toast.LENGTH_SHORT).show()
        } else {
            lp.width = FrameLayout.LayoutParams.MATCH_PARENT
            lp.gravity = Gravity.BOTTOM
            Toast.makeText(this, "One-Handed OFF", Toast.LENGTH_SHORT).show()
        }
        root.layoutParams = lp
        root.requestLayout()
    }

    fun showKeyboardPicker() {
        try {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showInputMethodPicker()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to show keyboard picker", e)
        }
    }

    fun moveCursor(direction: Int) {
        try {
            if (direction < 0) {
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT)
            } else if (direction > 0) {
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_RIGHT)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to move cursor", e)
        }
    }

    private fun setupBottomDismissBar(view: View) {
        keyboardBottomDismissBar = view.findViewById(R.id.keyboard_bottom_dismiss_bar)
        btnKeyboardDismiss = view.findViewById(R.id.btn_keyboard_dismiss)
        btnBottomSwitchKb = view.findViewById(R.id.btn_bottom_switch_kb)
        iconKeyboardDismiss = view.findViewById(R.id.icon_keyboard_dismiss)
        iconBottomSwitchKb = view.findViewById(R.id.icon_bottom_switch_kb)

        btnKeyboardDismiss?.setOnClickListener {
            playKeyFeedback()
            requestHideSelf(0)
        }

        btnBottomSwitchKb?.setOnClickListener {
            playKeyFeedback()
            showKeyboardPicker()
        }
    }

    override fun onPress(primaryCode: Int) {}
    override fun onRelease(primaryCode: Int) {}
    override fun onText(text: CharSequence?) {
        currentInputConnection?.commitText(text, 1)
    }
    override fun swipeLeft() {}
    override fun swipeRight() {}
    override fun swipeDown() {
        requestHideSelf(0)
    }
    override fun swipeUp() {}
}
