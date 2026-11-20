package com.cleverkeyboard.app.keyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import com.cleverkeyboard.app.R
import com.cleverkeyboard.app.data.KeyboardTheme

@Suppress("DEPRECATION")
class CustomKeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : KeyboardView(context, attrs, defStyleAttr) {

    private var currentTheme: KeyboardTheme = KeyboardTheme.DARK_THEME

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val keyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val accentKeyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val functionKeyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val keyRect = RectF()
    private val keyShadowRect = RectF()
    private val textBounds = Rect()
    private val keyShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var iconShift: Drawable? = null
    private var iconShiftLocked: Drawable? = null
    private var iconDelete: Drawable? = null
    private var iconEnter: Drawable? = null
    private var iconSearch: Drawable? = null
    private var iconGlobe: Drawable? = null

    var isShiftLocked: Boolean = false
    var isActionSearch: Boolean = false
    var spaceLabel: String = "English (India)"

    // Swipe / Glide Typing (Defaults to false so it never interferes with standard tapping)
    var isSwipeTypingEnabled: Boolean = false
    var onSwipeWordListener: ((List<Char>) -> Unit)? = null

    // Continuous Deletion & Long-Press Secondary Characters
    var onContinuousDeleteListener: (() -> Unit)? = null
    var onKeySecondaryCharacter: ((Char) -> Unit)? = null
    var onKeyPreviewListener: ((key: Keyboard.Key?, label: String?, isDown: Boolean) -> Unit)? = null

    private var isHoldingDelete = false
    private val deleteHandler = Handler(Looper.getMainLooper())
    private val deleteRepeatRunnable = object : Runnable {
        override fun run() {
            if (isHoldingDelete) {
                onContinuousDeleteListener?.invoke()
                try {
                    performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                } catch (_: Exception) {}
                deleteHandler.postDelayed(this, 50L)
            }
        }
    }

    private var longPressKey: Keyboard.Key? = null
    private var isKeyLongPressTriggered = false
    private val keyLongPressHandler = Handler(Looper.getMainLooper())
    private val keyLongPressRunnable = Runnable {
        val key = longPressKey
        if (key != null && !isSwiping && !isTrackingSpace) {
            val secondary = getSecondaryCharacterForKey(key)
            if (secondary != null) {
                isKeyLongPressTriggered = true
                try {
                    performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                } catch (_: Exception) {}
                key.pressed = false
                invalidate()
                onKeySecondaryCharacter?.invoke(secondary)
            }
        }
    }

    private var isSwiping: Boolean = false
    private var swipeStartX: Float = 0f
    private var swipeStartY: Float = 0f
    private val swipePath = Path()
    private val swipeVisitedKeys = mutableListOf<Char>()
    private val swipePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    // Spacebar Listeners & State (Cursor Control & Keyboard Switcher)
    var onSpaceLongPressListener: (() -> Unit)? = null
    var onCursorMoveListener: ((direction: Int) -> Unit)? = null
    var isCursorDragActive: Boolean = false
        private set

    private var isTrackingSpace: Boolean = false
    private var spaceDownX: Float = 0f
    private var spaceDownY: Float = 0f
    private var lastSlideX: Float = 0f
    private var spaceAccumulatedDrag: Float = 0f
    private var spaceLongPressTriggered: Boolean = false

    private val spaceHandler = Handler(Looper.getMainLooper())
    private val spaceLongPressRunnable = Runnable {
        if (isTrackingSpace && !isCursorDragActive) {
            spaceLongPressTriggered = true
            try {
                performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            } catch (_: Exception) {}
            val spaceKey = getSpaceKey()
            if (spaceKey != null) {
                spaceKey.pressed = false
            }
            invalidate()
            onSpaceLongPressListener?.invoke()
        }
    }

    private fun getSpaceKey(): Keyboard.Key? {
        return keyboard?.keys?.firstOrNull { it.codes.isNotEmpty() && it.codes[0] == 32 }
    }

    private fun isPointInsideKey(key: Keyboard.Key, x: Float, y: Float): Boolean {
        val density = context.resources.displayMetrics.density
        val padding = 6f * density
        return x >= key.x - padding && x <= key.x + key.width + padding &&
               y >= key.y - padding && y <= key.y + key.height + padding
    }

    private fun findKeyAt(x: Float, y: Float): Keyboard.Key? {
        val keys = keyboard?.keys ?: return null
        // 1. Strict containment
        for (k in keys) {
            if (x >= k.x && x <= k.x + k.width && y >= k.y && y <= k.y + k.height) {
                return k
            }
        }

        // 2. Proximity fallback: find closest key within a tolerance (up to 20dp)
        val density = context.resources.displayMetrics.density
        val maxDistSq = (20f * density) * (20f * density)
        var bestKey: Keyboard.Key? = null
        var bestDistSq = Float.MAX_VALUE

        for (k in keys) {
            val clampedX = x.coerceIn(k.x.toFloat(), (k.x + k.width).toFloat())
            val clampedY = y.coerceIn(k.y.toFloat(), (k.y + k.height).toFloat())
            val dx = x - clampedX
            val dy = y - clampedY
            val distSq = dx * dx + dy * dy
            if (distSq < bestDistSq) {
                bestDistSq = distSq
                bestKey = k
            }
        }

        return if (bestDistSq <= maxDistSq) bestKey else null
    }

    fun getSecondaryCharacterForKey(key: Keyboard.Key): Char? {
        if (key.codes.isEmpty()) return null
        return when (key.codes[0].toChar().lowercaseChar()) {
            'q' -> '%'
            'w' -> '\''
            'e' -> '|'
            'r' -> '='
            't' -> '['
            'y' -> ']'
            'u' -> '<'
            'i' -> '>'
            'o' -> '{'
            'p' -> '}'
            'a' -> '@'
            's' -> '#'
            'd' -> '₹'
            'f' -> '_'
            'g' -> '&'
            'h' -> '-'
            'j' -> '+'
            'k' -> '('
            'l' -> ')'
            'z' -> '*'
            'x' -> '"'
            'c' -> '\''
            'v' -> ':'
            'b' -> ';'
            'n' -> '!'
            'm' -> '?'
            ',' -> '?'
            '.' -> '!'
            else -> null
        }
    }

    private fun findLetterKeyAt(x: Float, y: Float): Keyboard.Key? {
        val key = findKeyAt(x, y) ?: return null
        if (key.codes.isNotEmpty()) {
            val code = key.codes[0]
            if ((code in 65..90) || (code in 97..122) || (code in 2947..3021)) {
                return key
            }
        }
        return null
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // Ensure touch coordinates snap into the nearest key bounds if tapped in gap/padding
                val matchedKey = findKeyAt(event.x, event.y)
                if (matchedKey != null) {
                    val clampedX = event.x.coerceIn((matchedKey.x + 1).toFloat(), (matchedKey.x + matchedKey.width - 1).toFloat())
                    val clampedY = event.y.coerceIn((matchedKey.y + 1).toFloat(), (matchedKey.y + matchedKey.height - 1).toFloat())
                    event.setLocation(clampedX, clampedY)
                }

                val spaceKey = getSpaceKey()
                if (spaceKey != null && isPointInsideKey(spaceKey, event.x, event.y)) {
                    isTrackingSpace = true
                    spaceDownX = event.x
                    spaceDownY = event.y
                    lastSlideX = event.x
                    spaceAccumulatedDrag = 0f
                    spaceLongPressTriggered = false
                    isCursorDragActive = false

                    spaceHandler.removeCallbacks(spaceLongPressRunnable)
                    spaceHandler.postDelayed(spaceLongPressRunnable, 450L)

                    return super.onTouchEvent(event)
                }

                // Check for Delete key (continuous deletion) or character key (secondary character long-press)
                val touchedKey = matchedKey ?: findKeyAt(event.x, event.y)
                if (touchedKey != null && touchedKey.codes.isNotEmpty()) {
                    val code = touchedKey.codes[0]
                    if (code == -5 || code == Keyboard.KEYCODE_DELETE) {
                        isHoldingDelete = true
                        deleteHandler.removeCallbacks(deleteRepeatRunnable)
                        deleteHandler.postDelayed(deleteRepeatRunnable, 380L)
                    } else if (code != 32 && getSecondaryCharacterForKey(touchedKey) != null) {
                        longPressKey = touchedKey
                        isKeyLongPressTriggered = false
                        keyLongPressHandler.removeCallbacks(keyLongPressRunnable)
                        keyLongPressHandler.postDelayed(keyLongPressRunnable, 500L)
                    }

                    // Key Press Popup / Bubble Preview (Shown immediately on touch down)
                    if (code > 0 && code != 32) {
                        val ch = code.toChar()
                        val label = if (isShifted) ch.uppercaseChar().toString() else ch.lowercaseChar().toString()
                        onKeyPreviewListener?.invoke(touchedKey, label, true)
                    }
                }

                if (isSwipeTypingEnabled) {
                    swipeStartX = event.x
                    swipeStartY = event.y
                    isSwiping = false
                    swipePath.reset()
                    swipeVisitedKeys.clear()
                    val startKey = findLetterKeyAt(event.x, event.y)
                    if (startKey != null && startKey.codes.isNotEmpty()) {
                        swipeVisitedKeys.add(startKey.codes[0].toChar())
                        swipePath.moveTo(event.x, event.y)
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isTrackingSpace) {
                    val deltaX = event.x - spaceDownX
                    val deltaY = event.y - spaceDownY
                    val density = context.resources.displayMetrics.density
                    val dragThreshold = 10f * density

                    // If dragged vertically far away, abort space tracking and let normal keyboard touch proceed
                    if (!isCursorDragActive && Math.abs(deltaY) > 35f * density) {
                        isTrackingSpace = false
                        spaceHandler.removeCallbacks(spaceLongPressRunnable)
                        return super.onTouchEvent(event)
                    }

                    if (!isCursorDragActive && Math.abs(deltaX) > dragThreshold) {
                        isCursorDragActive = true
                        spaceHandler.removeCallbacks(spaceLongPressRunnable)
                        try {
                            performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        } catch (_: Exception) {}
                        // Cancel underlying KeyboardView key repeat/press
                        val cancelEvent = MotionEvent.obtain(event).apply { action = MotionEvent.ACTION_CANCEL }
                        super.onTouchEvent(cancelEvent)
                        cancelEvent.recycle()
                        invalidate()
                    }

                    if (isCursorDragActive) {
                        val moveDelta = event.x - lastSlideX
                        spaceAccumulatedDrag += moveDelta
                        lastSlideX = event.x

                        val stepThreshold = 12f * density
                        while (spaceAccumulatedDrag >= stepThreshold) {
                            onCursorMoveListener?.invoke(1) // Move Right
                            try {
                                performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            } catch (_: Exception) {}
                            spaceAccumulatedDrag -= stepThreshold
                        }
                        while (spaceAccumulatedDrag <= -stepThreshold) {
                            onCursorMoveListener?.invoke(-1) // Move Left
                            try {
                                performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            } catch (_: Exception) {}
                            spaceAccumulatedDrag += stepThreshold
                        }

                        return true
                    }
                } else if (isSwipeTypingEnabled && swipeVisitedKeys.isNotEmpty()) {
                    val dx = event.x - swipeStartX
                    val dy = event.y - swipeStartY
                    val density = context.resources.displayMetrics.density
                    val swipeThreshold = 50f * density

                    val currKey = findLetterKeyAt(event.x, event.y)
                    if (currKey != null && currKey.codes.isNotEmpty()) {
                        val ch = currKey.codes[0].toChar()
                        if (swipeVisitedKeys.last().lowercaseChar() != ch.lowercaseChar()) {
                            swipeVisitedKeys.add(ch)
                            // User request: No vibration / haptic feedback during slide typing
                        }
                    }

                    // Only transition to swiping mode if the gesture deliberately moved across 3+ keys past 50dp!
                    if (!isSwiping && (dx * dx + dy * dy) > (swipeThreshold * swipeThreshold) && swipeVisitedKeys.size >= 3) {
                        isSwiping = true
                        keyLongPressHandler.removeCallbacks(keyLongPressRunnable)
                        longPressKey = null
                        onKeyPreviewListener?.invoke(null, null, false)
                        val cancelEvent = MotionEvent.obtain(event).apply { action = MotionEvent.ACTION_CANCEL }
                        super.onTouchEvent(cancelEvent)
                        cancelEvent.recycle()
                    }

                    if (isSwiping) {
                        swipePath.lineTo(event.x, event.y)
                        invalidate()
                        return true
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                spaceHandler.removeCallbacks(spaceLongPressRunnable)
                isHoldingDelete = false
                deleteHandler.removeCallbacks(deleteRepeatRunnable)
                keyLongPressHandler.removeCallbacks(keyLongPressRunnable)
                longPressKey = null

                // Notify preview dismiss
                onKeyPreviewListener?.invoke(null, null, false)

                if (isKeyLongPressTriggered) {
                    isKeyLongPressTriggered = false
                    val cancelEvent = MotionEvent.obtain(event).apply { action = MotionEvent.ACTION_CANCEL }
                    super.onTouchEvent(cancelEvent)
                    cancelEvent.recycle()
                    keyboard?.keys?.forEach { it.pressed = false }
                    invalidate()
                    return true
                }

                if (isTrackingSpace) {
                    val wasCursorDrag = isCursorDragActive
                    val wasLongPress = spaceLongPressTriggered

                    isTrackingSpace = false
                    isCursorDragActive = false
                    spaceLongPressTriggered = false
                    invalidate()

                    if (wasCursorDrag || wasLongPress) {
                        val spaceKey = getSpaceKey()
                        if (spaceKey != null) {
                            spaceKey.pressed = false
                        }
                        invalidate()
                        if (wasLongPress) {
                            val cancelEvent = MotionEvent.obtain(event).apply { action = MotionEvent.ACTION_CANCEL }
                            super.onTouchEvent(cancelEvent)
                            cancelEvent.recycle()
                        }
                        keyboard?.keys?.forEach { it.pressed = false }
                        return true
                    }
                } else if (isSwiping) {
                    isSwiping = false
                    val visited = swipeVisitedKeys.toList()
                    swipeVisitedKeys.clear()
                    swipePath.reset()
                    keyboard?.keys?.forEach { it.pressed = false }
                    invalidate()

                    if (visited.size >= 3) {
                        onSwipeWordListener?.invoke(visited)
                        return true
                    }
                }

                // Snap touch coordinates to matched key if in gap/border on release
                val upKey = findKeyAt(event.x, event.y)
                if (upKey != null) {
                    val clampedX = event.x.coerceIn((upKey.x + 1).toFloat(), (upKey.x + upKey.width - 1).toFloat())
                    val clampedY = event.y.coerceIn((upKey.y + 1).toFloat(), (upKey.y + upKey.height - 1).toFloat())
                    event.setLocation(clampedX, clampedY)
                }

                val handled = super.onTouchEvent(event)
                // Reset pressed state after super dispatch so keys never stay stuck
                keyboard?.keys?.forEach { it.pressed = false }
                invalidate()
                return handled
            }
        }

        return super.onTouchEvent(event)
    }

    init {
        isFocusable = false
        isFocusableInTouchMode = false
        isClickable = true
        isPreviewEnabled = false // Modern look: disable legacy popups

        iconShift = ContextCompat.getDrawable(context, R.drawable.ic_shift)
        iconShiftLocked = ContextCompat.getDrawable(context, R.drawable.ic_shift_locked)
        iconDelete = ContextCompat.getDrawable(context, R.drawable.ic_backspace)
        iconEnter = ContextCompat.getDrawable(context, R.drawable.ic_enter)
        iconSearch = ContextCompat.getDrawable(context, R.drawable.ic_search)
        iconGlobe = ContextCompat.getDrawable(context, R.drawable.ic_globe)

        borderPaint.strokeWidth = 1f * context.resources.displayMetrics.density
        swipePaint.strokeWidth = 5f * context.resources.displayMetrics.density
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        applyTheme(KeyboardTheme.DARK_THEME)
    }

    fun applyTheme(theme: KeyboardTheme) {
        currentTheme = theme
        val isPhotoTheme = theme.photoPath != null

        if (isPhotoTheme || theme.hasGradient) {
            setBackgroundColor(Color.TRANSPARENT)
            bgPaint.color = Color.TRANSPARENT
        } else {
            setBackgroundColor(theme.keyboardBgColor)
            bgPaint.color = theme.keyboardBgColor
        }

        keyPaint.color = theme.keyBgColor
        functionKeyPaint.color = adjustAlpha(theme.keyBgColor, 0.85f)
        textPaint.color = theme.keyTextColor
        textPaint.typeface = Typeface.create("sans-serif", if (isPhotoTheme) Typeface.BOLD else Typeface.NORMAL)
        accentKeyPaint.color = theme.accentColor
        swipePaint.color = theme.swipeTrailColor ?: adjustAlpha(theme.accentColor, 0.70f)

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val isPhotoTheme = currentTheme.photoPath != null
        val bordersActive = currentTheme.isBordersActive()
        val density = context.resources.displayMetrics.density

        // Draw overall keyboard background if not photo theme
        if (!isPhotoTheme) {
            val kbOpacity = currentTheme.keyboardOpacity.coerceIn(0.20f, 1.0f)
            if (currentTheme.hasGradient) {
                val start = adjustAlpha(currentTheme.gradientStartColor!!, kbOpacity)
                val end = adjustAlpha(currentTheme.gradientEndColor!!, kbOpacity)
                val shader = LinearGradient(
                    0f, 0f, 0f, height.toFloat(),
                    start, end, Shader.TileMode.CLAMP
                )
                bgPaint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
                bgPaint.shader = null
            } else {
                canvas.drawColor(adjustAlpha(currentTheme.keyboardBgColor, kbOpacity))
            }
        }

        val keys = keyboard?.keys ?: return
        val isShifted = keyboard?.isShifted == true

        val isAppleTheme = currentTheme.id.contains("apple", ignoreCase = true)
        val appleKeyRadius = 5f * density

        // Configurable key shape & corner radius slider
        val baseCornerRadius = when (currentTheme.keyShape) {
            KeyboardTheme.KEY_SHAPE_SQUARE -> 2f * density
            KeyboardTheme.KEY_SHAPE_SUPER_ROUNDED -> 13f * density
            KeyboardTheme.KEY_SHAPE_PILL -> 24f * density
            else -> currentTheme.keyCornerRadius * density
        }
        val cornerRadius = if (isAppleTheme) appleKeyRadius else baseCornerRadius
        val paddingHorizontal = if (isAppleTheme) 2.5f * density else (currentTheme.keySpacing * density)
        val paddingVertical = if (isAppleTheme) 3f * density else (currentTheme.keySpacing * 1.25f * density)

        borderPaint.strokeWidth = currentTheme.borderThickness * density
        swipePaint.color = currentTheme.swipeTrailColor ?: currentTheme.accentColor

        for (key in keys) {
            val keyLeft = key.x + paddingHorizontal
            val keyTop = key.y + paddingVertical
            val keyRight = key.x + key.width - paddingHorizontal
            val keyBottom = key.y + key.height - paddingVertical
            keyRect.set(keyLeft, keyTop, keyRight, keyBottom)

            val primaryCode = if (key.codes.isNotEmpty()) key.codes[0] else 0
            val isEnter = primaryCode == Keyboard.KEYCODE_DONE || primaryCode == -4
            val isShift = primaryCode == Keyboard.KEYCODE_SHIFT || primaryCode == -1
            val isDelete = primaryCode == Keyboard.KEYCODE_DELETE || primaryCode == -5
            val isSymbols = primaryCode == Keyboard.KEYCODE_MODE_CHANGE || primaryCode == -2
            val isSymbolShift = primaryCode == -6
            val isNumPadKey = primaryCode == -200
            val isGlobe = primaryCode == -100
            val isEmojiKey = primaryCode == -101 || (key.label != null && key.label == "😃")
            val isSpace = primaryCode == 32
            val isPillKey = isEnter || isSpace || isSymbols || isSymbolShift || isNumPadKey || (isShift && (isShiftLocked || isShifted))

            val pillRadius = if (isAppleTheme) appleKeyRadius else keyRect.height() / 2f
            val drawRadius = if (isAppleTheme) appleKeyRadius else (if (isPillKey) pillRadius else cornerRadius)

            // Optional Key Drop Shadow
            if (currentTheme.keyShadowEnabled || isAppleTheme) {
                val shadowColor = if (isAppleTheme) {
                    if (isEnter) Color.parseColor("#0056B3") else Color.parseColor("#8A8E97")
                } else {
                    if (isColorLight(currentTheme.keyboardBgColor)) Color.argb(80, 0, 0, 0) else Color.argb(140, 0, 0, 0)
                }
                keyShadowPaint.color = shadowColor
                keyShadowRect.set(keyRect.left, keyRect.top + 1f * density, keyRect.right, keyRect.bottom + 1.3f * density)
                canvas.drawRoundRect(keyShadowRect, drawRadius, drawRadius, keyShadowPaint)
            }

            // 1. Draw Key Background
            if (isAppleTheme) {
                if (isEnter) {
                    // Enter / Action Key: Apple iOS Signature Blue (#007AFF)
                    keyPaint.color = if (key.pressed) (currentTheme.keyPressedColor ?: Color.parseColor("#0056B3")) else Color.parseColor("#007AFF")
                    canvas.drawRoundRect(keyRect, appleKeyRadius, appleKeyRadius, keyPaint)
                } else if (isSpace) {
                    // Spacebar: Pure White (#FFFFFF)
                    val spaceBg = if (isCursorDragActive) Color.parseColor("#007AFF") else (if (key.pressed) (currentTheme.keyPressedColor ?: Color.parseColor("#D8DBE2")) else (currentTheme.functionKeyColor ?: Color.WHITE))
                    keyPaint.color = spaceBg
                    canvas.drawRoundRect(keyRect, appleKeyRadius, appleKeyRadius, keyPaint)
                } else if (isSymbols || isSymbolShift || isNumPadKey || isShift || isDelete || isGlobe) {
                    // Function Keys: Soft slate grey (#B0B5BF), turns white on press / active
                    val fnBase = currentTheme.functionKeyColor ?: Color.parseColor("#B0B5BF")
                    val isShiftActive = isShift && (isShiftLocked || isShifted)
                    val fnColor = if (isShiftActive || key.pressed) (currentTheme.keyPressedColor ?: Color.WHITE) else fnBase
                    keyPaint.color = fnColor
                    canvas.drawRoundRect(keyRect, appleKeyRadius, appleKeyRadius, keyPaint)
                } else {
                    // Standard letter keys: Crisp Pure White (#FFFFFF), soft grey on press
                    var letterBg = if (key.pressed) (currentTheme.keyPressedColor ?: Color.parseColor("#D8DBE2")) else Color.WHITE
                    if (currentTheme.keyOpacity < 1.0f) {
                        letterBg = adjustAlpha(letterBg, currentTheme.keyOpacity)
                    }
                    keyPaint.color = letterBg
                    canvas.drawRoundRect(keyRect, appleKeyRadius, appleKeyRadius, keyPaint)
                }
            } else if (isEnter) {
                // Enter / Search Key: prominent theme accent pill
                val enterColor = if (key.pressed && currentTheme.keyPressedColor != null) currentTheme.keyPressedColor!! else currentTheme.accentColor
                keyPaint.color = if (currentTheme.keyOpacity < 1.0f) adjustAlpha(enterColor, currentTheme.keyOpacity) else enterColor
                canvas.drawRoundRect(keyRect, pillRadius, pillRadius, keyPaint)
            } else if (isSpace) {
                // Spacebar: Rounded pill background
                val spaceColor = if (isCursorDragActive) {
                    currentTheme.accentColor
                } else if (key.pressed) {
                    currentTheme.keyPressedColor ?: blendColors(currentTheme.keyBgColor, Color.WHITE, 0.20f)
                } else {
                    currentTheme.functionKeyColor ?: adjustAlpha(currentTheme.keyBgColor, 0.88f)
                }
                keyPaint.color = if (currentTheme.keyOpacity < 1.0f) adjustAlpha(spaceColor, currentTheme.keyOpacity) else spaceColor
                canvas.drawRoundRect(keyRect, pillRadius, pillRadius, keyPaint)
            } else if (isSymbols || isSymbolShift || isNumPadKey || isDelete || isGlobe) {
                // Function Pill Keys (?123, ABC, =\<, 12/34, delete, globe)
                val baseColor = currentTheme.functionKeyColor ?: adjustAlpha(currentTheme.keyBgColor, 0.88f)
                val fnFill = if (key.pressed) (currentTheme.keyPressedColor ?: blendColors(baseColor, Color.WHITE, 0.25f)) else baseColor
                keyPaint.color = if (currentTheme.keyOpacity < 1.0f) adjustAlpha(fnFill, currentTheme.keyOpacity) else fnFill
                canvas.drawRoundRect(keyRect, pillRadius, pillRadius, keyPaint)
            } else if (isShift && (isShiftLocked || isShifted)) {
                // Active Shift Key pill highlight
                val shiftColor = if (isShiftLocked) {
                    blendColors(currentTheme.keyBgColor, currentTheme.accentColor, 0.40f)
                } else {
                    blendColors(currentTheme.keyBgColor, currentTheme.accentColor, 0.20f)
                }
                val shiftFill = if (key.pressed) (currentTheme.keyPressedColor ?: blendColors(shiftColor, Color.WHITE, 0.25f)) else shiftColor
                keyPaint.color = if (currentTheme.keyOpacity < 1.0f) adjustAlpha(shiftFill, currentTheme.keyOpacity) else shiftFill
                canvas.drawRoundRect(keyRect, pillRadius, pillRadius, keyPaint)
            } else if (bordersActive) {
                // ON (Board): Solid background + border
                val baseColor = when {
                    isShift -> currentTheme.functionKeyColor ?: adjustAlpha(currentTheme.keyBgColor, 0.88f)
                    else -> currentTheme.keyBgColor
                }
                val fillColor = if (key.pressed) (currentTheme.keyPressedColor ?: blendColors(baseColor, Color.WHITE, 0.25f)) else baseColor
                keyPaint.color = if (currentTheme.keyOpacity < 1.0f) adjustAlpha(fillColor, currentTheme.keyOpacity) else fillColor
                canvas.drawRoundRect(keyRect, cornerRadius, cornerRadius, keyPaint)

                val borderColor = when {
                    isColorLight(currentTheme.keyBgColor) -> Color.argb(90, 0, 0, 0)
                    else -> Color.argb(120, 220, 220, 220)
                }
                borderPaint.color = borderColor
                canvas.drawRoundRect(keyRect, cornerRadius, cornerRadius, borderPaint)
            } else {
                // OFF (Flat / Borderless): Clean floating characters on background
                // Soft tactile highlight pill when pressed
                if (key.pressed) {
                    val pressColor = currentTheme.keyPressedColor ?: (if (isColorLight(currentTheme.keyBgColor)) Color.argb(75, 0, 0, 0) else Color.argb(60, 255, 255, 255))
                    keyPaint.color = pressColor
                    canvas.drawRoundRect(keyRect, 8f * density, 8f * density, keyPaint)
                }
            }

            // Text / Icon styling: San Francisco Style Font across all themes
            val primaryTextColor = if (isAppleTheme) Color.BLACK else currentTheme.keyTextColor

            if (currentTheme.textShadowEnabled) {
                val shadowColor = if (isColorLight(primaryTextColor)) Color.argb(180, 0, 0, 0) else Color.argb(180, 255, 255, 255)
                textPaint.setShadowLayer(3f * density, 0f, 1.5f * density, shadowColor)
            } else {
                textPaint.clearShadowLayer()
            }
            textPaint.typeface = Typeface.create("sans-serif", if (isPhotoTheme) Typeface.BOLD else Typeface.NORMAL)

            when {
                isShift -> {
                    val icon = if (isShiftLocked) iconShiftLocked else iconShift
                    val iconTint = if (isAppleTheme) Color.BLACK else if (isShiftLocked || isShifted) currentTheme.accentColor else primaryTextColor
                    drawTintedIcon(canvas, icon, keyRect, iconTint)
                }

                isDelete -> {
                    val delTint = if (isAppleTheme) Color.BLACK else primaryTextColor
                    drawTintedIcon(canvas, iconDelete, keyRect, delTint)
                }

                isEnter -> {
                    if (isAppleTheme && !isActionSearch) {
                        textPaint.color = Color.WHITE
                        textPaint.textSize = key.height * 0.42f
                        textPaint.typeface = Typeface.create("sans-serif", Typeface.BOLD)
                        drawCenteredText(canvas, "→", keyRect, textPaint)
                    } else {
                        val icon = if (isActionSearch) iconSearch else iconEnter
                        val enterTint = if (isColorLight(currentTheme.accentColor)) Color.BLACK else Color.WHITE
                        drawTintedIcon(canvas, icon, keyRect, enterTint)
                    }
                }

                isGlobe -> {
                    val globeTint = if (isAppleTheme) Color.BLACK else primaryTextColor
                    drawTintedIcon(canvas, iconGlobe, keyRect, globeTint)
                }

                isEmojiKey -> {
                    textPaint.color = primaryTextColor
                    textPaint.textSize = key.height * 0.38f
                    drawCenteredText(canvas, "😃", keyRect, textPaint)
                }

                isNumPadKey -> {
                    val topText = "12"
                    val bottomText = "34"
                    textPaint.color = primaryTextColor
                    textPaint.textSize = key.height * 0.22f
                    textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    val offset = key.height * 0.15f
                    textPaint.getTextBounds(topText, 0, topText.length, textBounds)
                    canvas.drawText(topText, keyRect.centerX(), keyRect.centerY() - offset - textBounds.exactCenterY(), textPaint)
                    textPaint.getTextBounds(bottomText, 0, bottomText.length, textBounds)
                    canvas.drawText(bottomText, keyRect.centerX(), keyRect.centerY() + offset - textBounds.exactCenterY(), textPaint)
                }

                isSpace -> {
                    if (isCursorDragActive) {
                        textPaint.color = Color.WHITE
                        textPaint.textSize = key.height * 0.32f
                        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        drawCenteredText(canvas, "◀   Cursor   ▶", keyRect, textPaint)
                    } else {
                        textPaint.color = if (isAppleTheme) Color.parseColor("#8E8E93") else primaryTextColor
                        textPaint.textSize = key.height * 0.28f
                        val label = if (isAppleTheme) "space" else (if (spaceLabel.isNotBlank()) spaceLabel else "English")
                        drawCenteredText(canvas, label, keyRect, textPaint)
                    }
                }

                key.label != null -> {
                    var label = key.label.toString()
                    if (isAppleTheme && (label == "?123" || key.codes.contains(-2))) {
                        label = "123"
                    } else if (isShifted && label.length == 1 && label[0].isLetter()) {
                        label = label.uppercase()
                    } else if (!isShifted && label.length == 1 && label[0].isLetter()) {
                        label = label.lowercase()
                    }

                    textPaint.color = primaryTextColor
                    val textSize = if (label.length > 1) key.height * 0.30f else key.height * 0.40f
                    textPaint.textSize = textSize
                    drawCenteredText(canvas, label, keyRect, textPaint)

                    // Secondary character hint at top right (Gboard style - suppressed in Apple theme for pure minimalism)
                    if (!isAppleTheme) {
                        val secondary = getSecondaryCharacterForKey(key)
                        if (secondary != null && !isShifted) {
                            val secPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                                this.textSize = key.height * 0.22f
                                color = adjustAlpha(primaryTextColor, 0.45f)
                                textAlign = Paint.Align.RIGHT
                                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                            }
                            canvas.drawText(
                                secondary.toString(),
                                keyRect.right - 3.5f * density,
                                keyRect.top + key.height * 0.28f,
                                secPaint
                            )
                        }
                    }
                }

                key.icon != null -> {
                    drawTintedIcon(canvas, key.icon, keyRect, primaryTextColor)
                }
            }
        }

        // Draw glowing swipe gesture trail
        if (isSwiping && swipeVisitedKeys.isNotEmpty()) {
            canvas.drawPath(swipePath, swipePaint)
        }
    }

    private fun drawCenteredText(canvas: Canvas, text: String, rect: RectF, paint: Paint) {
        val x = rect.centerX()
        val y = if (text.length == 1 && text[0].isLetter()) {
            // Anchor all letters to a unified typographic baseline using reference character "x" / "X"
            // Prevents descenders (in g, y, j, p, q) from jumping above the row baseline!
            val refChar = if (text[0].isUpperCase()) "X" else "x"
            paint.getTextBounds(refChar, 0, 1, textBounds)
            rect.centerY() - textBounds.exactCenterY()
        } else {
            paint.getTextBounds(text, 0, text.length, textBounds)
            rect.centerY() - textBounds.exactCenterY()
        }
        canvas.drawText(text, x, y, paint)
    }

    private fun drawTintedIcon(canvas: Canvas, drawable: Drawable?, rect: RectF, tintColor: Int) {
        if (drawable == null) return
        val iconSize = (rect.height() * 0.42f).toInt()
        val left = (rect.centerX() - iconSize / 2).toInt()
        val top = (rect.centerY() - iconSize / 2).toInt()
        val right = left + iconSize
        val bottom = top + iconSize

        drawable.setBounds(left, top, right, bottom)
        drawable.setTint(tintColor)
        drawable.draw(canvas)
    }

    private fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = (Color.alpha(color) * factor).toInt().coerceIn(0, 255)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    private fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
        val inverseRatio = 1f - ratio
        val a = (Color.alpha(color1) * inverseRatio) + (Color.alpha(color2) * ratio)
        val r = (Color.red(color1) * inverseRatio) + (Color.red(color2) * ratio)
        val g = (Color.green(color1) * inverseRatio) + (Color.green(color2) * ratio)
        val b = (Color.blue(color1) * inverseRatio) + (Color.blue(color2) * ratio)
        return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }

    private fun isColorLight(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness < 0.5
    }
}
