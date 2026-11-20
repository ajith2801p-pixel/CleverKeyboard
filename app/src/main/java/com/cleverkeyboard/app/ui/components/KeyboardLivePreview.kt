package com.cleverkeyboard.app.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleverkeyboard.app.data.KeyboardTheme
import java.io.File

@Composable
fun KeyboardLivePreview(
    theme: KeyboardTheme,
    modifier: Modifier = Modifier
) {
    val isApple = theme.id.contains("apple", ignoreCase = true)
    val isPhotoTheme = !theme.photoPath.isNullOrBlank()
    val bordersActive = theme.isBordersActive()

    val kbBg = if (isApple) Color(0xFFD1D3D9) else Color(theme.keyboardBgColor)
    val keyBg = when {
        isApple -> Color.White.copy(alpha = theme.keyOpacity.coerceIn(0f, 1f))
        !bordersActive -> Color.Transparent
        else -> Color(theme.keyBgColor).copy(alpha = theme.keyOpacity.coerceIn(0f, 1f))
    }
    val fnKeyBg = when {
        theme.functionKeyColor != null -> Color(theme.functionKeyColor)
        isApple -> Color(0xFFB0B5BF)
        !bordersActive -> Color.Transparent
        else -> Color(theme.keyBgColor).copy(alpha = (0.85f * theme.keyOpacity).coerceIn(0f, 1f))
    }
    val keyText = if (isApple) Color.Black else Color(theme.keyTextColor)
    val accentColor = Color(theme.accentColor)
    val searchBlue = accentColor
    val aiPurple = if (isApple) Color(0xFF007AFF) else Color(0xFF6A5ACD)

    val photoBitmap: android.graphics.Bitmap? = remember(theme.photoPath) {
        val currentPath: String? = theme.photoPath
        if (!currentPath.isNullOrBlank()) {
            val file = File(currentPath)
            if (file.exists()) {
                try {
                    val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeFile(file.absolutePath, boundsOptions)

                    var inSampleSize = 1
                    while (boundsOptions.outWidth / inSampleSize > 800) {
                        inSampleSize *= 2
                    }

                    val decodeOptions = BitmapFactory.Options().apply {
                        this.inSampleSize = inSampleSize
                    }
                    BitmapFactory.decodeFile(file.absolutePath, decodeOptions)
                } catch (e: Exception) {
                    null
                }
            } else null
        } else null
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
    ) {
        // Base background (Photo base, Gradient, or Solid color)
        Box(
            modifier = Modifier
                .matchParentSize()
                .then(
                    if (photoBitmap != null) {
                        Modifier.background(Color.Black)
                    } else if (theme.hasGradient) {
                        Modifier.background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(
                                    Color(theme.gradientStartColor!!),
                                    Color(theme.gradientEndColor!!)
                                )
                            )
                        )
                    } else {
                        Modifier.background(kbBg)
                    }
                )
        )

        // Photo Background Layer + Blur + Overlay
        if (photoBitmap != null) {
            Image(
                bitmap = photoBitmap.asImageBitmap(),
                contentDescription = "Theme Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .alpha(1.0f)
                    .then(if (theme.photoBlur > 0.5f) Modifier.blur(theme.photoBlur.coerceIn(0f, 100f).dp) else Modifier)
            )
            // Overlay tint using keyboardBgColor
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(kbBg.copy(alpha = theme.photoDarkness.coerceIn(0f, 0.80f)))
            )
        }

        val borderColor = if (bordersActive) {
            if (KeyboardTheme.isColorLight(theme.keyBgColor)) Color.Black.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.22f)
        } else Color.Transparent

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(theme.keyboardOpacity.coerceIn(0.20f, 1.0f))
                .padding(top = 8.dp, bottom = 10.dp, start = 6.dp, end = 6.dp)
        ) {
            // =========================================================================
            // ROW 0: AI TOOLBAR (Top Purple Bar - 52dp with Icon + Small Text)
            // Left: 5 buttons (#6A5ACD), Right: 3 buttons (Emoji, ..., Mic)
            // =========================================================================
            val showLabels = theme.showAiToolbarLabels
            val labelColor = if (theme.toolbarTextColor != null) Color(theme.toolbarTextColor) else if (isPhotoTheme) Color.White else keyText

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp, start = 2.dp, end = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left 5 AI buttons: Translate, AI Write, AI Reply, Fix, Lang
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    val leftTools = listOf(
                        Pair("文", "Translate"),
                        Pair("✨", "AI Write"),
                        Pair("↩", "Fix"),
                        Pair("📋", "Clip"),
                        Pair("💬", "Reply")
                    )
                    leftTools.forEach { (icon, label) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(aiPurple),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = icon, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            if (showLabels) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = label,
                                    color = labelColor,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Normal,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Right 3 buttons: Emoji, More, Voice
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    val rightTools = listOf(
                        Triple("😊", "Emoji", if (bordersActive) Color(0xFF2C2C2E) else Color.Transparent),
                        Triple("•••", "More", if (bordersActive) Color(0xFF2C2C2E) else Color.Transparent),
                        Triple("🎤", "Voice", aiPurple)
                    )
                    rightTools.forEach { (icon, label, bg) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(bg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = icon, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            if (showLabels) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = label,
                                    color = labelColor,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Normal,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // ROW 1: SUGGESTION BAR (Always visible: "I", "you", "the")
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val suggBg = if (isApple) Color.White else (if (!bordersActive) Color.Transparent else Color(theme.keyBgColor))
                val suggTextColor = if (theme.toolbarTextColor != null) Color(theme.toolbarTextColor) else keyText
                PreviewSuggestionChip(text = "I", keyBg = suggBg, textColor = suggTextColor, hasBorder = bordersActive || isApple, borderColor = if (isApple) Color(0xFFB8BCC6) else borderColor, modifier = Modifier.weight(1f))
                PreviewSuggestionChip(text = "you", keyBg = suggBg, textColor = suggTextColor, hasBorder = bordersActive || isApple, borderColor = if (isApple) Color(0xFFB8BCC6) else borderColor, isBold = true, modifier = Modifier.weight(1.2f))
                PreviewSuggestionChip(text = "the", keyBg = suggBg, textColor = suggTextColor, hasBorder = bordersActive || isApple, borderColor = if (isApple) Color(0xFFB8BCC6) else borderColor, modifier = Modifier.weight(1f))
            }

            // =========================================================================
            // ROW 2: NUMBER ROW (1 2 3 4 5 6 7 8 9 0)
            // =========================================================================
            if (theme.showNumberRow) {
                val numbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(theme.keySpacing.dp)
                ) {
                    numbers.forEach { num ->
                        PreviewKey(
                            label = num,
                            keyBg = keyBg,
                            keyText = keyText,
                            hasBorder = bordersActive || isApple,
                            borderThickness = theme.borderThickness,
                            cornerRadiusDp = if (isApple) 5f else theme.keyCornerRadius,
                            borderColor = if (isApple) Color(0xFF8A8E97) else borderColor,
                            isBold = isPhotoTheme || !bordersActive,
                            modifier = Modifier.weight(1f),
                            isApple = isApple,
                            hasShadow = theme.keyShadowEnabled || isApple,
                            hasTextShadow = theme.textShadowEnabled
                        )
                    }
                }
            }

            // =========================================================================
            // ROW 3: QWERTY Row (San Francisco lowercase unshifted characters)
            // =========================================================================
            val row1 = if (isApple) {
                listOf(
                    "q" to null, "w" to null, "e" to null, "r" to null, "t" to null,
                    "y" to null, "u" to null, "i" to null, "o" to null, "p" to null
                )
            } else {
                listOf(
                    "q" to "%", "w" to "'", "e" to "|", "r" to "=", "t" to "[",
                    "y" to "]", "u" to "<", "i" to ">", "o" to "{", "p" to "}"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(theme.keySpacing.dp)
            ) {
                row1.forEach { (letter, sec) ->
                    PreviewKey(
                        label = letter,
                        secondary = sec,
                        keyBg = keyBg,
                        keyText = keyText,
                        hasBorder = bordersActive || isApple,
                        borderThickness = theme.borderThickness,
                        cornerRadiusDp = if (isApple) 5f else theme.keyCornerRadius,
                        borderColor = if (isApple) Color(0xFF8A8E97) else borderColor,
                        isBold = isPhotoTheme || !bordersActive,
                        modifier = Modifier.weight(1f),
                        isApple = isApple,
                        hasShadow = theme.keyShadowEnabled || isApple,
                        hasTextShadow = theme.textShadowEnabled
                    )
                }
            }

            // =========================================================================
            // ROW 4: ASDF Row (San Francisco lowercase unshifted characters)
            // =========================================================================
            val row2 = if (isApple) {
                listOf(
                    "a" to null, "s" to null, "d" to null, "f" to null, "g" to null,
                    "h" to null, "j" to null, "k" to null, "l" to null
                )
            } else {
                listOf(
                    "a" to "@", "s" to "#", "d" to "₹", "f" to "_", "g" to "&",
                    "h" to "-", "j" to "+", "k" to "(", "l" to ")"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(theme.keySpacing.dp)
            ) {
                row2.forEach { (letter, sec) ->
                    PreviewKey(
                        label = letter,
                        secondary = sec,
                        keyBg = keyBg,
                        keyText = keyText,
                        hasBorder = bordersActive || isApple,
                        borderThickness = theme.borderThickness,
                        cornerRadiusDp = if (isApple) 5f else theme.keyCornerRadius,
                        borderColor = if (isApple) Color(0xFF8A8E97) else borderColor,
                        isBold = isPhotoTheme || !bordersActive,
                        modifier = Modifier.weight(1f),
                        isApple = isApple,
                        hasShadow = theme.keyShadowEnabled || isApple,
                        hasTextShadow = theme.textShadowEnabled
                    )
                }
            }

            // =========================================================================
            // ROW 5: ZXCV Row [Shift ^] z x c v b n m [Backspace X]
            // =========================================================================
            val row3 = if (isApple) {
                listOf(
                    "z" to null, "x" to null, "c" to null, "v" to null, "b" to null,
                    "n" to null, "m" to null
                )
            } else {
                listOf(
                    "z" to "*", "x" to "\"", "c" to "'", "v" to ":", "b" to ";",
                    "n" to "!", "m" to "?"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(theme.keySpacing.dp)
            ) {
                // Shift
                PreviewKey(
                    label = "⇧",
                    keyBg = if (isApple) fnKeyBg else (if (bordersActive) Color(0xFF3A3A3C) else Color.Transparent),
                    keyText = if (isApple) Color.Black else searchBlue,
                    hasBorder = bordersActive || isApple,
                    borderThickness = theme.borderThickness,
                    cornerRadiusDp = if (isApple) 5f else theme.keyCornerRadius,
                    borderColor = if (isApple) Color(0xFF8A8E97) else borderColor,
                    modifier = Modifier.weight(1.5f),
                    isFunctionKey = true,
                    isBold = true,
                    isApple = isApple,
                    hasShadow = theme.keyShadowEnabled || isApple,
                    hasTextShadow = theme.textShadowEnabled
                )

                row3.forEach { (letter, sec) ->
                    PreviewKey(
                        label = letter,
                        secondary = sec,
                        keyBg = keyBg,
                        keyText = keyText,
                        hasBorder = bordersActive || isApple,
                        borderThickness = theme.borderThickness,
                        cornerRadiusDp = if (isApple) 5f else theme.keyCornerRadius,
                        borderColor = if (isApple) Color(0xFF8A8E97) else borderColor,
                        isBold = isPhotoTheme || !bordersActive,
                        modifier = Modifier.weight(1f),
                        isApple = isApple,
                        hasShadow = theme.keyShadowEnabled || isApple,
                        hasTextShadow = theme.textShadowEnabled
                    )
                }

                // Backspace / Delete
                PreviewKey(
                    label = "⌫",
                    keyBg = fnKeyBg,
                    keyText = keyText,
                    hasBorder = bordersActive || isApple,
                    borderThickness = theme.borderThickness,
                    cornerRadiusDp = if (isApple) 5f else theme.keyCornerRadius,
                    borderColor = if (isApple) Color(0xFF8A8E97) else borderColor,
                    modifier = Modifier.weight(1.5f),
                    isFunctionKey = true,
                    isBold = true,
                    isApple = isApple,
                    hasShadow = theme.keyShadowEnabled || isApple,
                    hasTextShadow = theme.textShadowEnabled
                )
            }

            // =========================================================================
            // ROW 6: Bottom Row [123] [,] [😃] [SPACE] [.] [→ Action]
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(theme.keySpacing.dp)
            ) {
                // 123 / ?123
                PreviewKey(
                    label = if (isApple) "123" else "?123",
                    keyBg = if (isApple) fnKeyBg else Color(theme.keyBgColor).copy(alpha = 0.88f),
                    keyText = keyText,
                    hasBorder = isApple,
                    borderThickness = theme.borderThickness,
                    cornerRadiusDp = if (isApple) 5f else theme.keyCornerRadius,
                    borderColor = if (isApple) Color(0xFF8A8E97) else Color.Transparent,
                    modifier = Modifier.weight(1.4f),
                    isFunctionKey = true,
                    isBold = true,
                    isPill = !isApple,
                    isApple = isApple,
                    hasShadow = theme.keyShadowEnabled || isApple,
                    hasTextShadow = theme.textShadowEnabled
                )

                // Comma (flat)
                PreviewKey(
                    label = ",",
                    keyBg = keyBg,
                    keyText = keyText,
                    hasBorder = bordersActive || isApple,
                    borderThickness = theme.borderThickness,
                    cornerRadiusDp = if (isApple) 5f else theme.keyCornerRadius,
                    borderColor = if (isApple) Color(0xFF8A8E97) else borderColor,
                    isBold = isPhotoTheme || !bordersActive,
                    modifier = Modifier.weight(0.8f),
                    isApple = isApple,
                    hasShadow = theme.keyShadowEnabled || isApple,
                    hasTextShadow = theme.textShadowEnabled
                )

                // 😃 Emoji (flat)
                PreviewKey(
                    label = "😃",
                    keyBg = keyBg,
                    keyText = keyText,
                    hasBorder = bordersActive || isApple,
                    borderThickness = theme.borderThickness,
                    cornerRadiusDp = if (isApple) 5f else theme.keyCornerRadius,
                    borderColor = if (isApple) Color(0xFF8A8E97) else borderColor,
                    modifier = Modifier.weight(0.9f),
                    isFunctionKey = true,
                    isBold = true,
                    isApple = isApple,
                    hasShadow = theme.keyShadowEnabled || isApple,
                    hasTextShadow = theme.textShadowEnabled
                )

                // Spacebar
                val spaceCorner = if (isApple) 5.dp else 17.dp
                Box(
                    modifier = Modifier
                        .weight(4.7f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(spaceCorner))
                        .background(if (isApple) Color.White else (if (theme.functionKeyColor != null) Color(theme.functionKeyColor) else Color(theme.keyBgColor).copy(alpha = 0.88f)))
                        .border(
                            (theme.borderThickness).dp,
                            if (isApple) Color(0xFF8A8E97) else (if (bordersActive) borderColor else Color.Transparent),
                            RoundedCornerShape(spaceCorner)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isApple) "space" else "English",
                        color = if (isApple) Color(0xFF8E8E93) else keyText,
                        fontSize = 11.sp,
                        fontWeight = if (isApple) FontWeight.Normal else (if (isPhotoTheme || !bordersActive) FontWeight.Bold else FontWeight.Normal)
                    )
                }

                // Period (flat)
                PreviewKey(
                    label = ".",
                    keyBg = keyBg,
                    keyText = keyText,
                    hasBorder = bordersActive || isApple,
                    borderThickness = theme.borderThickness,
                    cornerRadiusDp = if (isApple) 5f else theme.keyCornerRadius,
                    borderColor = if (isApple) Color(0xFF8A8E97) else borderColor,
                    isBold = isPhotoTheme || !bordersActive,
                    modifier = Modifier.weight(0.8f),
                    isApple = isApple,
                    hasShadow = theme.keyShadowEnabled || isApple,
                    hasTextShadow = theme.textShadowEnabled
                )

                // Enter / Action Button
                PreviewKey(
                    label = if (isApple) "→" else "↵",
                    keyBg = if (isApple) Color(0xFF007AFF) else accentColor,
                    keyText = Color.White,
                    hasBorder = false,
                    borderThickness = theme.borderThickness,
                    cornerRadiusDp = if (isApple) 5f else theme.keyCornerRadius,
                    borderColor = Color.Transparent,
                    modifier = Modifier.weight(1.4f),
                    isFunctionKey = true,
                    isBold = true,
                    isPill = !isApple,
                    isApple = isApple,
                    hasShadow = theme.keyShadowEnabled || isApple,
                    hasTextShadow = theme.textShadowEnabled
                )
            }

            // Bottom Dismiss Bar Preview (if active)
            val context = LocalContext.current
            if (theme.shouldShowDismissButton(context)) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 36.dp, height = 20.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⌄", color = keyText.copy(alpha = 0.85f), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .size(width = 36.dp, height = 20.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🌐", color = keyText.copy(alpha = 0.6f), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewSuggestionChip(
    text: String,
    keyBg: Color,
    textColor: Color,
    hasBorder: Boolean,
    borderColor: Color,
    modifier: Modifier = Modifier,
    isBold: Boolean = false
) {
    Box(
        modifier = modifier
            .height(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(keyBg)
            .border(if (hasBorder) 1.dp else 0.dp, if (hasBorder) borderColor else Color.Transparent, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun PreviewKey(
    label: String,
    secondary: String? = null,
    keyBg: Color,
    keyText: Color,
    modifier: Modifier = Modifier,
    hasBorder: Boolean = true,
    borderThickness: Float = 1.0f,
    cornerRadiusDp: Float = 8f,
    borderColor: Color = Color.White.copy(alpha = 0.15f),
    isFunctionKey: Boolean = false,
    isBold: Boolean = false,
    isPill: Boolean = false,
    keyShape: String = KeyboardTheme.KEY_SHAPE_ROUNDED,
    isApple: Boolean = false,
    hasShadow: Boolean = false,
    hasTextShadow: Boolean = false
) {
    val corner = if (isApple) 5.dp else when {
        isPill -> 17.dp
        keyShape == KeyboardTheme.KEY_SHAPE_SQUARE -> 2.dp
        keyShape == KeyboardTheme.KEY_SHAPE_SUPER_ROUNDED -> 13.dp
        keyShape == KeyboardTheme.KEY_SHAPE_PILL -> 24.dp
        else -> cornerRadiusDp.dp
    }
    Box(
        modifier = modifier
            .height(34.dp)
            .then(
                if (hasShadow) {
                    Modifier.background(
                        Color.Black.copy(alpha = 0.35f),
                        RoundedCornerShape(corner)
                    )
                } else Modifier
            )
            .clip(RoundedCornerShape(corner))
            .background(keyBg)
            .border(
                if (hasBorder) borderThickness.dp else 0.dp,
                if (hasBorder) borderColor else Color.Transparent,
                RoundedCornerShape(corner)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!secondary.isNullOrBlank()) {
            Text(
                text = secondary,
                color = keyText.copy(alpha = 0.45f),
                fontSize = 7.5.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 1.dp, end = 2.dp)
            )
        }
        Text(
            text = label,
            color = keyText,
            fontSize = if (label.length > 2) 11.sp else 15.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
            fontWeight = if (isFunctionKey || isBold) FontWeight.Bold else FontWeight.Normal,
            style = if (hasTextShadow) {
                androidx.compose.ui.text.TextStyle(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.7f),
                        offset = androidx.compose.ui.geometry.Offset(1f, 2f),
                        blurRadius = 3f
                    )
                )
            } else androidx.compose.ui.text.TextStyle.Default
        )
    }
}
