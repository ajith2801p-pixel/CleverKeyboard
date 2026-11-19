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
import android.content.ClipDescription
import android.net.Uri
import android.util.Size
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import com.cleverkeyboard.app.clipboard.ScreenshotManager
import com.cleverkeyboard.app.clipboard.ScreenshotItem
import com.cleverkeyboard.app.autofill.UserAutofillManager
import com.cleverkeyboard.app.gif.GifManager
import com.cleverkeyboard.app.gif.GifItem

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
    private var undoneAutocorrectWord: String? = null

    // Real-Time Intelligence & Grammar state
    private var activeGrammarFix: com.cleverkeyboard.app.intelligence.GrammarFix? = null
    private var activeTypingContext: com.cleverkeyboard.app.intelligence.TypingContext? = null

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
    // Key Preview Bubble (0.25s popup)
    private var keyboardContainerLayout: ViewGroup? = null
    private var keyPreviewBubble: TextView? = null
    private val keyPreviewDismissHandler = Handler(Looper.getMainLooper())
    private val keyPreviewDismissRunnable = Runnable {
        keyPreviewBubble?.visibility = View.GONE
    }

    // Recent Screenshot detection in Suggestion Bar
    private var screenshotChipContainer: View? = null
    private var screenshotChipThumbnail: ImageView? = null
    private var latestScreenshotItem: ScreenshotItem? = null
    private val screenshotChipHandler = Handler(Looper.getMainLooper())

    // GIF Keyboard
    private var gifContainer: LinearLayout? = null
    private var gifSearchInput: EditText? = null
    private var btnGifClearSearch: ImageView? = null
    private var btnGifBackAbc: View? = null
    private var gifCategoriesContainer: LinearLayout? = null
    private var gifGridContainer: LinearLayout? = null


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
        private const val CODE_EMOJI = -101
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

        keyboardContainerLayout = view.findViewById(R.id.keyboard_container_layout)
        keyPreviewBubble = view.findViewById(R.id.key_preview_bubble)
        screenshotChipContainer = view.findViewById(R.id.screenshot_chip_container)
        screenshotChipThumbnail = view.findViewById(R.id.screenshot_chip_thumbnail)

        gifContainer = view.findViewById(R.id.gif_container)
        gifSearchInput = view.findViewById(R.id.gif_search_input)
        btnGifClearSearch = view.findViewById(R.id.btn_gif_clear_search)
        btnGifBackAbc = view.findViewById(R.id.btn_gif_back_abc)
        gifCategoriesContainer = view.findViewById(R.id.gif_categories_container)
        gifGridContainer = view.findViewById(R.id.gif_grid_container)

        screenshotChipContainer?.setOnClickListener {
            playKeyFeedback()
            val shot = latestScreenshotItem
            if (shot != null) {
                commitScreenshot(shot.uri)
            }
            screenshotChipContainer?.visibility = View.GONE
        }

        view.findViewById<Button>(R.id.btn_emoji_to_gif)?.setOnClickListener {
            playKeyFeedback()
            showGifKeyboard()
        }

        setupGifKeyboard(view)


        // Initialize Keyboard layouts
        qwertyKeyboard = Keyboard(this, R.xml.qwerty)
        symbolsKeyboard = Keyboard(this, R.xml.symbols)
        symbolsShiftKeyboard = Keyboard(this, R.xml.symbols_shift)
        tamilKeyboard = Keyboard(this, R.xml.tamil)
        tamilShiftKeyboard = Keyboard(this, R.xml.tamil_shift)

        val activeLang = AutoCorrectManager.getActiveLanguage(this)
        keyboardView?.keyboard = if (activeLang == AutoCorrectManager.LANG_TA) tamilKeyboard else qwertyKeyboard
        keyboardView?.setOnKeyboardActionListener(this)

        
        keyboardView?.onKeyPreviewListener = preview@ { key, label, isDown ->
            if (isDown && key != null && !label.isNullOrEmpty()) {
                keyPreviewDismissHandler.removeCallbacks(keyPreviewDismissRunnable)
                val kbView = keyboardView ?: return@preview
                val container = keyboardContainerLayout ?: return@preview
                val bubble = keyPreviewBubble ?: return@preview

                val keyLoc = IntArray(2)
                kbView.getLocationInWindow(keyLoc)
                val contLoc = IntArray(2)
                container.getLocationInWindow(contLoc)

                val keyLeftInCont = (keyLoc[0] - contLoc[0]) + key.x
                val keyTopInCont = (keyLoc[1] - contLoc[1]) + key.y

                val density = resources.displayMetrics.density
                val bubbleWidth = (54 * density).toInt()
                val bubbleHeight = (62 * density).toInt()

                val lp = bubble.layoutParams as? FrameLayout.LayoutParams
                if (lp != null) {
                    lp.width = bubbleWidth
                    lp.height = bubbleHeight
                    bubble.layoutParams = lp
                }

                val targetX = keyLeftInCont + (key.width - bubbleWidth) / 2f
                val targetY = keyTopInCont - bubbleHeight + (4 * density)

                bubble.translationX = targetX
                bubble.translationY = targetY
                bubble.text = label
                bubble.visibility = View.VISIBLE
            } else if (!isDown) {
                keyPreviewDismissHandler.removeCallbacks(keyPreviewDismissRunnable)
                keyPreviewDismissHandler.postDelayed(keyPreviewDismissRunnable, 10L)
            }
        }

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
        checkRecentScreenshot()

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
            val targetKb = if (shifted) tamilShiftKeyboard else tamilKeyboard
            if (keyboardView?.keyboard != targetKb) {
                keyboardView?.keyboard = targetKb
            }
        } else {
            qwertyKeyboard?.isShifted = shifted
            if (keyboardView?.keyboard != qwertyKeyboard) {
                keyboardView?.keyboard = qwertyKeyboard
            }
            keyboardView?.isShifted = shifted
        }
        keyboardView?.isShiftLocked = isShiftLocked
        keyboardView?.keyboard?.keys?.forEach { k ->
            if (k.codes.isNotEmpty() && k.codes[0] == -1) {
                k.on = shifted && isShiftLocked
            }
        }
        keyboardView?.invalidate()
    }

    private fun handleShift() {
        val now = System.currentTimeMillis()
        if (isShiftLocked) {
            // Caps Lock is active -> Tap Shift toggles directly to Lowercase
            isShiftLocked = false
            setShiftState(shifted = false, auto = false)
        } else if (isShifted) {
            // Shift is already active (either single-shot or auto-capitalized):
            if (now - lastShiftPressTime < 500) {
                // Double tap within 500ms activates Caps Lock (all caps)
                isShiftLocked = true
                setShiftState(shifted = true, auto = false)
            } else {
                // Tapping again after 500ms toggles off to Lowercase
                isShiftLocked = false
                setShiftState(shifted = false, auto = false)
            }
        } else {
            // Lowercase -> Single tap activates One-shot Uppercase for exactly one letter
            isShiftLocked = false
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
        checkRecentScreenshot()

        // 5. NumPad dynamic theming
        numPadContainer?.let { container ->
            if (isPhotoTheme || theme.hasGradient) {
                container.setBackgroundColor(Color.TRANSPARENT)
            } else {
                container.setBackgroundColor(theme.keyboardBgColor)
            }
            val numPadTextIds = listOf(
                R.id.num_key_plus, R.id.num_key_minus, R.id.num_key_mul, R.id.num_key_div,
                R.id.num_key_1, R.id.num_key_2, R.id.num_key_3, R.id.num_key_percent,
                R.id.num_key_4, R.id.num_key_5, R.id.num_key_6, R.id.num_key_space,
                R.id.num_key_7, R.id.num_key_8, R.id.num_key_9,
                R.id.num_key_comma, R.id.num_key_sym, R.id.num_key_0, R.id.num_key_equal, R.id.num_key_dot
            )
            for (id in numPadTextIds) {
                container.findViewById<TextView>(id)?.setTextColor(theme.keyTextColor)
            }
            container.findViewById<ImageView>(R.id.num_icon_backspace)?.setColorFilter(theme.keyTextColor)

            val abcBtn = container.findViewById<TextView>(R.id.num_key_abc)
            abcBtn?.let {
                val pill = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 20f * density
                    setColor(adjustAlpha(theme.keyBgColor, 0.88f))
                }
                it.background = pill
                it.setTextColor(theme.keyTextColor)
            }

            val enterBtn = container.findViewById<View>(R.id.num_key_enter)
            enterBtn?.let {
                val pill = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 20f * density
                    setColor(theme.accentColor)
                }
                it.background = pill
                val enterTint = if (isColorLight(theme.accentColor)) Color.BLACK else Color.WHITE
                container.findViewById<ImageView>(R.id.num_icon_enter)?.setColorFilter(enterTint)
            }
        }

        // 6. Bottom Dismiss Bar visibility & styling
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

    private fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = (Color.alpha(color) * factor).toInt().coerceIn(0, 255)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
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
        val activeLang = AutoCorrectManager.getActiveLanguage(this)
        val before = ic.getTextBeforeCursor(120, 0)?.toString() ?: ""

        if (chosenText.startsWith("👤 ")) {
            val cleanName = chosenText.removePrefix("👤 ")
            if (word.isNotEmpty()) {
                ic.deleteSurroundingText(word.length, 0)
            }
            ic.commitText(cleanName, 1)
            updateSuggestions()
            return
        } else if (chosenText.startsWith("📞 ")) {
            val cleanPhone = chosenText.removePrefix("📞 ")
            if (word.isNotEmpty()) {
                ic.deleteSurroundingText(word.length, 0)
            }
            ic.commitText(cleanPhone, 1)
            updateSuggestions()
            return
        }

        // Case 0: Undo Autocorrect via tapping original candidate in suggestion bar
        val undoOrig = lastAutocorrectOriginal
        val undoRepl = lastAutocorrectReplacement
        if (undoOrig != null && undoRepl != null && chosenText.equals(undoOrig, ignoreCase = true)) {
            val replWithSpace = "$undoRepl "
            if (before.endsWith(replWithSpace, ignoreCase = true)) {
                ic.deleteSurroundingText(replWithSpace.length, 0)
                ic.commitText(undoOrig, 1)
                lastAutocorrectOriginal = null
                lastAutocorrectReplacement = null
                undoneAutocorrectWord = undoOrig.lowercase()
                updateSuggestions()
                return
            } else if (before.endsWith(undoRepl, ignoreCase = true)) {
                ic.deleteSurroundingText(undoRepl.length, 0)
                ic.commitText(undoOrig, 1)
                lastAutocorrectOriginal = null
                lastAutocorrectReplacement = null
                undoneAutocorrectWord = undoOrig.lowercase()
                updateSuggestions()
                return
            }
        }

        // Case 1: Check if chosenText is a Grammar Fix
        val fix = activeGrammarFix
        if (fix != null && (chosenText.equals(fix.correctedText, ignoreCase = true) || chosenText.equals(fix.fullCorrectedSentence, ignoreCase = true))) {
            val currentSent = activeTypingContext?.currentSentence ?: ""
            if (currentSent.isNotBlank() && before.endsWith(currentSent)) {
                ic.deleteSurroundingText(currentSent.length, 0)
                val replacement = if (before.endsWith(" ") && !fix.fullCorrectedSentence.endsWith(" ")) {
                    fix.fullCorrectedSentence + " "
                } else {
                    fix.fullCorrectedSentence
                }
                ic.commitText(replacement, 1)
                activeGrammarFix = null
                updateSuggestions()
                return
            }
        }

        // Case 2: Check if chosenText is a full-phrase / sentence completion
        val isSentenceCompletion = chosenText.contains(" ") || chosenText.endsWith(".") || chosenText.endsWith("?") || chosenText.endsWith("!")
        if (isSentenceCompletion && word.isEmpty()) {
            val completionText = if (!before.endsWith(" ") && !chosenText.startsWith(" ")) {
                " $chosenText"
            } else {
                chosenText
            }
            ic.commitText(completionText, 1)
            updateSuggestions()
            return
        }

        // Case 3: Standard word or personal prefix replacement
        if (word.isNotEmpty()) {
            ic.deleteSurroundingText(word.length, 0)
        }
        ic.commitText(chosenText + " ", 1)

        // Track for Undo Autocorrect if user committed a correction
        if (word.isNotEmpty() && !word.equals(chosenText, ignoreCase = false)) {
            lastAutocorrectOriginal = word
            lastAutocorrectReplacement = chosenText
        } else {
            lastAutocorrectOriginal = null
            lastAutocorrectReplacement = null
        }

        com.cleverkeyboard.app.intelligence.CleverIntelligenceEngine.getInstance(this).onWordCommitted(chosenText, activeLang, currentInputEditorInfo)
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
        val before = ic.getTextBeforeCursor(120, 0)?.toString() ?: ""
        val activeLang = AutoCorrectManager.getActiveLanguage(this)

        // 0. Check for Name or Phone autofill fields ONLY when the field is empty
        if (before.isBlank()) {
            if (com.cleverkeyboard.app.autofill.UserAutofillManager.isNameField(info)) {
                val names = com.cleverkeyboard.app.autofill.UserAutofillManager.getUserNames(this)
                val views = listOf(suggestion1, suggestion2, suggestion3, suggestion4, suggestion5)
                for (i in views.indices) {
                    val v = views[i]
                    val name = names.getOrNull(i)
                    if (name != null) {
                        v?.text = "👤 $name"
                        v?.visibility = View.VISIBLE
                        v?.setTypeface(null, android.graphics.Typeface.BOLD)
                        v?.setTextColor(currentTheme.accentColor)
                    } else {
                        v?.visibility = View.GONE
                    }
                }
                return
            } else if (com.cleverkeyboard.app.autofill.UserAutofillManager.isPhoneOrNumberField(info)) {
                val phones = com.cleverkeyboard.app.autofill.UserAutofillManager.getUserPhoneNumbers(this)
                val views = listOf(suggestion1, suggestion2, suggestion3, suggestion4, suggestion5)
                for (i in views.indices) {
                    val v = views[i]
                    val phone = phones.getOrNull(i)
                    if (phone != null) {
                        v?.text = "📞 $phone"
                        v?.visibility = View.VISIBLE
                        v?.setTypeface(null, android.graphics.Typeface.BOLD)
                        v?.setTextColor(currentTheme.accentColor)
                    } else {
                        v?.visibility = View.GONE
                    }
                }
                return
            }
        }

        // Query unified AI typing intelligence engine
        val intelligence = com.cleverkeyboard.app.intelligence.CleverIntelligenceEngine.getInstance(this)
        val typingContext = intelligence.analyzeContext(before, activeLang)
        activeTypingContext = typingContext
        activeGrammarFix = intelligence.checkLocalGrammar(typingContext.currentSentence)

        val intelligentSuggestions = intelligence.getSuggestions(typingContext, maxSuggestions = 5)

        val rawSuggestions = if (intelligentSuggestions.isNotEmpty()) {
            intelligentSuggestions
        } else {
            AutoCorrectManager.getDynamicSuggestions(this, before)
        }

        val suggestions = mutableListOf<SuggestionItem>()
        val orig = lastAutocorrectOriginal
        val repl = lastAutocorrectReplacement
        if (orig != null && repl != null && before.endsWith("$repl ", ignoreCase = true)) {
            suggestions.add(SuggestionItem(text = orig, isCorrection = true, isPrimary = true))
        }
        for (item in rawSuggestions) {
            if (suggestions.size >= 5) break
            if (suggestions.none { it.text.equals(item.text, ignoreCase = true) }) {
                suggestions.add(item)
            }
        }

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
        val spaceText = when (lang) {
            AutoCorrectManager.LANG_EN -> "English"
            AutoCorrectManager.LANG_TA -> "தமிழ்"
            AutoCorrectManager.LANG_TANG -> "Tanglish"
            else -> "English"
        }
        val toastText = when (lang) {
            AutoCorrectManager.LANG_EN -> "English"
            AutoCorrectManager.LANG_TA -> "தமிழ்"
            AutoCorrectManager.LANG_TANG -> "Tanglish"
            else -> "English"
        }
        keyboardView?.spaceLabel = spaceText
        if (lang == AutoCorrectManager.LANG_TA) {
            keyboardView?.keyboard = if (isShifted) tamilShiftKeyboard else tamilKeyboard
        } else {
            keyboardView?.keyboard = qwertyKeyboard
        }
        resetDefaultSuggestions()
        keyboardView?.invalidate()
        if (showToast) {
            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
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
                // Instant local grammar engine check as immediate rule-based fallback
                val localFix = com.cleverkeyboard.app.intelligence.GrammarCorrectionEngine.findGrammarIssue(inputText)

                val callResult = task(inputText, apiKey)
                val cloudResult = callResult.getOrNull()?.trim() ?: ""
                val result = if (cloudResult.isNotBlank()) cloudResult else (localFix?.correctedText ?: "")

                withContext(Dispatchers.Main) {
                    mainHandler.removeCallbacks(hideLoadingRunnable)
                    aiLoadingContainer?.visibility = View.GONE

                    if (result.isNotBlank()) {
                        val ic = currentInputConnection
                        if (ic != null) {
                            ic.beginBatchEdit()
                            try {
                                ic.finishComposingText()
                                val sel = ic.getSelectedText(0)?.toString()
                                if (!sel.isNullOrEmpty()) {
                                    ic.commitText(result, 1)
                                } else {
                                    // Strictly delete ONLY the target text length, never delete entire history!
                                    val deleteCount = minOf(inputText.length, ic.getTextBeforeCursor(inputText.length, 0)?.length ?: inputText.length)
                                    ic.deleteSurroundingText(deleteCount, 0)
                                    ic.commitText(result, 1)
                                }
                            } finally {
                                ic.endBatchEdit()
                            }
                            Toast.makeText(this@MyKeyboardService, "Fixed with AI ✓", Toast.LENGTH_SHORT).show()
                            showStatusBanner("✓ Text updated", 2000L)
                        }
                    } else {
                        val err = callResult.exceptionOrNull()?.message ?: "AI could not process text"
                        Toast.makeText(this@MyKeyboardService, err, Toast.LENGTH_SHORT).show()
                        showStatusBanner("⚠️ $err", 3000L)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "AI Task failed", e)
                withContext(Dispatchers.Main) {
                    mainHandler.removeCallbacks(hideLoadingRunnable)
                    aiLoadingContainer?.visibility = View.GONE
                    Toast.makeText(this@MyKeyboardService, "AI error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showToneOptions() {
        showToneSelector("WRITE")
    }

    private fun showToneSelector(mode: String) {
        var text = getTargetText()
        if (text.isNullOrBlank()) {
            val clipItem = ClipboardHistoryManager.getItems(this).firstOrNull()?.text
            if (!clipItem.isNullOrBlank()) {
                text = clipItem.trim()
                Toast.makeText(this, "Replying to copied message 📋", Toast.LENGTH_SHORT).show()
            }
        }
        if (text.isNullOrBlank()) {
            Toast.makeText(this, "Please type or copy a message first", Toast.LENGTH_SHORT).show()
            return
        }
        val apiKey = checkOrPromptApiKey() ?: return

        currentAiToneMode = mode
        currentAiToneInputText = text

        val tones = listOf(
            "Casual",
            "Professional",
            "Formal",
            "Friendly",
            "Confident",
            "Polite",
            "Short & Concise",
            "Persuasive",
            "Empathetic",
            "Sarcastic",
            "Humorous",
            "Mysterious",
            "Motivational",
            "Assertive",
            "Flirty",
            "Funny",
            "Cute",
            "Cool"
        )
        val prefs = getSharedPreferences("clever_keyboard_prefs", Context.MODE_PRIVATE)
        var selectedTone = prefs.getString("saved_ai_reply_tone", "Casual") ?: "Casual"
        if (!tones.any { it.equals(selectedTone, ignoreCase = true) }) {
            selectedTone = "Casual"
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
                            "casual" -> Color.parseColor("#26A69A") // Teal
                            "professional" -> Color.parseColor("#1E88E5") // Sapphire Blue
                            "formal" -> Color.parseColor("#3949AB") // Indigo/Navy
                            "friendly" -> Color.parseColor("#FFA000") // Amber
                            "confident" -> Color.parseColor("#7B1FA2") // Deep Purple
                            "polite" -> Color.parseColor("#00897B") // Sea Green
                            "short & concise", "concise" -> Color.parseColor("#546E7A") // Slate Grey
                            "flirty" -> Color.parseColor("#E91E63") // Pink
                            "funny" -> Color.parseColor("#FB8C00") // Amber
                            "cute" -> Color.parseColor("#F06292") // Soft Rose
                            "cool" -> Color.parseColor("#00ACC1") // Cyan
                            "persuasive" -> Color.parseColor("#8E24AA") // Purple
                            "empathetic" -> Color.parseColor("#00897B") // Teal
                            "sarcastic" -> Color.parseColor("#D81B60") // Ruby Magenta
                            "humorous" -> Color.parseColor("#F57C00") // Warm Orange
                            "mysterious" -> Color.parseColor("#5E35B1") // Deep Violet
                            "motivational" -> Color.parseColor("#43A047") // Emerald Green
                            "assertive" -> Color.parseColor("#E53935") // Crimson Red
                            else -> Color.parseColor("#5B5BF0") // Accent Blue
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
            ic.finishComposingText()
            val sel = ic.getSelectedText(0)?.toString()
            if (!sel.isNullOrEmpty()) {
                ic.commitText(currentAiToneResult, 1)
            } else {
                val targetLen = currentAiToneInputText.length
                if (targetLen > 0) {
                    val beforeLen = ic.getTextBeforeCursor(targetLen, 0)?.length ?: 0
                    if (beforeLen > 0) {
                        ic.deleteSurroundingText(beforeLen, 0)
                    }
                }
                ic.commitText(currentAiToneResult, 1)
            }
        } finally {
            ic.endBatchEdit()
        }
        Toast.makeText(this, "Sent!", Toast.LENGTH_SHORT).show()
        hideSubPanel()
    }

    private fun showTranslateOptions() {
        val languages = listOf(
            Pair("⇄ To English", "English"),
            Pair("⇄ To Spanish", "Spanish"),
            Pair("⇄ To German", "German"),
            Pair("⇄ To Japanese", "Japanese"),
            Pair("⇄ To French", "French"),
            Pair("⇄ To Portuguese", "Portuguese"),
            Pair("⇄ To Russian", "Russian"),
            Pair("⇄ To Italian", "Italian"),
            Pair("⇄ To Arabic", "Arabic"),
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
            Pair("En → Korean", "Korean"),
            Pair("En → Chinese (Simp)", "Chinese Simplified"),
            Pair("En → Chinese (Trad)", "Chinese Traditional"),
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
        var text = getTargetText()
        if (text.isNullOrBlank()) {
            val clipItem = ClipboardHistoryManager.getItems(this).firstOrNull()?.text
            if (!clipItem.isNullOrBlank()) {
                text = clipItem.trim()
                Toast.makeText(this, "Generating replies for copied message 📋", Toast.LENGTH_SHORT).show()
            }
        }
        if (text.isNullOrBlank()) {
            Toast.makeText(this, "Please type or copy a message to reply to", Toast.LENGTH_SHORT).show()
            return
        }
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
                                    ic.finishComposingText()
                                    val sel = ic.getSelectedText(0)?.toString()
                                    if (!sel.isNullOrEmpty()) {
                                        ic.commitText(chosenReply, 1)
                                    } else {
                                        val targetLen = text.length
                                        val beforeLen = ic.getTextBeforeCursor(targetLen, 0)?.length ?: 0
                                        if (beforeLen > 0) {
                                            ic.deleteSurroundingText(beforeLen, 0)
                                        }
                                        ic.commitText(chosenReply, 1)
                                    }
                                } finally {
                                    ic.endBatchEdit()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this@MyKeyboardService, "AI could not generate replies, try again", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mainHandler.removeCallbacks(hideLoadingRunnable)
                    aiLoadingContainer?.visibility = View.GONE
                    Toast.makeText(this@MyKeyboardService, "AI error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
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

            // Emoji Key (-101)
            CODE_EMOJI -> {
                resetDefaultSuggestions()
                showEmojiKeyboard()
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
                val isUndoneWord = undoneAutocorrectWord != null && word.equals(undoneAutocorrectWord, ignoreCase = true)
                val correction = if (allowed && word.isNotEmpty() && !isUndoneWord) {
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
                undoneAutocorrectWord = null

                if (word.isNotBlank()) {
                    val activeLang = AutoCorrectManager.getActiveLanguage(this)
                    val learnedWord = correction ?: word
                    com.cleverkeyboard.app.intelligence.CleverIntelligenceEngine.getInstance(this).onWordCommitted(learnedWord, activeLang, currentInputEditorInfo)
                }

                updateSuggestions()

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
                undoneAutocorrectWord = null
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
        gifContainer?.visibility = View.GONE
        keyPreviewBubble?.visibility = View.GONE
        clipboardContainer?.visibility = View.GONE
        moreMenuContainer?.visibility = View.GONE
        keyboardView?.visibility = View.VISIBLE
        val activeLang = AutoCorrectManager.getActiveLanguage(this)
        val defaultTextKeyboard = if (activeLang == AutoCorrectManager.LANG_TA) tamilKeyboard else qwertyKeyboard
        if (keyboardView?.keyboard !== defaultTextKeyboard) {
            keyboardView?.keyboard = defaultTextKeyboard
        }
        val theme = ThemeManager.getTheme(this)
        numberRowContainer?.visibility = if (theme.showNumberRow) View.VISIBLE else View.GONE
        suggestionBarContainer?.visibility = View.VISIBLE
    }

    
    // =========================================================================
    // Screenshot & GIF Handlers
    // =========================================================================

    private fun checkRecentScreenshot() {
        screenshotChipHandler.removeCallbacksAndMessages(null)
        screenshotChipContainer?.visibility = View.GONE
        serviceScope.launch(Dispatchers.IO) {
            val shot = ScreenshotManager.getLatestScreenshot(this@MyKeyboardService)
            if (shot != null && ScreenshotManager.isScreenshotVeryRecent(shot, maxSeconds = 45)) {
                latestScreenshotItem = shot
                withContext(Dispatchers.Main) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val bmp = contentResolver.loadThumbnail(shot.uri, Size(80, 80), null)
                            screenshotChipThumbnail?.setImageBitmap(bmp)
                        } else {
                            screenshotChipThumbnail?.setImageURI(shot.uri)
                        }
                        screenshotChipContainer?.visibility = View.VISIBLE
                        // Hide automatically after 5 seconds (as user requested)
                        screenshotChipHandler.postDelayed({
                            screenshotChipContainer?.visibility = View.GONE
                        }, 5000L)
                    } catch (_: Exception) {}
                }
            }
        }
    }

    private fun commitScreenshot(uri: Uri) {
        val ic = currentInputConnection ?: return
        val info = currentInputEditorInfo ?: return

        val mimeTypes = EditorInfoCompat.getContentMimeTypes(info)
        val imageSupported = mimeTypes.any { it.startsWith("image/", ignoreCase = true) || it.equals("*/*", ignoreCase = true) }

        if (imageSupported) {
            val description = ClipDescription("Screenshot", arrayOf("image/png", "image/jpeg"))
            val contentInfo = InputContentInfoCompat(uri, description, uri)
            val flags = InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION
            val success = InputConnectionCompat.commitContent(ic, info, contentInfo, flags, null)
            if (!success) {
                copyImageUriToClipboard(uri)
            }
        } else {
            copyImageUriToClipboard(uri)
        }
    }

    private fun copyImageUriToClipboard(uri: Uri) {
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newUri(contentResolver, "Screenshot", uri)
        cm?.setPrimaryClip(clip)
        Toast.makeText(this, "Screenshot copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun commitGif(gifItem: GifItem) {
        val ic = currentInputConnection ?: return
        val info = currentInputEditorInfo ?: return

        val mimeTypes = EditorInfoCompat.getContentMimeTypes(info)
        val gifSupported = mimeTypes.any { it.equals("image/gif", ignoreCase = true) || it.equals("image/*", ignoreCase = true) }

        if (gifSupported) {
            val uri = Uri.parse(gifItem.gifUrl)
            val description = ClipDescription(gifItem.title, arrayOf("image/gif"))
            val contentInfo = InputContentInfoCompat(uri, description, Uri.parse(gifItem.gifUrl))
            val flags = InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION
            InputConnectionCompat.commitContent(ic, info, contentInfo, flags, null)
        } else {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            cm?.setPrimaryClip(ClipData.newPlainText("GIF", gifItem.gifUrl))
            Toast.makeText(this, "GIF link copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupGifKeyboard(view: View) {
        view.findViewById<View>(R.id.btn_gif_back_abc)?.setOnClickListener {
            playKeyFeedback()
            showQwertyKeyboard()
        }
        val clearBtn = view.findViewById<View>(R.id.btn_gif_clear_search)
        val searchInput = view.findViewById<EditText>(R.id.gif_search_input)

        clearBtn?.setOnClickListener {
            searchInput?.text?.clear()
            clearBtn.visibility = View.GONE
            loadGifGrid("Trending")
        }

        searchInput?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val q = s?.toString()?.trim() ?: ""
                clearBtn?.visibility = if (q.isNotEmpty()) View.VISIBLE else View.GONE
                loadGifGrid(if (q.isNotEmpty()) q else "Trending")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showGifKeyboard() {
        deactivateEmojiSearchMode()
        numPadContainer?.visibility = View.GONE
        emojiContainer?.visibility = View.GONE
        clipboardContainer?.visibility = View.GONE
        moreMenuContainer?.visibility = View.GONE
        keyboardView?.visibility = View.GONE
        numberRowContainer?.visibility = View.GONE
        suggestionBarContainer?.visibility = View.GONE
        keyPreviewBubble?.visibility = View.GONE
        gifContainer?.visibility = View.VISIBLE
        loadGifCategories()
        loadGifGrid("Trending")
    }

    private fun loadGifCategories() {
        val container = gifCategoriesContainer ?: return
        container.removeAllViews()
        val density = resources.displayMetrics.density

        for (cat in GifManager.CATEGORIES) {
            val chip = TextView(this).apply {
                text = cat
                textSize = 12f
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 14f * density
                    setColor(Color.parseColor("#262A30"))
                }
                setPadding((12 * density).toInt(), (4 * density).toInt(), (12 * density).toInt(), (4 * density).toInt())
                isClickable = true
                isFocusable = false
                setOnClickListener {
                    playKeyFeedback()
                    loadGifGrid(cat)
                }
            }
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = (6 * density).toInt()
            }
            container.addView(chip, lp)
        }
    }

    private fun loadGifGrid(queryOrCat: String) {
        val container = gifGridContainer ?: return
        container.removeAllViews()
        val density = resources.displayMetrics.density

        val items = GifManager.searchGifs(queryOrCat)
        for (i in items.indices step 2) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (68 * density).toInt()
                ).apply {
                    bottomMargin = (6 * density).toInt()
                }
            }

            val item1 = items[i]
            val card1 = createGifCard(item1, density)
            val lp1 = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).apply {
                marginEnd = (3 * density).toInt()
            }
            row.addView(card1, lp1)

            if (i + 1 < items.size) {
                val item2 = items[i + 1]
                val card2 = createGifCard(item2, density)
                val lp2 = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).apply {
                    marginStart = (3 * density).toInt()
                }
                row.addView(card2, lp2)
            } else {
                val dummy = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                }
                row.addView(dummy)
            }

            container.addView(row)
        }
    }

    private fun createGifCard(item: GifItem, density: Float): View {
        return FrameLayout(this).apply {
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 12f * density
                setColor(Color.parseColor("#262A30"))
                setStroke((1 * density).toInt(), Color.parseColor("#1AFFFFFF"))
            }
            isClickable = true
            isFocusable = false
            setOnClickListener {
                playKeyFeedback()
                commitGif(item)
            }

            val label = TextView(this@MyKeyboardService).apply {
                text = "🎞️ ${item.title}"
                textSize = 12f
                setTextColor(Color.WHITE)
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER
                setPadding((8 * density).toInt(), (4 * density).toInt(), (8 * density).toInt(), (4 * density).toInt())
            }
            addView(label, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        }
    }

    private fun showNumPad() {
        deactivateEmojiSearchMode()
        emojiContainer?.visibility = View.GONE
        clipboardContainer?.visibility = View.GONE
        moreMenuContainer?.visibility = View.GONE
        keyboardView?.visibility = View.GONE
        numberRowContainer?.visibility = View.GONE
        suggestionBarContainer?.visibility = View.GONE
        numPadContainer?.visibility = View.VISIBLE
    }

    private fun showEmojiKeyboard() {
        deactivateEmojiSearchMode()
        numPadContainer?.visibility = View.GONE
        clipboardContainer?.visibility = View.GONE
        gifContainer?.visibility = View.GONE
        keyPreviewBubble?.visibility = View.GONE
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
        gifContainer?.visibility = View.GONE
        keyPreviewBubble?.visibility = View.GONE
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
        gifContainer?.visibility = View.GONE
        keyPreviewBubble?.visibility = View.GONE
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
        // If a word was auto-corrected, tapping backspace reverts to original typed word!
        val orig = lastAutocorrectOriginal
        val repl = lastAutocorrectReplacement
        if (selected.isNullOrEmpty() && orig != null && repl != null) {
            val before = ic.getTextBeforeCursor(maxOf(repl.length + 20, 100), 0)?.toString() ?: ""
            val replWithSpace = "$repl "
            if (before.endsWith(replWithSpace, ignoreCase = true)) {
                ic.deleteSurroundingText(replWithSpace.length, 0)
                ic.commitText(orig, 1)
                lastAutocorrectOriginal = null
                lastAutocorrectReplacement = null
                undoneAutocorrectWord = orig.lowercase()
                updateSuggestions()
                return
            } else if (before.endsWith(repl, ignoreCase = true)) {
                ic.deleteSurroundingText(repl.length, 0)
                ic.commitText(orig, 1)
                lastAutocorrectOriginal = null
                lastAutocorrectReplacement = null
                undoneAutocorrectWord = orig.lowercase()
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
            R.id.num_key_plus to "+",
            R.id.num_key_minus to "-",
            R.id.num_key_mul to "*",
            R.id.num_key_div to "/",
            R.id.num_key_percent to "%",
            R.id.num_key_space to " ",
            R.id.num_key_comma to ",",
            R.id.num_key_equal to "=",
            R.id.num_key_dot to "."
        )

        for ((id, char) in numKeys) {
            val btn = view.findViewById<View>(id) ?: continue
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
            }
        }

        view.findViewById<View>(R.id.num_key_backspace)?.setOnClickListener {
            playKeyFeedback()
            handleDelete()
        }

        view.findViewById<View>(R.id.num_key_abc)?.setOnClickListener {
            playKeyFeedback()
            showQwertyKeyboard()
        }

        view.findViewById<View>(R.id.num_key_sym)?.setOnClickListener {
            playKeyFeedback()
            numPadContainer?.visibility = View.GONE
            keyboardView?.visibility = View.VISIBLE
            suggestionBarContainer?.visibility = View.VISIBLE
            keyboardView?.keyboard = symbolsKeyboard
            numberRowContainer?.visibility = View.GONE
            isShifted = false
            isShiftLocked = false
            keyboardView?.isShifted = false
            keyboardView?.isShiftLocked = false
            keyboardView?.invalidate()
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
                isFocusable = false
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
                isFocusable = false
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
        val shot = ScreenshotManager.getLatestScreenshot(this)
        if (items.isEmpty() && shot == null) {
            clipboardEmptyView?.visibility = View.VISIBLE
            clipboardScrollView?.visibility = View.GONE
            return
        }

        clipboardEmptyView?.visibility = View.GONE
        clipboardScrollView?.visibility = View.VISIBLE
        val container = clipboardItemsContainer ?: return
        container.removeAllViews()
        val density = resources.displayMetrics.density

        // Recent Screenshot Card at top of clipboard (if available)
        if (shot != null) {
            val shotCard = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 12f * density
                    setColor(Color.parseColor("#2A2E34"))
                    setStroke((1 * density).toInt(), Color.parseColor("#3D424B"))
                }
                setPadding((10 * density).toInt(), (8 * density).toInt(), (10 * density).toInt(), (8 * density).toInt())
                isClickable = true
                isFocusable = false
                setOnClickListener {
                    playKeyFeedback()
                    commitScreenshot(shot.uri)
                    Toast.makeText(this@MyKeyboardService, "Screenshot pasted", Toast.LENGTH_SHORT).show()
                }
            }

            val thumb = ImageView(this).apply {
                val s = (44 * density).toInt()
                layoutParams = LinearLayout.LayoutParams(s, s).apply {
                    marginEnd = (10 * density).toInt()
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        setImageBitmap(contentResolver.loadThumbnail(shot.uri, Size(80, 80), null))
                    } else {
                        setImageURI(shot.uri)
                    }
                } catch (_: Exception) {}
            }

            val textCol = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            val titleTv = TextView(this).apply {
                text = "📸 Recent Screenshot"
                textSize = 13f
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(Color.WHITE)
            }
            val subTv = TextView(this).apply {
                text = "Tap to paste directly into chat"
                textSize = 10.5f
                setTextColor(Color.parseColor("#AAAAAA"))
            }
            textCol.addView(titleTv)
            textCol.addView(subTv)

            val shareBtn = TextView(this).apply {
                text = "↗"
                textSize = 18f
                setTextColor(Color.parseColor("#8E95A0"))
                setPadding((6 * density).toInt(), (4 * density).toInt(), (6 * density).toInt(), (4 * density).toInt())
            }

            shotCard.addView(thumb)
            shotCard.addView(textCol)
            shotCard.addView(shareBtn)

            val shotLp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = (8 * density).toInt()
            }
            container.addView(shotCard, shotLp)
        }

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
                isFocusable = false
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
    // More Menu Implementation (Gboard 2-Column Cards matching Screenshot)
    // -------------------------------------------------------------------------

    private fun setupMoreMenu(view: View) {
        view.findViewById<View>(R.id.btn_more_menu_close)?.setOnClickListener {
            playKeyFeedback()
            showQwertyKeyboard()
        }
    }

    private fun populateMoreMenu() {
        val quickBar = keyboardRoot?.findViewById<LinearLayout>(R.id.more_quick_bar)
        val gridContainer = keyboardRoot?.findViewById<LinearLayout>(R.id.more_grid_container) ?: return
        val subpanel = keyboardRoot?.findViewById<LinearLayout>(R.id.more_subpanel_container) ?: return
        quickBar?.removeAllViews()
        gridContainer.removeAllViews()
        subpanel.visibility = View.GONE
        subpanel.removeAllViews()

        val density = resources.displayMetrics.density

        // Quick Icon Strip (Header)
        val quickActions = listOf(
            Triple("😊", "Emoji") { showEmojiKeyboard() },
            Triple("🎬", "GIF") { showGifKeyboard() },
            Triple("⚙️", "Settings") {
                val intent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            },
            Triple("🌐", "Translate") { showTranslateOptions() },
            Triple("📋", "Clipboard") { showClipboard() },
            Triple("🎙️", "Voice") { startVoiceTyping() }
        )

        for ((icon, _, action) in quickActions) {
            quickBar?.addView(createQuickBarButton(icon, action, density))
        }

        // 2-Column Grid Cards (matching user screenshot)
        val cards = listOf(
            Triple("Theme", "🎨") { showThemesSubpanel(subpanel) },
            Triple("Emoji", "😊") { showEmojiKeyboard() },
            Triple("GIF", "🎬") { showGifKeyboard() },
            Triple("Clipboard", "📋") { showClipboard() },
            Triple("Translate", "🌐") { showTranslateOptions() },
            Triple("Text editing", "✂️") { showCursorSubpanel(subpanel) },
            Triple("AI Write", "✨") { showToneSelector("WRITE") },
            Triple("AI Reply", "💬") { showToneSelector("REPLY") },
            Triple("Fix Grammar", "✓") {
                val text = getTargetText()
                val apiKey = checkOrPromptApiKey()
                if (text != null && apiKey != null) {
                    executeAiTask(text, apiKey) { input, key ->
                        aiService.fixGrammar(input, key)
                    }
                }
            },
            Triple("Num Pad", "🔢") { showNumPad() },
            Triple("One-handed", "📱") { toggleOneHanded() },
            Triple("Settings", "⚙️") {
                val intent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            },
            Triple("Undo", "↩️") { currentInputConnection?.performContextMenuAction(android.R.id.undo); Unit },
            Triple("Redo", "↪️") { currentInputConnection?.performContextMenuAction(android.R.id.redo); Unit }
        )

        for (i in cards.indices step 2) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            val card1 = cards[i]
            row.addView(createMoreMenuCard(card1.first, card1.second, card1.third, density))

            if (i + 1 < cards.size) {
                val card2 = cards[i + 1]
                row.addView(createMoreMenuCard(card2.first, card2.second, card2.third, density))
            } else {
                val spacer = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(0, 0, 1f)
                }
                row.addView(spacer)
            }
            gridContainer.addView(row)
        }
    }

    private fun createQuickBarButton(iconStr: String, action: () -> Any?, density: Float): View {
        return FrameLayout(this).apply {
            val size = (34 * density).toInt()
            val lp = LinearLayout.LayoutParams(size, size).apply {
                marginEnd = (10 * density).toInt()
            }
            layoutParams = lp
            setBackgroundResource(R.drawable.key_pill_background)
            isClickable = true
            isFocusable = false
            setOnClickListener {
                playKeyFeedback()
                action()
            }
            val tv = TextView(this@MyKeyboardService).apply {
                text = iconStr
                textSize = 15f
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            }
            addView(tv)
        }
    }

    private fun createMoreMenuCard(
        title: String,
        iconStr: String,
        action: () -> Any?,
        density: Float
    ): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundResource(R.drawable.more_menu_card_bg)
            setPadding((12 * density).toInt(), (10 * density).toInt(), (12 * density).toInt(), (10 * density).toInt())
            isClickable = true
            isFocusable = false
            setOnClickListener {
                playKeyFeedback()
                action()
            }
            val lp = LinearLayout.LayoutParams(0, (52 * density).toInt(), 1f).apply {
                setMargins((4 * density).toInt(), (4 * density).toInt(), (4 * density).toInt(), (4 * density).toInt())
            }
            layoutParams = lp
        }

        val iconView = TextView(this).apply {
            text = iconStr
            textSize = 18f
            gravity = Gravity.CENTER
            val iconLp = LinearLayout.LayoutParams((28 * density).toInt(), (28 * density).toInt()).apply {
                marginEnd = (10 * density).toInt()
            }
            layoutParams = iconLp
        }

        val titleView = TextView(this).apply {
            text = title
            textSize = 13.5f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.WHITE)
            val titleLp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            layoutParams = titleLp
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
        }

        val chevronView = TextView(this).apply {
            text = "›"
            textSize = 19f
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#8E95A0"))
        }

        card.addView(iconView)
        card.addView(titleView)
        card.addView(chevronView)
        return card
    }

    private fun showCursorSubpanel(subpanel: LinearLayout) {
        subpanel.visibility = View.VISIBLE
        subpanel.removeAllViews()
        val density = resources.displayMetrics.density

        val editingActions = listOf(
            "◀" to { sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT) },
            "▲" to { sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_UP) },
            "▼" to { sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_DOWN) },
            "▶" to { sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_RIGHT) },
            "Select" to { currentInputConnection?.performContextMenuAction(android.R.id.selectAll) },
            "Copy" to { 
                currentInputConnection?.performContextMenuAction(android.R.id.copy)
                Toast.makeText(this@MyKeyboardService, "Copied", Toast.LENGTH_SHORT).show()
            },
            "Cut" to { currentInputConnection?.performContextMenuAction(android.R.id.cut) },
            "Paste" to { currentInputConnection?.performContextMenuAction(android.R.id.paste) }
        )

        for ((label, action) in editingActions) {
            val btn = Button(this).apply {
                text = label
                textSize = if (label.length == 1) 14f else 10f
                setTextColor(Color.WHITE)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 6f * density
                    setColor(Color.parseColor("#3A3A3E"))
                }
                setOnClickListener {
                    playKeyFeedback()
                    action()
                }
            }
            val lp = LinearLayout.LayoutParams(
                if (label.length == 1) (40 * density).toInt() else LinearLayout.LayoutParams.WRAP_CONTENT,
                (34 * density).toInt()
            ).apply {
                marginEnd = (5 * density).toInt()
            }
            subpanel.addView(btn, lp)
        }

        val doneBtn = Button(this).apply {
            text = "✕"
            textSize = 12f
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
        val doneLp = LinearLayout.LayoutParams((36 * density).toInt(), (34 * density).toInt())
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
