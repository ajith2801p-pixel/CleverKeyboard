package com.cleverkeyboard.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleverkeyboard.app.data.KeyboardTheme
import com.cleverkeyboard.app.ui.components.ColorPickerDialog
import com.cleverkeyboard.app.ui.components.KeyboardLivePreview
import com.cleverkeyboard.app.ui.theme.*
import com.cleverkeyboard.app.ui.viewmodel.ColorTarget
import com.cleverkeyboard.app.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)

        setContent {
            CleverKeyboardTheme {
                ModernAppScreen(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshKeyboardStatus()
    }

    private fun handleIntent(intent: Intent?) {
        when (intent?.getStringExtra("extra_screen")) {
            "themes" -> viewModel.selectTab(1)
            "settings" -> viewModel.selectTab(0)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ModernAppScreen(viewModel: SettingsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    // Sub-page state: null = root menu, or "setup", "typing", "ai", "layout", "sound", "privacy", "about"
    var currentSubPage by remember { mutableStateOf<String?>(null) }

    // Category filter on Themes tab
    var selectedThemeCategory by remember { mutableStateOf("All") }

    // HorizontalPager state for sliding smoothly between Settings (0) and Themes (1)
    val pagerState = rememberPagerState(initialPage = uiState.selectedTab, pageCount = { 2 })

    // Sync Pager state with ViewModel selectedTab
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != uiState.selectedTab) {
            viewModel.selectTab(pagerState.currentPage)
        }
    }

    LaunchedEffect(uiState.selectedTab) {
        if (pagerState.currentPage != uiState.selectedTab) {
            pagerState.animateScrollToPage(uiState.selectedTab)
        }
    }

    // Android Hardware/System Navigation Back Button Handling:
    // If inside a sub-page, back press goes back to root menu without exiting the app.
    // If at root menu, back press exits the app naturally.
    BackHandler(enabled = currentSubPage != null) {
        currentSubPage = null
    }

    // Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val oldFiles = context.filesDir.listFiles { _, name -> name.startsWith("custom_keyboard_bg") }
                oldFiles?.forEach { it.delete() }

                val destinationFile = File(context.filesDir, "custom_keyboard_bg_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    destinationFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                viewModel.setPhotoTheme(
                    photoPath = destinationFile.absolutePath,
                    darkness = 0.40f,
                    blur = 25f
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Color Picker Dialog
    uiState.activeColorTarget?.let { target ->
        val title = when (target) {
            ColorTarget.KEYBOARD_BG -> "Keyboard Background (Overlay Tint)"
            ColorTarget.KEY_BG -> "Key Background Color"
            ColorTarget.KEY_TEXT -> "Key Text Color"
            ColorTarget.ACCENT -> "Accent Color (AI & Enter)"
        }
        val initialColor = when (target) {
            ColorTarget.KEYBOARD_BG -> uiState.currentTheme.keyboardBgColor
            ColorTarget.KEY_BG -> uiState.currentTheme.keyBgColor
            ColorTarget.KEY_TEXT -> uiState.currentTheme.keyTextColor
            ColorTarget.ACCENT -> uiState.currentTheme.accentColor
        }

        ColorPickerDialog(
            title = title,
            initialColorInt = initialColor,
            onColorSelected = { viewModel.onColorSelected(it) },
            onDismissRequest = { viewModel.closeColorPicker() }
        )
    }

    // App Background Gradient (Modern Obsidian / Dark Slate)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(ModernBgStart, ModernBgEnd, ModernBgStart)
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    if (currentSubPage == null) {
                        // Top Branding & Pro Badge
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(ModernCyan, ModernBlue)
                                            )
                                        )
                                        .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = ModernCyan),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("✨", fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "CleverKeyboard",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "AI Keyboard • Voice • Themes",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = ModernCyan
                                    )
                                }
                            }

                            // 👑 Pro Badge
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = ModernGold.copy(alpha = 0.20f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ModernGold)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("👑", fontSize = 12.sp)
                                    Text(
                                        text = "Pro",
                                        color = ModernGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Navigation Pills: [ ⚙️ Settings ] and [ 🎨 Themes ]
                        // Tapping either slides smoothly to that page
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(23.dp))
                                .background(Color(0xFF0C1824))
                                .border(1.dp, ModernCardStroke, RoundedCornerShape(23.dp))
                                .padding(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Settings Tab Pill
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(20.dp))
                                    .then(
                                        if (pagerState.currentPage == 0) {
                                            Modifier
                                                .background(
                                                    Brush.horizontalGradient(
                                                        listOf(ModernCyan, ModernBlue)
                                                    )
                                                )
                                                .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = ModernCyan)
                                        } else {
                                            Modifier.clickable {
                                                coroutineScope.launch {
                                                    pagerState.animateScrollToPage(0)
                                                }
                                            }
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("⚙️", fontSize = 14.sp)
                                    Text(
                                        text = "Settings",
                                        fontSize = 14.sp,
                                        fontWeight = if (pagerState.currentPage == 0) FontWeight.Bold else FontWeight.Medium,
                                        color = if (pagerState.currentPage == 0) Color.White else TextSecondary
                                    )
                                }
                            }

                            // Themes Tab Pill
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(20.dp))
                                    .then(
                                        if (pagerState.currentPage == 1) {
                                            Modifier
                                                .background(
                                                    Brush.horizontalGradient(
                                                        listOf(ModernCyan, ModernBlue)
                                                    )
                                                )
                                                .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = ModernCyan)
                                        } else {
                                            Modifier.clickable {
                                                coroutineScope.launch {
                                                    pagerState.animateScrollToPage(1)
                                                }
                                            }
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("🎨", fontSize = 14.sp)
                                    Text(
                                        text = "Themes",
                                        fontSize = 14.sp,
                                        fontWeight = if (pagerState.currentPage == 1) FontWeight.Bold else FontWeight.Medium,
                                        color = if (pagerState.currentPage == 1) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    } else {
                        // Sub-Page Navigation Bar with Back Arrow
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { currentSubPage = null },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ModernCardBg)
                                    .border(1.dp, ModernCardStroke, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = when (currentSubPage) {
                                    "setup" -> "Keyboard Setup"
                                    "typing" -> "Typing & Input"
                                    "languages" -> "Languages"
                                    "ai" -> "AI Features"
                                    "voice" -> "Voice & Dictation"
                                    "layout" -> "Layout & Keys"
                                    "clipboard" -> "Clipboard & Snippets"
                                    "sound" -> "Sound & Haptic"
                                    "privacy" -> "Privacy & Data"
                                    "dictionary" -> "Dictionary & Vocabulary"
                                    "about" -> "About"
                                    else -> "Settings"
                                },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // If user is inside a sub-page, display the sub-page
                if (currentSubPage != null) {
                    when (currentSubPage) {
                        "setup" -> KeyboardSetupSubPage(viewModel, uiState)
                        "typing" -> TypingAndInputSubPage(viewModel, uiState)
                        "languages" -> LanguagesSubPage(viewModel, uiState)
                        "ai" -> AiFeaturesSubPage(viewModel, uiState, keyboardController)
                        "voice" -> VoiceSubPage()
                        "layout" -> LayoutAndKeysSubPage(viewModel, uiState)
                        "clipboard" -> ClipboardSubPage(context)
                        "sound" -> SoundAndHapticSubPage(viewModel, uiState)
                        "privacy" -> PrivacySubPage(context)
                        "dictionary" -> DictionarySubPage(context)
                        "about" -> AboutSubPage()
                    }
                } else {
                    // Horizontal Pager: Allows user to SLIDE / SWIPE horizontally between Settings and Themes!
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        userScrollEnabled = currentSubPage == null
                    ) { pageIndex ->
                        if (pageIndex == 0) {
                            // Settings Screen (Screen 1 from mockup)
                            SettingsRootScreen(
                                uiState = uiState,
                                onNavigate = { pageId -> currentSubPage = pageId }
                            )
                        } else {
                            // Themes Screen (Screen 3 from mockup)
                            ThemesScreen(
                                viewModel = viewModel,
                                uiState = uiState,
                                selectedCategory = selectedThemeCategory,
                                onSelectCategory = { selectedThemeCategory = it },
                                onPickPhoto = { photoPickerLauncher.launch("image/*") }
                            )
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// SCREEN 1: SETTINGS ROOT SCREEN (Cleaned: Only Functional Settings, No Static Text Rows)
// =============================================================================

@Composable
fun SettingsRootScreen(
    uiState: com.cleverkeyboard.app.ui.viewmodel.SettingsUiState,
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                ModernSettingsRow(
                    icon = "⌨️",
                    title = "Keyboard Setup",
                    subtitle = "Enable, select & basic setup",
                    trailingBadge = if (uiState.isKeyboardEnabled && uiState.isKeyboardSelected) "✓" else null,
                    badgeColor = SuccessGreen,
                    onClick = { onNavigate("setup") }
                )
                HorizontalDivider(color = ModernCardStroke.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                ModernSettingsRow(
                    icon = "A",
                    isKeycapIcon = true,
                    title = "Typing & Input",
                    subtitle = "Auto-correct, prediction & capitalization",
                    onClick = { onNavigate("typing") }
                )
                HorizontalDivider(color = ModernCardStroke.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                ModernSettingsRow(
                    icon = "🌐",
                    title = "Languages",
                    subtitle = "English, தமிழ் (Tamil) & Tanglish transliteration",
                    trailingBadge = uiState.activeLanguage,
                    badgeColor = ModernCyan,
                    onClick = { onNavigate("languages") }
                )
                HorizontalDivider(color = ModernCardStroke.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                ModernSettingsRow(
                    icon = "✨",
                    title = "AI Features",
                    subtitle = "Gemini AI, writing assist & toolbar labels",
                    trailingBadge = if (uiState.hasApiKey) "Active" else "Add Key",
                    badgeColor = if (uiState.hasApiKey) SuccessGreen else ModernCyan,
                    onClick = { onNavigate("ai") }
                )
                HorizontalDivider(color = ModernCardStroke.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                ModernSettingsRow(
                    icon = "🎙️",
                    title = "Voice & Dictation",
                    subtitle = "Speech typing with live audio waveform",
                    onClick = { onNavigate("voice") }
                )
                HorizontalDivider(color = ModernCardStroke.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                ModernSettingsRow(
                    icon = "🎛️",
                    title = "Layout & Keys",
                    subtitle = "Key shape, number row, height & dismiss button",
                    onClick = { onNavigate("layout") }
                )
                HorizontalDivider(color = ModernCardStroke.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                ModernSettingsRow(
                    icon = "📋",
                    title = "Clipboard & Snippets",
                    subtitle = "50-item history, PIN & auto-delete timer",
                    onClick = { onNavigate("clipboard") }
                )
                HorizontalDivider(color = ModernCardStroke.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                ModernSettingsRow(
                    icon = "🔊",
                    title = "Sound & Haptic",
                    subtitle = "Key sound and tactile vibration",
                    onClick = { onNavigate("sound") }
                )
                HorizontalDivider(color = ModernCardStroke.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                ModernSettingsRow(
                    icon = "🛡️",
                    title = "Privacy & Data",
                    subtitle = "Learning, data control & clear clipboard",
                    onClick = { onNavigate("privacy") }
                )
                HorizontalDivider(color = ModernCardStroke.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                ModernSettingsRow(
                    icon = "📖",
                    title = "Dictionary & Vocabulary",
                    subtitle = "Personal dictionary, definitions & Word of the Day",
                    onClick = { onNavigate("dictionary") }
                )
                HorizontalDivider(color = ModernCardStroke.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                ModernSettingsRow(
                    icon = "ℹ️",
                    title = "About",
                    subtitle = "App info, version 2.0.0 & feedback",
                    onClick = { onNavigate("about") }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ModernSettingsRow(
    icon: String,
    isKeycapIcon: Boolean = false,
    title: String,
    subtitle: String,
    trailingBadge: String? = null,
    badgeColor: Color = SuccessGreen,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ModernIconBg)
                    .border(1.dp, ModernCardStroke, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (isKeycapIcon) {
                    Text(
                        text = icon,
                        color = ModernCyan,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(text = icon, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (trailingBadge != null) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(badgeColor.copy(alpha = 0.15f))
                        .border(1.dp, badgeColor, CircleShape)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = trailingBadge,
                        color = badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = "›",
                fontSize = 20.sp,
                fontWeight = FontWeight.Light,
                color = TextSecondary
            )
        }
    }
}

// =============================================================================
// SCREEN 2: TYPING & INPUT SUB-PAGE (Cleaned: Only Functional Controls)
// =============================================================================

@Composable
fun TypingAndInputSubPage(
    viewModel: SettingsViewModel,
    uiState: com.cleverkeyboard.app.ui.viewmodel.SettingsUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.Transparent,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCyan.copy(alpha = 0.4f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF0F2B48), Color(0xFF091827))
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(ModernCyan, ModernBlue)
                                )
                            )
                            .shadow(12.dp, RoundedCornerShape(14.dp), spotColor = ModernCyan),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("A", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Typing & Input",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Customize your typing experience for speed and accuracy.",
                            fontSize = 12.sp,
                            color = ModernCyan.copy(alpha = 0.85f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Section 1: Prediction & Correction
        Text(
            text = "Prediction & Correction",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = ModernCyan,
            modifier = Modifier.padding(start = 4.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                ModernToggleRow(
                    icon = "🔤",
                    title = "Smart Auto-Correct",
                    subtitle = "Auto-correct for English, Tamil & Tanglish",
                    checked = uiState.isAutoCorrectEnabled,
                    onCheckedChange = { viewModel.toggleAutoCorrect(it) }
                )
                HorizontalDivider(color = ModernCardStroke.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                ModernToggleRow(
                    icon = "💡",
                    title = "Next Word Prediction",
                    subtitle = "Predict next words naturally as you type",
                    checked = uiState.isNextWordPredictionEnabled,
                    onCheckedChange = { viewModel.toggleNextWordPrediction(it) }
                )
                HorizontalDivider(color = ModernCardStroke.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                ModernToggleRow(
                    icon = "🔠",
                    title = "Auto Capitalization",
                    subtitle = "Start sentences with a capital letter",
                    checked = uiState.isAutoCapitalizationEnabled,
                    onCheckedChange = { viewModel.toggleAutoCapitalization(it) }
                )
                HorizontalDivider(color = ModernCardStroke.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                ModernToggleRow(
                    icon = "⚡",
                    title = "Smart Punctuation",
                    subtitle = "Double-tap space for period (.) & smart auto spacing",
                    checked = uiState.isSmartPunctuationEnabled,
                    onCheckedChange = { viewModel.toggleSmartPunctuation(it) }
                )
                HorizontalDivider(color = ModernCardStroke.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                ModernToggleRow(
                    icon = "👆",
                    title = "Swipe / Glide Typing",
                    subtitle = "Slide finger across keys to type words rapidly",
                    checked = uiState.isSwipeTypingEnabled,
                    onCheckedChange = { viewModel.toggleSwipeTyping(it) }
                )
            }
        }

        // Section 2: Number & Symbols
        Text(
            text = "Number & Keypad",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = ModernCyan,
            modifier = Modifier.padding(start = 4.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                ModernToggleRow(
                    icon = "🔢",
                    title = "Show Number Row",
                    subtitle = "Permanent 1-0 row on top of keyboard",
                    checked = uiState.isNumberRowEnabled,
                    onCheckedChange = { viewModel.toggleNumberRow(it) }
                )
                HorizontalDivider(color = ModernCardStroke.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                ModernToggleRow(
                    icon = "📱",
                    title = "Auto Number Pad for OTP",
                    subtitle = "Auto-open calculator number pad on OTP, phone & PIN fields",
                    checked = uiState.isAutoNumPadEnabled,
                    onCheckedChange = { viewModel.toggleAutoNumPad(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ModernToggleRow(
    icon: String,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ModernIconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ModernCyan,
                uncheckedTrackColor = Color(0xFF1E2E3E),
                uncheckedThumbColor = Color.LightGray
            )
        )
    }
}

// =============================================================================
// SCREEN 3: THEMES SCREEN (Complete Palette + Photo Color Customization + No Duplicates)
// =============================================================================

@Composable
fun ThemesScreen(
    viewModel: SettingsViewModel,
    uiState: com.cleverkeyboard.app.ui.viewmodel.SettingsUiState,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onPickPhoto: () -> Unit
) {
    val categories = listOf(
        "All" to "🎨 All",
        "My Themes" to "⭐ My Themes",
        "Gradient Themes" to "🌈 Gradients",
        "Dark Themes" to "🌑 Dark",
        "Light Themes" to "☀️ Light",
        "Neon & Glow Themes" to "⚡ Neon",
        "Nature Themes" to "🌿 Nature",
        "Minimal & Glass Themes" to "🪟 Glass",
        "Solid Colors" to "🔘 Solid",
        "Photo" to "🖼️ Photo",
        "Custom" to "⚙️ Custom"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Live Keyboard Preview Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Live Keyboard Preview",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = uiState.currentTheme.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ModernCyan
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                KeyboardLivePreview(theme = uiState.currentTheme)
            }
        }

        // 2. Sub-Category Filter Capsules
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { (catKey, catLabel) ->
                val isSelected = selectedCategory == catKey
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .then(
                            if (isSelected) {
                                Modifier
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(ModernCyan, ModernBlue)
                                        )
                                    )
                                    .shadow(6.dp, RoundedCornerShape(18.dp), spotColor = ModernCyan)
                            } else {
                                Modifier
                                    .background(ModernCardBg)
                                    .border(1.dp, ModernCardStroke, RoundedCornerShape(18.dp))
                                    .clickable { onSelectCategory(catKey) }
                            }
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = catLabel,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextSecondary
                    )
                }
            }
        }

        // 3. Theme Content Based on Selected Filter
        when (selectedCategory) {
            "My Themes" -> {
                MyThemesSection(viewModel, uiState)
            }
            "Photo" -> {
                PhotoThemeSection(viewModel, uiState, onPickPhoto)
            }
            "Custom" -> {
                CustomColorsSection(viewModel, uiState)
            }
            "All" -> {
                // Show All Categorized Sections
                for ((categoryTitle, themeList) in KeyboardTheme.ALL_CATEGORIZED_THEMES) {
                    ThemeCategorySection(
                        title = categoryTitle,
                        themes = themeList,
                        currentThemeId = uiState.currentTheme.id,
                        onSelectTheme = { viewModel.selectPreset(it) }
                    )
                }
            }
            else -> {
                // Show specific category
                val themes = KeyboardTheme.ALL_CATEGORIZED_THEMES[selectedCategory] ?: emptyList()
                ThemeCategorySection(
                    title = selectedCategory,
                    themes = themes,
                    currentThemeId = uiState.currentTheme.id,
                    onSelectTheme = { viewModel.selectPreset(it) }
                )
            }
        }

        // 4. Key Customization Controls (Strictly theme appearance controls)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Key Borders Control
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("🖼️ Key Borders", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text(
                            text = when (uiState.keyBordersMode) {
                                KeyboardTheme.BORDERS_ON -> "ON (Board with 1dp border)"
                                KeyboardTheme.BORDERS_OFF -> "OFF (Flat transparent keys)"
                                else -> "AUTO (Adapts to theme)"
                            },
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val borderModes = listOf(
                            KeyboardTheme.BORDERS_AUTO to "Auto",
                            KeyboardTheme.BORDERS_ON to "Board",
                            KeyboardTheme.BORDERS_OFF to "Flat"
                        )
                        for ((bMode, bLabel) in borderModes) {
                            val isSel = uiState.keyBordersMode == bMode
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) ModernCyan else Color(0xFF162534),
                                border = if (isSel) null else androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke),
                                modifier = Modifier.clickable { viewModel.setKeyBordersMode(bMode) }
                            ) {
                                Text(
                                    text = bLabel,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Color.Black else Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = ModernCardStroke.copy(alpha = 0.5f))

                // Key Shape Control (Square, Rounded, Super Rounded, Pill)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⬡ Key Shape", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text(
                            text = when (uiState.keyShape) {
                                KeyboardTheme.KEY_SHAPE_SQUARE -> "Square"
                                KeyboardTheme.KEY_SHAPE_ROUNDED -> "Rounded"
                                KeyboardTheme.KEY_SHAPE_SUPER_ROUNDED -> "Super Rounded"
                                KeyboardTheme.KEY_SHAPE_PILL -> "Pill"
                                else -> "Rounded"
                            },
                            fontSize = 11.sp,
                            color = ModernCyan
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val shapes = listOf(
                            KeyboardTheme.KEY_SHAPE_SQUARE to "Square",
                            KeyboardTheme.KEY_SHAPE_ROUNDED to "Rounded",
                            KeyboardTheme.KEY_SHAPE_SUPER_ROUNDED to "Super",
                            KeyboardTheme.KEY_SHAPE_PILL to "Pill"
                        )
                        for ((shapeKey, shapeLabel) in shapes) {
                            val isSel = uiState.keyShape == shapeKey
                            val shapeRadius = when (shapeKey) {
                                KeyboardTheme.KEY_SHAPE_SQUARE -> RoundedCornerShape(2.dp)
                                KeyboardTheme.KEY_SHAPE_ROUNDED -> RoundedCornerShape(8.dp)
                                KeyboardTheme.KEY_SHAPE_SUPER_ROUNDED -> RoundedCornerShape(13.dp)
                                KeyboardTheme.KEY_SHAPE_PILL -> RoundedCornerShape(24.dp)
                                else -> RoundedCornerShape(8.dp)
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) ModernCyan.copy(alpha = 0.25f) else Color(0xFF162534),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSel) 2.dp else 1.dp,
                                    if (isSel) ModernCyan else ModernCardStroke
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setKeyShape(shapeKey) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp, 18.dp)
                                            .clip(shapeRadius)
                                            .background(if (isSel) ModernCyan else TextSecondary.copy(alpha = 0.5f))
                                    )
                                    Text(
                                        text = shapeLabel,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) ModernCyan else Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = ModernCardStroke.copy(alpha = 0.5f))

                // Keyboard Height Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("↕ Keyboard Height", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        val heightLabel = when {
                            uiState.keyboardHeightPercent < 95 -> "Compact (${uiState.keyboardHeightPercent}%)"
                            uiState.keyboardHeightPercent > 105 -> "Tall (${uiState.keyboardHeightPercent}%)"
                            else -> "Normal (100%)"
                        }
                        Text(text = heightLabel, fontSize = 11.sp, color = ModernCyan)
                    }

                    Slider(
                        value = uiState.keyboardHeightPercent.toFloat(),
                        onValueChange = { viewModel.setKeyboardHeight(it.toInt()) },
                        valueRange = 80f..120f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = ModernCyan,
                            activeTrackColor = ModernCyan,
                            inactiveTrackColor = Color(0xFF162534)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ThemeCategorySection(
    title: String,
    themes: List<KeyboardTheme>,
    currentThemeId: String,
    onSelectTheme: (KeyboardTheme) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(start = 2.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            themes.chunked(2).forEach { rowThemes ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowThemes.forEach { theme ->
                        val isSelected = currentThemeId == theme.id
                        ThemeCard(
                            theme = theme,
                            isSelected = isSelected,
                            onClick = { onSelectTheme(theme) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowThemes.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeCard(
    theme: KeyboardTheme,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(76.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) ModernCyan else ModernCardStroke
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (theme.hasGradient) {
                        Modifier.background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(theme.gradientStartColor!!),
                                    Color(theme.gradientEndColor!!)
                                )
                            )
                        )
                    } else {
                        Modifier.background(Color(theme.keyboardBgColor))
                    }
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text(
                    text = theme.name,
                    color = if (KeyboardTheme.isColorLight(theme.keyboardBgColor) && !theme.hasGradient) Color(0xFF111827) else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp, 12.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(theme.keyBgColor))
                            .border(0.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(3.dp))
                    )
                    Box(
                        modifier = Modifier
                            .size(18.dp, 12.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(theme.accentColor))
                    )
                }
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(ModernCyan),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// =============================================================================
// PHOTO THEME (With Key Text Color, Key Background Color, Accent Color & Tint)
// =============================================================================

@Composable
fun PhotoThemeSection(
    viewModel: SettingsViewModel,
    uiState: com.cleverkeyboard.app.ui.viewmodel.SettingsUiState,
    onPickPhoto: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = ModernCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🖼️ Photo Background Theme", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Pick any photo from gallery with blur, opacity, and full color styling.", fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onPickPhoto,
                    colors = ButtonDefaults.buttonColors(containerColor = ModernCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🖼️ Pick from Gallery", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                if (!uiState.currentTheme.photoPath.isNullOrBlank()) {
                    OutlinedButton(
                        onClick = { viewModel.removePhotoTheme() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) {
                        Text("Remove")
                    }
                }
            }

            if (!uiState.currentTheme.photoPath.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Dark Overlay (Darkness)", color = Color.White, fontSize = 13.sp)
                    Text("${(uiState.currentTheme.photoDarkness * 100).toInt()}%", color = ModernCyan, fontSize = 13.sp)
                }
                Slider(
                    value = uiState.currentTheme.photoDarkness,
                    onValueChange = { viewModel.updatePhotoDarkness(it) },
                    valueRange = 0f..0.80f,
                    colors = SliderDefaults.colors(thumbColor = ModernCyan, activeTrackColor = ModernCyan)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Background Blur", color = Color.White, fontSize = 13.sp)
                    Text("${uiState.currentTheme.photoBlur.toInt()} px", color = ModernCyan, fontSize = 13.sp)
                }
                Slider(
                    value = uiState.currentTheme.photoBlur,
                    onValueChange = { viewModel.updatePhotoBlur(it) },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(thumbColor = ModernCyan, activeTrackColor = ModernCyan)
                )
            }

            // COLOR CUSTOMIZATION OPTIONS FOR PHOTO THEME
            Spacer(modifier = Modifier.height(16.dp))
            Text("Customize Colors on Photo Theme", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Adjust key text color, keycap background, and accent color over your photo:", fontSize = 11.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ColorPickerRow(
                    label = "1. Background Overlay Tint",
                    colorInt = uiState.currentTheme.keyboardBgColor,
                    onClick = { viewModel.openColorPicker(ColorTarget.KEYBOARD_BG) }
                )
                ColorPickerRow(
                    label = "2. Key Background Color",
                    colorInt = uiState.currentTheme.keyBgColor,
                    onClick = { viewModel.openColorPicker(ColorTarget.KEY_BG) }
                )
                ColorPickerRow(
                    label = "3. Key Text Color",
                    colorInt = uiState.currentTheme.keyTextColor,
                    onClick = { viewModel.openColorPicker(ColorTarget.KEY_TEXT) }
                )
                ColorPickerRow(
                    label = "4. Accent Color (AI, Enter & Space)",
                    colorInt = uiState.currentTheme.accentColor,
                    onClick = { viewModel.openColorPicker(ColorTarget.ACCENT) }
                )
            }
        }
    }
}

// =============================================================================
// MY THEMES SECTION (Room Database Persistence)
// =============================================================================

@Composable
fun MyThemesSection(
    viewModel: SettingsViewModel,
    uiState: com.cleverkeyboard.app.ui.viewmodel.SettingsUiState
) {
    val context = LocalContext.current
    val customThemes by com.cleverkeyboard.app.data.ThemeManager.getCustomThemesFlow(context).collectAsState(initial = emptyList())
    var showCreateDialog by remember { mutableStateOf(false) }
    var newThemeName by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Create Theme Button Header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    newThemeName = "My Theme #${customThemes.size + 1}"
                    showCreateDialog = true
                },
            shape = RoundedCornerShape(18.dp),
            color = ModernCyan.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCyan.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ModernCyan),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Create Custom Theme", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Save current colors & shape to Room DB", fontSize = 12.sp, color = ModernCyan)
                    }
                }
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = ModernCyan)
            }
        }

        // Saved Themes List
        if (customThemes.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = ModernCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("⭐", fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No Custom Themes Saved Yet", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Customize colors and tap 'Create Custom Theme' above to save your theme permanently.", fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.Center)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (entity in customThemes) {
                    val isSel = uiState.currentTheme.id == entity.themeId
                    val themeObj = com.cleverkeyboard.app.data.KeyboardTheme(
                        id = entity.themeId,
                        name = entity.name,
                        category = "My Themes",
                        keyboardBgColor = entity.keyboardBgColor,
                        keyBgColor = entity.keyBgColor,
                        keyTextColor = entity.keyTextColor,
                        accentColor = entity.accentColor,
                        photoPath = entity.photoPath,
                        photoDarkness = entity.photoDarkness,
                        photoBlur = entity.photoBlur,
                        keyBordersMode = entity.keyBordersMode,
                        isCustom = true
                    )
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectPreset(themeObj) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSel) ModernCyan.copy(alpha = 0.15f) else ModernCardBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) ModernCyan else ModernCardStroke)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(entity.keyboardBgColor))
                                        .border(2.dp, Color(entity.accentColor), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(entity.keyBgColor))
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(entity.name, fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 14.sp)
                                    Text(if (isSel) "Active Theme ✓" else "Tap to apply", fontSize = 11.sp, color = if (isSel) ModernCyan else TextSecondary)
                                }
                            }
                            IconButton(onClick = { viewModel.deleteCustomTheme(entity.themeId) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF6B6B))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Save Custom Theme", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Give your theme a name and save it into your permanent collection.", fontSize = 12.sp, color = TextSecondary)
                    OutlinedTextField(
                        value = newThemeName,
                        onValueChange = { newThemeName = it },
                        label = { Text("Theme Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ModernCyan,
                            unfocusedBorderColor = ModernCardStroke
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createAndSaveCustomTheme(
                            name = newThemeName,
                            keyboardBgColor = uiState.currentTheme.keyboardBgColor,
                            keyBgColor = uiState.currentTheme.keyBgColor,
                            keyTextColor = uiState.currentTheme.keyTextColor,
                            accentColor = uiState.currentTheme.accentColor,
                            keyShape = uiState.keyShape,
                            keyBordersMode = uiState.keyBordersMode
                        )
                        showCreateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ModernCyan)
                ) {
                    Text("Save & Apply", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = Color(0xFF141F2B)
        )
    }
}

// =============================================================================
// CUSTOM COLORS SECTION
// =============================================================================

@Composable
fun CustomColorsSection(
    viewModel: SettingsViewModel,
    uiState: com.cleverkeyboard.app.ui.viewmodel.SettingsUiState
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = ModernCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🎨 Custom Color Palette", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Customize exact hex colors for keyboard background, keycaps, text, and accent.", fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ColorPickerRow(
                    label = "1. Keyboard Background",
                    colorInt = uiState.currentTheme.keyboardBgColor,
                    onClick = { viewModel.openColorPicker(ColorTarget.KEYBOARD_BG) }
                )
                ColorPickerRow(
                    label = "2. Key Background",
                    colorInt = uiState.currentTheme.keyBgColor,
                    onClick = { viewModel.openColorPicker(ColorTarget.KEY_BG) }
                )
                ColorPickerRow(
                    label = "3. Key Text Color",
                    colorInt = uiState.currentTheme.keyTextColor,
                    onClick = { viewModel.openColorPicker(ColorTarget.KEY_TEXT) }
                )
                ColorPickerRow(
                    label = "4. Accent Color (AI & Enter)",
                    colorInt = uiState.currentTheme.accentColor,
                    onClick = { viewModel.openColorPicker(ColorTarget.ACCENT) }
                )
            }
        }
    }
}

@Composable
private fun ColorPickerRow(
    label: String,
    colorInt: Int,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = Color(0xFF142434),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = Color.White
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = KeyboardTheme.colorToHex(colorInt),
                    fontSize = 12.sp,
                    color = ModernCyan,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(colorInt))
                        .border(1.5.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                )
            }
        }
    }
}

// =============================================================================
// SUB-PAGES (Setup, AI, Layout, Sound, Privacy, About)
// =============================================================================

@Composable
fun KeyboardSetupSubPage(
    viewModel: SettingsViewModel,
    uiState: com.cleverkeyboard.app.ui.viewmodel.SettingsUiState
) {
    val context = LocalContext.current
    var testText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Activation Status", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(14.dp))

                // Step 1: Enable
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("1. Enable Keyboard", fontWeight = FontWeight.SemiBold, color = Color.White)
                        Text(
                            text = if (uiState.isKeyboardEnabled) "✓ Enabled in Settings" else "Turn on in Android Settings",
                            fontSize = 12.sp,
                            color = if (uiState.isKeyboardEnabled) SuccessGreen else TextSecondary
                        )
                    }
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.isKeyboardEnabled) Color(0xFF162534) else ModernCyan
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (uiState.isKeyboardEnabled) "Recheck" else "Enable",
                            color = if (uiState.isKeyboardEnabled) Color.White else Color.Black
                        )
                    }
                }

                HorizontalDivider(color = ModernCardStroke.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 12.dp))

                // Step 2: Select
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("2. Select Default Keyboard", fontWeight = FontWeight.SemiBold, color = Color.White)
                        Text(
                            text = if (uiState.isKeyboardSelected) "✓ Active as default keyboard" else "Select CleverKeyboard",
                            fontSize = 12.sp,
                            color = if (uiState.isKeyboardSelected) SuccessGreen else TextSecondary
                        )
                    }
                    Button(
                        onClick = {
                            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                            imm?.showInputMethodPicker()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.isKeyboardSelected) Color(0xFF162534) else ModernBlue
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (uiState.isKeyboardSelected) "Switch" else "Select",
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Step 3: Interactive Test Box
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Test Keyboard", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Tap below to test typing, emoji, numpad, and AI features:", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = testText,
                    onValueChange = { testText = it },
                    placeholder = { Text("Type here to test CleverKeyboard...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = ModernCyan,
                        unfocusedBorderColor = ModernCardStroke
                    )
                )
            }
        }
    }
}

@Composable
fun AiFeaturesSubPage(
    viewModel: SettingsViewModel,
    uiState: com.cleverkeyboard.app.ui.viewmodel.SettingsUiState,
    keyboardController: androidx.compose.ui.platform.SoftwareKeyboardController?
) {
    val context = LocalContext.current
    var isApiKeyVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Google Gemini AI Key", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    if (uiState.hasApiKey) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(SuccessGreen.copy(alpha = 0.2f))
                                .border(1.dp, SuccessGreen, CircleShape)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Active ✓", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text("Powers AI Write, AI Reply, Fix Grammar, Tone Rewrite, and Tamil Translation.", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.apiKey,
                    onValueChange = { viewModel.onApiKeyChanged(it) },
                    label = { Text("Gemini API Key") },
                    placeholder = { Text("AIzaSy...") },
                    singleLine = true,
                    visualTransformation = if (isApiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isApiKeyVisible = !isApiKeyVisible }) {
                            Icon(
                                imageVector = if (isApiKeyVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle visibility",
                                tint = Color.Gray
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = ModernCyan,
                        unfocusedBorderColor = ModernCardStroke
                    )
                )

                uiState.apiKeySavedMessage?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(it, color = SuccessGreen, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey"))
                            context.startActivity(intent)
                        }
                    ) {
                        Text("Get Free Key ↗", color = ModernCyan, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            keyboardController?.hide()
                            viewModel.saveApiKey()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ModernCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Key", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // AI Toolbar Labels Toggle
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                ModernToggleRow(
                    icon = "🏷️",
                    title = "Show AI Bar Labels",
                    subtitle = "Show text labels under icons (Translate, AI Write, Fix, Lang)",
                    checked = uiState.isAiToolbarLabelsEnabled,
                    onCheckedChange = { viewModel.toggleAiToolbarLabels(it) }
                )
            }
        }
    }
}

@Composable
fun LayoutAndKeysSubPage(
    viewModel: SettingsViewModel,
    uiState: com.cleverkeyboard.app.ui.viewmodel.SettingsUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Key Shape Customization (Square, Rounded, Super Rounded, Pill)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Key Shape", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Choose key cap corner curvature style.", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val shapes = listOf(
                        KeyboardTheme.KEY_SHAPE_SQUARE to "Square",
                        KeyboardTheme.KEY_SHAPE_ROUNDED to "Rounded",
                        KeyboardTheme.KEY_SHAPE_SUPER_ROUNDED to "Super",
                        KeyboardTheme.KEY_SHAPE_PILL to "Pill"
                    )
                    for ((shape, label) in shapes) {
                        val isSel = uiState.keyShape == shape
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) ModernCyan else Color(0xFF162534),
                            border = if (isSel) null else androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke),
                            modifier = Modifier.weight(1f).clickable { viewModel.setKeyShape(shape) }
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.Black else Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Keyboard Dismiss Button Setting
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Keyboard Dismiss Button", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Control when the downward chevron (⌄) appears to hide the keyboard.", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val modes = listOf(
                        KeyboardTheme.DISMISS_AUTO to "Auto",
                        KeyboardTheme.DISMISS_ALWAYS to "Always",
                        KeyboardTheme.DISMISS_NEVER to "Off"
                    )
                    for ((mode, label) in modes) {
                        val isSel = uiState.currentTheme.dismissButtonMode == mode
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) ModernCyan else Color(0xFF162534),
                            border = if (isSel) null else androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke),
                            modifier = Modifier.weight(1f).clickable { viewModel.setDismissButtonMode(mode) }
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.Black else Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Number Row & Number Pad controls
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                ModernToggleRow(
                    icon = "🔢",
                    title = "Show Number Row",
                    subtitle = "Permanent 1-0 row on top of keyboard",
                    checked = uiState.isNumberRowEnabled,
                    onCheckedChange = { viewModel.toggleNumberRow(it) }
                )
                HorizontalDivider(color = ModernCardStroke.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                ModernToggleRow(
                    icon = "📱",
                    title = "Auto Number Pad for OTP",
                    subtitle = "Auto-open calculator keypad on OTP and Phone fields",
                    checked = uiState.isAutoNumPadEnabled,
                    onCheckedChange = { viewModel.toggleAutoNumPad(it) }
                )
            }
        }
    }
}

@Composable
fun SoundAndHapticSubPage(
    viewModel: SettingsViewModel,
    uiState: com.cleverkeyboard.app.ui.viewmodel.SettingsUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                ModernToggleRow(
                    icon = "📳",
                    title = "Keypress Vibration",
                    subtitle = "Subtle tactile haptic feedback on keypress",
                    checked = uiState.isVibrationEnabled,
                    onCheckedChange = { viewModel.toggleVibration(it) }
                )
                HorizontalDivider(color = ModernCardStroke.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                ModernToggleRow(
                    icon = "🔊",
                    title = "Keypress Sound",
                    subtitle = "Audible click feedback when typing",
                    checked = uiState.isSoundEnabled,
                    onCheckedChange = { viewModel.toggleSound(it) }
                )
            }
        }
    }
}

@Composable
fun PrivacySubPage(context: Context) {
    var clearMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🛡️ Privacy Commitment", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "CleverKeyboard is built with privacy-first architecture. All keystrokes, personal dictionary data, and typing history remain strictly 100% on your device.\n\nOnly explicit AI requests (AI Write, AI Reply, Fix Grammar) are sent to Google Gemini using your personal private API key.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            com.cleverkeyboard.app.clipboard.ClipboardHistoryManager.clearAll(context)
                            clearMessage = "Clipboard history cleared!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B1E28)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear Clipboard", color = Color(0xFFFF6B6B), fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            context.getSharedPreferences("clever_personal_dict_prefs", Context.MODE_PRIVATE).edit().clear().apply()
                            clearMessage = "Learned vocabulary reset!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2838)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset Dictionary", color = ModernCyan, fontSize = 11.sp)
                    }
                }

                clearMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it, color = SuccessGreen, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun AboutSubPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("CleverKeyboard AI 2.0", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Version 2.0.0 • 👑 Pro Commercial Edition", fontSize = 12.sp, color = ModernGold, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "• Full Gradient, Dark, Light, Neon, Nature & Glass theme studio\n" +
                    "• Dedicated Native Tamil Keyboard Layout (தமிழ்) & Unicode character composition\n" +
                    "• Phonetic Tanglish-to-Tamil real-time transliteration engine\n" +
                    "• Gboard-style Undo Autocorrect on backspace\n" +
                    "• 13 AI Reply & Write tones with prompt backoff retry & cancellation\n" +
                    "• SQLite Room database persistence for snippets, dictionary & custom themes\n" +
                    "• 50-Item Clipboard with PIN, search & auto-deletion for sensitive data\n" +
                    "• Trackpad spacebar cursor slider & IME keyboard switcher\n" +
                    "• Photo gallery themes with darkness, blur, and live preview",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

// =============================================================================
// SUB-PAGE: LANGUAGES (English, Tamil, Tanglish)
// =============================================================================

@Composable
fun LanguagesSubPage(
    viewModel: SettingsViewModel,
    uiState: com.cleverkeyboard.app.ui.viewmodel.SettingsUiState
) {
    val languages = listOf(
        Triple("EN", "English (India)", "Standard QWERTY layout with intelligent autocorrect"),
        Triple("TA", "தமிழ் (Tamil)", "Native Tamil script layout (அ ஆ இ... க ங ச...)"),
        Triple("TANG", "Tanglish", "Type in English letters, auto-transliterate to Tamil (vanakkam -> வணக்கம்)")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🌐 Keyboard Languages", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Select your primary typing mode. You can also switch instantly via the 🌐 globe icon on the spacebar.", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    for ((code, title, desc) in languages) {
                        val isSelected = uiState.activeLanguage == code
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) ModernCyan.copy(alpha = 0.15f) else Color(0xFF141F2B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ModernCyan else ModernCardStroke),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setActiveLanguage(code) }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(title, fontWeight = FontWeight.Bold, color = if (isSelected) ModernCyan else Color.White, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(desc, fontSize = 11.sp, color = TextSecondary)
                                }
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(ModernCyan),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("✓", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// SUB-PAGE: VOICE & DICTATION
// =============================================================================

@Composable
fun VoiceSubPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🎙️ Voice Typing & Dictation", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "CleverKeyboard 2.0 features natural speech-to-text dictation with live animated audio waveforms.\n\n" +
                    "• English & Tamil speech recognition\n" +
                    "• Automatic punctuation and formatting\n" +
                    "• Real-time sound amplitude waveform\n" +
                    "• Tap the microphone (🎙️) on the top toolbar anytime to speak.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

// =============================================================================
// SUB-PAGE: CLIPBOARD & SNIPPETS
// =============================================================================

@Composable
fun ClipboardSubPage(context: Context) {
    var message by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📋 Clipboard & Snippets", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "• History capped at latest 50 items to ensure lightning speed.\n" +
                    "• Pinned items stay forever at top and never auto-delete.\n" +
                    "• Sensitive text (OTPs, PINs, Passwords) auto-deleted after 1 Hour.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        com.cleverkeyboard.app.clipboard.ClipboardHistoryManager.clearAll(context)
                        message = "Clipboard cleared!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B1E28)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Clear All Non-Pinned Items", color = Color(0xFFFF6B6B))
                }
                message?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(it, color = SuccessGreen, fontSize = 12.sp)
                }
            }
        }
    }
}

// =============================================================================
// SUB-PAGE: DICTIONARY & VOCABULARY
// =============================================================================

@Composable
fun DictionarySubPage(context: Context) {
    val coroutineScope = rememberCoroutineScope()
    var searchWord by remember { mutableStateOf("") }
    var definitionResult by remember { mutableStateOf<com.cleverkeyboard.app.data.WordDefinition?>(null) }
    var isSearching by remember { mutableStateOf(false) }
    var actionMessage by remember { mutableStateOf<String?>(null) }

    val wordOfTheDay = remember { com.cleverkeyboard.app.data.DictionaryManager.getWordOfTheDay() }
    val personalWordsFlow = remember { com.cleverkeyboard.app.data.DictionaryManager.getPersonalWordsFlow(context) }
    val personalWords by personalWordsFlow.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Word of the Day
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCyan.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🌟 Word of the Day", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ModernCyan)
                    Text("Daily Vocabulary", fontSize = 11.sp, color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(wordOfTheDay.word, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("(${wordOfTheDay.partOfSpeech})", fontSize = 12.sp, color = ModernCyan)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(wordOfTheDay.definition, fontSize = 13.sp, color = Color(0xFFE0E0E0), lineHeight = 18.sp)
                if (wordOfTheDay.synonyms.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Synonyms: ${wordOfTheDay.synonyms.joinToString(", ")}", fontSize = 11.sp, color = Color(0xFF80D8FF))
                }
                if (wordOfTheDay.example.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("\"${wordOfTheDay.example}\"", fontSize = 11.sp, color = Color(0xFFB0BEC5), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                }
            }
        }

        // 2. Word Lookup Search
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📖 Look Up Word", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchWord,
                        onValueChange = { searchWord = it },
                        placeholder = { Text("Enter a word (e.g. innovation)", color = TextSecondary, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ModernCyan,
                            unfocusedBorderColor = ModernCardStroke,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (searchWord.isNotBlank()) {
                                isSearching = true
                                coroutineScope.launch {
                                    val apiKey = com.cleverkeyboard.app.data.ApiKeyManager.getApiKey(context)
                                    val res = com.cleverkeyboard.app.data.DictionaryManager.lookupWord(searchWord, apiKey)
                                    definitionResult = res
                                    isSearching = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ModernCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(54.dp)
                    ) {
                        Text("Search", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                if (isSearching) {
                    Spacer(modifier = Modifier.height(10.dp))
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = ModernCyan, strokeWidth = 2.dp)
                }

                definitionResult?.let { def ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF141F2B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(def.word.replaceFirstChar { it.uppercase() }, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("(${def.partOfSpeech})", fontSize = 12.sp, color = ModernCyan)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(def.definition, fontSize = 12.sp, color = Color(0xFFE0E0E0), lineHeight = 16.sp)
                            if (def.synonyms.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Synonyms: ${def.synonyms.joinToString(", ")}", fontSize = 11.sp, color = Color(0xFF80D8FF))
                            }
                            if (def.example.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("\"${def.example}\"", fontSize = 11.sp, color = Color(0xFFB0BEC5), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                            }
                        }
                    }
                }
            }
        }

        // 3. Personal Learned Dictionary (Room DB)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ModernCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ModernCardStroke)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📚 Personal Dictionary", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Words and phrases you type frequently are automatically learned locally in your private Room database.\n" +
                    "Learned words: ${personalWords.size}",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (personalWords.isNotEmpty()) {
                    Text("Recent learned terms:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(6.dp))
                    val previewWords = personalWords.take(10).joinToString(", ") { "${it.word} (${it.frequency}x)" }
                    Text(previewWords, fontSize = 11.sp, color = ModernCyan)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            com.cleverkeyboard.app.data.DictionaryManager.clearPersonalDictionary(context)
                            actionMessage = "Personal learned dictionary cleared!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B1E28)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Clear Learned Vocabulary", color = Color(0xFFFF6B6B))
                }

                actionMessage?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(it, color = SuccessGreen, fontSize = 12.sp)
                }
            }
        }
    }
}
