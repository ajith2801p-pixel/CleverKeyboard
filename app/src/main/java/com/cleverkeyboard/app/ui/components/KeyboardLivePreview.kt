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
    val isPhotoTheme = !theme.photoPath.isNullOrBlank()
    val bordersActive = theme.isBordersActive()

    val kbBg = Color(theme.keyboardBgColor)
    val keyBg = when {
        !bordersActive -> Color.Transparent
        else -> Color(theme.keyBgColor)
    }
    val fnKeyBg = when {
        !bordersActive -> Color.Transparent
        else -> Color(theme.keyBgColor).copy(alpha = 0.85f)
    }
    val keyText = Color(theme.keyTextColor)
    val accentColor = Color(theme.accentColor)
    val searchBlue = accentColor
    val aiPurple = Color(0xFF6A5ACD)

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
            if (KeyboardTheme.isColorLight(theme.keyBgColor)) Color.Black.copy(alpha = 0.20f) else Color.White.copy(alpha = 0.18f)
        } else Color.Transparent

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 10.dp, start = 6.dp, end = 6.dp)
        ) {
            // =========================================================================
            // ROW 0: AI TOOLBAR (Top Purple Bar - 52dp with Icon + Small Text)
            // Left: 5 buttons (#6A5ACD), Right: 3 buttons (Emoji, ..., Mic)
            // =========================================================================
            val showLabels = theme.showAiToolbarLabels
            val labelColor = if (isPhotoTheme) Color.White else keyText

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
                val suggBg = if (!bordersActive) Color.Transparent else Color(theme.keyBgColor)
                PreviewSuggestionChip(text = "I", keyBg = suggBg, textColor = keyText, hasBorder = bordersActive, borderColor = borderColor, modifier = Modifier.weight(1f))
                PreviewSuggestionChip(text = "you", keyBg = suggBg, textColor = keyText, hasBorder = bordersActive, borderColor = borderColor, isBold = true, modifier = Modifier.weight(1.2f))
                PreviewSuggestionChip(text = "the", keyBg = suggBg, textColor = keyText, hasBorder = bordersActive, borderColor = borderColor, modifier = Modifier.weight(1f))
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
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    numbers.forEach { num ->
                        PreviewKey(
                            label = num,
                            keyBg = keyBg,
                            keyText = keyText,
                            hasBorder = bordersActive,
                            borderColor = borderColor,
                            isBold = isPhotoTheme || !bordersActive,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // =========================================================================
            // ROW 3: QWERTY Row (Auto Capital starts with Q W E R T Y U I O P)
            // =========================================================================
            val row1 = listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                row1.forEach { letter ->
                    PreviewKey(
                        label = letter,
                        keyBg = keyBg,
                        keyText = keyText,
                        hasBorder = bordersActive,
                        borderColor = borderColor,
                        isBold = isPhotoTheme || !bordersActive,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // =========================================================================
            // ROW 4: ASDF Row (Centered)
            // =========================================================================
            val row2 = listOf("A", "S", "D", "F", "G", "H", "J", "K", "L")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp, horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                row2.forEach { letter ->
                    PreviewKey(
                        label = letter,
                        keyBg = keyBg,
                        keyText = keyText,
                        hasBorder = bordersActive,
                        borderColor = borderColor,
                        isBold = isPhotoTheme || !bordersActive,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // =========================================================================
            // ROW 5: ZXCV Row [Shift ^] Z X C V B N M [Backspace X]
            // =========================================================================
            val row3 = listOf("Z", "X", "C", "V", "B", "N", "M")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                // Shift (Active/highlighted)
                PreviewKey(
                    label = "⇧",
                    keyBg = if (bordersActive) Color(0xFF3A3A3C) else Color.Transparent,
                    keyText = searchBlue,
                    hasBorder = bordersActive,
                    borderColor = borderColor,
                    modifier = Modifier.weight(1.5f),
                    isFunctionKey = true,
                    isBold = true
                )

                row3.forEach { letter ->
                    PreviewKey(
                        label = letter,
                        keyBg = keyBg,
                        keyText = keyText,
                        hasBorder = bordersActive,
                        borderColor = borderColor,
                        isBold = isPhotoTheme || !bordersActive,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Backspace / Delete
                PreviewKey(
                    label = "⌫",
                    keyBg = fnKeyBg,
                    keyText = keyText,
                    hasBorder = bordersActive,
                    borderColor = borderColor,
                    modifier = Modifier.weight(1.5f),
                    isFunctionKey = true,
                    isBold = true
                )
            }

            // =========================================================================
            // ROW 6: Bottom Row [?123] [,] [Globe] [SPACE] [.] [Search Blue Button]
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                // ?123
                PreviewKey(
                    label = "?123",
                    keyBg = fnKeyBg,
                    keyText = keyText,
                    hasBorder = bordersActive,
                    borderColor = borderColor,
                    modifier = Modifier.weight(1.2f),
                    isFunctionKey = true,
                    isBold = true
                )

                // Comma
                PreviewKey(
                    label = ",",
                    keyBg = keyBg,
                    keyText = keyText,
                    hasBorder = bordersActive,
                    borderColor = borderColor,
                    isBold = isPhotoTheme || !bordersActive,
                    modifier = Modifier.weight(0.8f)
                )

                // Globe
                PreviewKey(
                    label = "🌐",
                    keyBg = fnKeyBg,
                    keyText = keyText,
                    hasBorder = bordersActive,
                    borderColor = borderColor,
                    modifier = Modifier.weight(1.0f),
                    isFunctionKey = true,
                    isBold = true
                )

                // Spacebar ("English (India)")
                Box(
                    modifier = Modifier
                        .weight(5.0f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(keyBg)
                        .border(
                            if (bordersActive) 1.dp else 0.dp,
                            if (bordersActive) borderColor else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "English (India)",
                        color = keyText,
                        fontSize = 11.sp,
                        fontWeight = if (isPhotoTheme || !bordersActive) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // Period
                PreviewKey(
                    label = ".",
                    keyBg = keyBg,
                    keyText = keyText,
                    hasBorder = bordersActive,
                    borderColor = borderColor,
                    isBold = isPhotoTheme || !bordersActive,
                    modifier = Modifier.weight(0.8f)
                )

                // Search / Enter Button
                PreviewKey(
                    label = "🔍",
                    keyBg = accentColor,
                    keyText = if (KeyboardTheme.isColorLight(theme.accentColor)) Color.Black else Color.White,
                    hasBorder = false,
                    borderColor = Color.Transparent,
                    modifier = Modifier.weight(1.2f),
                    isFunctionKey = true,
                    isBold = true
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
    keyBg: Color,
    keyText: Color,
    modifier: Modifier = Modifier,
    hasBorder: Boolean = true,
    borderColor: Color = Color.White.copy(alpha = 0.15f),
    isFunctionKey: Boolean = false,
    isBold: Boolean = false,
    keyShape: String = KeyboardTheme.KEY_SHAPE_ROUNDED
) {
    val corner = when (keyShape) {
        KeyboardTheme.KEY_SHAPE_SQUARE -> 2.dp
        KeyboardTheme.KEY_SHAPE_SUPER_ROUNDED -> 13.dp
        KeyboardTheme.KEY_SHAPE_PILL -> 24.dp
        else -> 8.dp
    }
    Box(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(corner))
            .background(keyBg)
            .border(
                if (hasBorder) 1.dp else 0.dp,
                if (hasBorder) borderColor else Color.Transparent,
                RoundedCornerShape(corner)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = keyText,
            fontSize = if (label.length > 2) 11.sp else 15.sp,
            fontWeight = if (isFunctionKey || isBold) FontWeight.Bold else FontWeight.Normal
        )
    }
}
