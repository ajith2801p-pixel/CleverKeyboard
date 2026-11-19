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
    private val textBounds = Rect()

    private var iconShift: Drawable? = null
    private var iconShiftLocked: Drawable? = null
    private var iconDelete: Drawable? = null
    private var iconEnter: Drawable? = null
    private var iconSearch: Drawable? = null
    private var iconGlobe: Drawable? = null

    var isShiftLocked: Boolean = false
    var isActionSearch: Boolean = false
    var spaceLabel: String = "English (India)"

    // Swipe / Glide Typing
    var isSwipeTypingEnabled: Boolean = true
    var onSwipeWordListener: ((List<Char>) -> Unit)? = null

    // Continuous Deletion & Long-Press Secondary Characters
    var onContinuousDeleteListener: (() -> Unit)? = null
    var onKeySecondaryCharacter: ((Char) -> Unit)? = null

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
        for (k in keys) {
            if (x >= k.x && x <= k.x + k.width && y >= k.y && y <= k.y + k.height) {
                return k
            }
        }
        return null
    }

    fun getSecondaryCharacterForKey(key: Keyboard.Key): Char? {
        if (key.codes.isEmpty()) return null
        return when (key.codes[0].toChar().lowercaseChar()) {
            'q' -> '1'
            'w' -> '2'
            'e' -> '3'
            'r' -> '4'
            't' -> '5'
            'y' -> '6'
            'u' -> '7'
            'i' -> '8'
            'o' -> '9'
            'p' -> '0'
            'a' -> '@'
            's' -> '#'
            'd' -> '$'
            'f' -> '%'
            'g' -> '&'
            'h' -> '*'
            'j' -> '-'
            'k' -> '+'
            'l' -> '/'
            'z' -> '_'
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
        val keys = keyboard?.keys ?: return null
        for (k in keys) {
            if (x >= k.x && x <= k.x + k.width && y >= k.y && y <= k.y + k.height) {
                if (k.codes.isNotEmpty()) {
                    val code = k.codes[0]
                    if ((code in 65..90) || (code in 97..122) || (code in 2947..3021)) {
                        return k
                    }
                }
            }
        }
        return null
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
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
                val touchedKey = findKeyAt(event.x, event.y)
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
                        keyLongPressHandler.postDelayed(keyLongPressRunnable, 400L)
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
                    val swipeThreshold = 18f * density

                    if (!isSwiping && (dx * dx + dy * dy) > (swipeThreshold * swipeThreshold)) {
                        isSwiping = true
                        keyLongPressHandler.removeCallbacks(keyLongPressRunnable)
                        longPressKey = null
                        val cancelEvent = MotionEvent.obtain(event).apply { action = MotionEvent.ACTION_CANCEL }
                        super.onTouchEvent(cancelEvent)
                        cancelEvent.recycle()
                    }

                    if (isSwiping) {
                        swipePath.lineTo(event.x, event.y)
                        val currKey = findLetterKeyAt(event.x, event.y)
                        if (currKey != null && currKey.codes.isNotEmpty()) {
                            val ch = currKey.codes[0].toChar()
                            if (swipeVisitedKeys.isEmpty() || swipeVisitedKeys.last().lowercaseChar() != ch.lowercaseChar()) {
                                swipeVisitedKeys.add(ch)
                                try {
                                    performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                } catch (_: Exception) {}
                            }
                        }
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

                if (isKeyLongPressTriggered) {
                    isKeyLongPressTriggered = false
                    val cancelEvent = MotionEvent.obtain(event).apply { action = MotionEvent.ACTION_CANCEL }
                    super.onTouchEvent(cancelEvent)
                    cancelEvent.recycle()
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
                        return true
                    }
                } else if (isSwiping) {
                    isSwiping = false
                    val visited = swipeVisitedKeys.toList()
                    swipeVisitedKeys.clear()
                    swipePath.reset()
                    invalidate()

                    if (visited.size >= 2) {
                        onSwipeWordListener?.invoke(visited)
                        return true
                    }
                }
            }
        }

        return super.onTouchEvent(event)
    }

    init {
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
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, if (isPhotoTheme) Typeface.BOLD else Typeface.NORMAL)
        accentKeyPaint.color = theme.accentColor
        swipePaint.color = adjustAlpha(theme.accentColor, 0.70f)

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val isPhotoTheme = currentTheme.photoPath != null
        val bordersActive = currentTheme.isBordersActive()
        val density = context.resources.displayMetrics.density

        // Draw overall keyboard background if not photo theme
        if (!isPhotoTheme) {
            if (currentTheme.hasGradient) {
                val start = currentTheme.gradientStartColor!!
                val end = currentTheme.gradientEndColor!!
                val shader = LinearGradient(
                    0f, 0f, 0f, height.toFloat(),
                    start, end, Shader.TileMode.CLAMP
                )
                bgPaint.shader = shader
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
                bgPaint.shader = null
            } else {
                canvas.drawColor(currentTheme.keyboardBgColor)
            }
        }

        val keys = keyboard?.keys ?: return
        val isShifted = keyboard?.isShifted == true

        // Configurable key shape: Square, Rounded, Super Rounded, Pill
        val baseCornerRadius = when (currentTheme.keyShape) {
            KeyboardTheme.KEY_SHAPE_SQUARE -> 2f * density
            KeyboardTheme.KEY_SHAPE_SUPER_ROUNDED -> 13f * density
            KeyboardTheme.KEY_SHAPE_PILL -> 24f * density
            else -> 8f * density
        }
        val cornerRadius = baseCornerRadius
        val paddingHorizontal = 2f * density
        val paddingVertical = 2.5f * density

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
            val isGlobe = primaryCode == -100
            val isSpace = primaryCode == 32

            // Enter / Search Key: Always prominent accent background
            if (isEnter) {
                keyPaint.color = currentTheme.accentColor
                canvas.drawRoundRect(keyRect, cornerRadius, cornerRadius, keyPaint)
            } else if (isSpace && isCursorDragActive) {
                // Highlight spacebar during cursor trackpad gesture
                keyPaint.color = currentTheme.accentColor
                canvas.drawRoundRect(keyRect, cornerRadius, cornerRadius, keyPaint)
            } else if (bordersActive) {
                // ON (Board): Solid background + 1dp border, Gboard style
                val baseColor = when {
                    isShift && isShiftLocked -> blendColors(currentTheme.keyBgColor, currentTheme.accentColor, 0.40f)
                    isShift && isShifted -> blendColors(currentTheme.keyBgColor, currentTheme.accentColor, 0.20f)
                    isShift || isDelete || isSymbols || isGlobe -> adjustAlpha(currentTheme.keyBgColor, 0.88f)
                    else -> currentTheme.keyBgColor
                }

                val fillColor = if (key.pressed) {
                    blendColors(baseColor, Color.WHITE, 0.25f)
                } else baseColor

                keyPaint.color = fillColor
                canvas.drawRoundRect(keyRect, cornerRadius, cornerRadius, keyPaint)

                // Key Borders: 1dp outline
                val borderColor = when {
                    isColorLight(currentTheme.keyBgColor) -> Color.argb(50, 0, 0, 0)
                    else -> Color.argb(70, 120, 120, 120)
                }
                borderPaint.color = borderColor
                canvas.drawRoundRect(keyRect, cornerRadius, cornerRadius, borderPaint)
            } else {
                // OFF (Flat): Keys have NO border, transparent / flat keys, only text visible on keyboard background.
                // When pressed, show subtle translucent touch feedback.
                if (key.pressed) {
                    val pressColor = if (isColorLight(currentTheme.keyBgColor)) Color.argb(80, 0, 0, 0) else Color.argb(60, 255, 255, 255)
                    keyPaint.color = pressColor
                    canvas.drawRoundRect(keyRect, cornerRadius, cornerRadius, keyPaint)
                }
            }

            // Text / Icon styling: Uses currentTheme.keyTextColor
            val primaryTextColor = currentTheme.keyTextColor

            textPaint.clearShadowLayer()
            textPaint.typeface = if (isPhotoTheme) {
                Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            } else {
                Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }

            when {
                isShift -> {
                    val icon = if (isShiftLocked) iconShiftLocked else iconShift
                    val iconTint = if (isShiftLocked || isShifted) currentTheme.accentColor else primaryTextColor
                    drawTintedIcon(canvas, icon, keyRect, iconTint)
                }

                isDelete -> {
                    drawTintedIcon(canvas, iconDelete, keyRect, primaryTextColor)
                }

                isEnter -> {
                    val icon = if (isActionSearch) iconSearch else iconEnter
                    val enterTint = if (isColorLight(currentTheme.accentColor)) Color.BLACK else Color.WHITE
                    drawTintedIcon(canvas, icon, keyRect, enterTint)
                }

                isGlobe -> {
                    drawTintedIcon(canvas, iconGlobe, keyRect, primaryTextColor)
                }

                isSpace -> {
                    if (isCursorDragActive) {
                        textPaint.color = Color.WHITE
                        textPaint.textSize = key.height * 0.32f
                        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        drawCenteredText(canvas, "◀   Cursor   ▶", keyRect, textPaint)
                    } else {
                        textPaint.color = primaryTextColor
                        textPaint.textSize = key.height * 0.28f
                        val label = if (spaceLabel.isNotBlank()) spaceLabel else "English (India)"
                        drawCenteredText(canvas, label, keyRect, textPaint)
                    }
                }

                key.label != null -> {
                    var label = key.label.toString()
                    if (isShifted && label.length == 1 && label[0].isLetter()) {
                        label = label.uppercase()
                    }

                    textPaint.color = primaryTextColor
                    val textSize = if (label.length > 1) key.height * 0.30f else key.height * 0.40f
                    textPaint.textSize = textSize
                    drawCenteredText(canvas, label, keyRect, textPaint)

                    // Secondary character hint at top right (Gboard style)
                    val secondary = getSecondaryCharacterForKey(key)
                    if (secondary != null && !isShifted) {
                        val secPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            this.textSize = key.height * 0.22f
                            color = adjustAlpha(primaryTextColor, 0.42f)
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
        paint.getTextBounds(text, 0, text.length, textBounds)
        val x = rect.centerX()
        val y = rect.centerY() - textBounds.exactCenterY()
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
