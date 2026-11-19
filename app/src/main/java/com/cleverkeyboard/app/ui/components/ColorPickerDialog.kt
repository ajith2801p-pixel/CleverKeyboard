package com.cleverkeyboard.app.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cleverkeyboard.app.data.KeyboardTheme

@Composable
fun ColorPickerDialog(
    title: String,
    initialColorInt: Int,
    onColorSelected: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    // Convert initial color to HSV
    val initialHsv = remember(initialColorInt) {
        val hsv = FloatArray(3)
        AndroidColor.colorToHSV(initialColorInt, hsv)
        hsv
    }

    var hue by remember { mutableFloatStateOf(initialHsv[0]) }
    var saturation by remember { mutableFloatStateOf(initialHsv[1]) }
    var value by remember { mutableFloatStateOf(initialHsv[2]) }
    var hexText by remember { mutableStateOf(KeyboardTheme.colorToHex(initialColorInt)) }

    val currentColorInt = remember(hue, saturation, value) {
        AndroidColor.HSVToColor(floatArrayOf(hue, saturation, value))
    }

    // Keep hex input in sync when sliders change
    LaunchedEffect(currentColorInt) {
        hexText = KeyboardTheme.colorToHex(currentColorInt)
    }

    val quickColors = remember {
        listOf(
            "#000000", "#121214", "#1C1C1E", "#2C2C2E", "#3A3A3C", "#555555", "#8E8E93",
            "#0A1931", "#1E3A8A", "#2563EB", "#0284C7", "#0F766E", "#00E5FF", "#38BDF8",
            "#064E3B", "#10B981", "#00FF88", "#F59E0B", "#FFD600", "#EA580C", "#FB923C",
            "#7F1D1D", "#DC2626", "#DB2777", "#7C3AED", "#6C63FF", "#E0E0E0", "#FFFFFF"
        )
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E28)),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF333344)))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Color Preview Comparison: Before vs Selected
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Current", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(initialColorInt))
                                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))
                    Text("➔", color = Color.Gray, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(20.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("New", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(currentColorInt))
                                .border(2.dp, Color.White, RoundedCornerShape(12.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hue Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Hue", style = MaterialTheme.typography.bodyMedium, color = Color.LightGray, modifier = Modifier.width(70.dp))
                    Slider(
                        value = hue,
                        onValueChange = { hue = it },
                        valueRange = 0f..360f,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Saturation Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Saturation", style = MaterialTheme.typography.bodyMedium, color = Color.LightGray, modifier = Modifier.width(70.dp))
                    Slider(
                        value = saturation,
                        onValueChange = { saturation = it },
                        valueRange = 0f..1f,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Value / Brightness Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Brightness", style = MaterialTheme.typography.bodyMedium, color = Color.LightGray, modifier = Modifier.width(70.dp))
                    Slider(
                        value = value,
                        onValueChange = { value = it },
                        valueRange = 0f..1f,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Palette Grid
                Text(
                    "Quick Swatches",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(125.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickColors) { hex ->
                        val swatchColor = AndroidColor.parseColor(hex)
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(swatchColor))
                                .border(
                                    width = if (swatchColor == currentColorInt) 2.dp else 1.dp,
                                    color = if (swatchColor == currentColorInt) Color.White else Color.Black.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                                .clickable {
                                    val newHsv = FloatArray(3)
                                    AndroidColor.colorToHSV(swatchColor, newHsv)
                                    hue = newHsv[0]
                                    saturation = newHsv[1]
                                    value = newHsv[2]
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hex input / display
                OutlinedTextField(
                    value = hexText,
                    onValueChange = { input ->
                        hexText = input
                        if (input.length == 7 && input.startsWith("#")) {
                            try {
                                val parsed = AndroidColor.parseColor(input)
                                val newHsv = FloatArray(3)
                                AndroidColor.colorToHSV(parsed, newHsv)
                                hue = newHsv[0]
                                saturation = newHsv[1]
                                value = newHsv[2]
                            } catch (ignored: Exception) {}
                        }
                    },
                    label = { Text("Hex Code") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6C63FF),
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth(0.6f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onColorSelected(currentColorInt) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF))
                    ) {
                        Text("Select Color", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

