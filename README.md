# CleverKeyboard ⌨️✨

A modern, production-grade Android AI Keyboard app built in **Kotlin** using **Android Studio**, **Jetpack Compose** for Settings UI, and **classic Android Views** for the Keyboard Service.

---

## 🌟 Features Overview

### 1. Fully Functional Android InputMethodService (`MyKeyboardService.kt`)
- Full typing support (letters, numbers, space, delete, shift/caps lock, symbols, enter).
- Classic XML keyboard definitions:
  - `res/xml/qwerty.xml`: Standard QWERTY layout
  - `res/xml/symbols.xml`: Numbers and primary symbols (?123 mode)
  - `res/xml/symbols_shift.xml`: Math and special symbols (=< mode)
- Input connection management ensuring seamless operation across **WhatsApp, Chrome, Notes, Gmail, Telegram**, and any standard Android input field.
- System manifest declaration with `android.permission.BIND_INPUT_METHOD`, `android.view.InputMethod` intent filter, and `res/xml/method.xml`.

### 2. AI Features Toolbar (Top Row Above Keyboard)
Horizontal scrollable toolbar with 5 AI capabilities powered by **Google Gemini 2.5 Flash**:
1. **Fix Grammar & Spelling**:
   - Prompt: `Fix grammar and spelling, return only corrected text, no explanation: {text}`
2. **Tone Change**:
   - Sub-panel options: **Professional**, **Casual / Friendly**, **Funny**.
   - Prompt: `Rewrite this text in {tone} tone, keep meaning same, return only rewritten text: {text}`
3. **Translate**:
   - Sub-panel options: **Tamil → English** and **English → Tamil**.
   - Prompt: `Translate this to {targetLang}, return only translation: {text}`
4. **Smart Reply**:
   - Prompt: `Given this incoming message: {text}, generate 3 short, natural replies, return as JSON array`
   - Generates 3 contextual quick-replies displayed as interactive tap-to-insert chips.
5. **Make Short / Make Long**:
   - Sub-panel options: **Make Shorter**, **Make Longer**.
   - Prompt: `Make this text shorter / longer while keeping meaning: {text}`

- **Text Replacement**: Retrieves selected text via `currentInputConnection.getSelectedText()` (with fallback to surrounding text before cursor), deletes the selected text, and replaces it with the AI result via `currentInputConnection.commitText()`.
- **Security**: Gemini API Key is never hardcoded; saved securely using `EncryptedSharedPreferences`.

### 3. Custom Theme Builder ("Create Your Theme")
- Customize 4 colors with an interactive HSV Color Picker:
  1. **Keyboard Background Color**
  2. **Key Background Color**
  3. **Key Text Color**
  4. **Accent Color** (for AI buttons, Enter, Space)
- **Live Keyboard Preview** at top that updates in real time as any color is adjusted.
- **3 Preset Themes**:
  - **Midnight Black**: BG `#000000`, Key `#1E1E1E`, Text `#E0E0E0`, Accent `#D4AF37` (Gold)
  - **King Blue**: BG `#0A1931`, Key `#1A2A4A`, Text `#FFFFFF`, Accent `#EC1C24` (Red)
  - **Neon Gamer**: BG `#000000`, Key `#1A1A1A`, Text `#FFFFFF`, Accent `#00FF88` (Neon Green)
- Persistent storage in `SharedPreferences` via `ThemeManager`, applied immediately on keyboard launch.

### 4. Settings Screen (`MainActivity.kt` in Jetpack Compose)
- **App Logo & Header**: Dark mode, modern look like Gboard / CleverType.
- **Activation Card**:
  - Button to **Enable Keyboard** in Android System Settings (`Settings.ACTION_INPUT_METHOD_SETTINGS`).
  - Button to **Select Keyboard** as active input method (`InputMethodManager.showInputMethodPicker()`).
  - Status indicators displaying whether CleverKeyboard is enabled and currently selected.
- **Gemini API Key Card**:
  - Secure input field with password visibility toggle.
  - "Save API Key" button with real-time feedback.
  - Direct link to Google AI Studio to generate a free Gemini API key.
- **Custom Theme Builder Card**:
  - Real-time Live Keyboard Preview.
  - Preset theme selectors.
  - 4 customizable color rows with color swatches and hex codes.
- **About Section**:
  - Version info, features breakdown, and privacy policy (all keystrokes processed locally).

---

## 🏗️ Architecture & Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin 1.9.24 |
| **Android SDK** | Min SDK 24, Target SDK 34, Compile SDK 34 |
| **Keyboard View** | Classic Android Views (`CustomKeyboardView` + `res/layout/keyboard_view.xml`) |
| **Settings UI** | Jetpack Compose + Material 3 Dark Theme |
| **Networking** | Retrofit 2.11.0 + OkHttp 4.12.0 + Coroutines |
| **AI API** | Google Gemini REST API (`gemini-2.0-flash:generateContent`) |
| **Storage** | `EncryptedSharedPreferences` (API Key) & `SharedPreferences` (Theme) |
| **Architecture** | MVVM (Single Activity `MainActivity` + `SettingsViewModel`) |

---

## 📂 Project Directory Structure

```
CleverKeyboard/
├── build.gradle.kts                      # Root Gradle build configuration
├── settings.gradle.kts                   # Project repositories and module setup
├── gradle.properties                     # JVM & AndroidX optimization flags
├── gradlew & gradlew.bat                 # Portable Gradle wrapper scripts
├── gradle/wrapper/gradle-wrapper.properties
└── app/
    ├── build.gradle.kts                  # App module build file (Compose + Retrofit + Security)
    ├── proguard-rules.pro                # ProGuard rules
    └── src/
        └── main/
            ├── AndroidManifest.xml       # BIND_INPUT_METHOD service & MainActivity declarations
            ├── java/com/cleverkeyboard/app/
            │   ├── CleverKeyboardApp.kt  # Application entry point (initializes ThemeManager)
            │   ├── MainActivity.kt       # Jetpack Compose Settings UI
            │   ├── data/
            │   │   ├── ApiKeyManager.kt  # EncryptedSharedPreferences storage for Gemini API Key
            │   │   ├── KeyboardTheme.kt  # Theme model and Midnight Black, King Blue, Neon Gamer presets
            │   │   └── ThemeManager.kt   # Theme persistence and real-time state flow
            │   ├── keyboard/
            │   │   ├── CustomKeyboardView.kt # High-performance custom keyboard view with dynamic colors
            │   │   └── MyKeyboardService.kt  # Android InputMethodService with 5 AI features
            │   ├── network/
            │   │   ├── AiService.kt      # AI service calling Gemini 2.0 Flash with exact prompts
            │   │   ├── GeminiApiService.kt # Retrofit interface for generateContent
            │   │   └── GeminiModels.kt   # Request/response DTOs for Gemini REST API
            │   └── ui/
            │       ├── components/
            │       │   ├── ColorPickerDialog.kt   # HSV/Hex Color Picker Dialog
            │       │   └── KeyboardLivePreview.kt # Live Compose preview updating in real-time
            │       ├── theme/
            │       │   ├── Color.kt      # Palette definitions
            │       │   ├── Theme.kt      # Material3 dark theme setup
            │       │   └── Type.kt       # Typography configurations
            │       └── viewmodel/
            │           └── SettingsViewModel.kt # MVVM ViewModel for settings & themes
            └── res/
                ├── drawable/             # Vector icons for Shift, Delete, Enter, AI features, and Keycaps
                ├── layout/
                │   └── keyboard_view.xml # Classic Android View container (AI Toolbar + KeyboardView)
                ├── mipmap-anydpi-v26/    # Adaptive launcher icon
                ├── values/
                │   ├── colors.xml        # Theme preset color definitions
                │   ├── strings.xml       # String resources
                │   └── themes.xml        # Android Material3 DayNight themes
                └── xml/
                    ├── method.xml        # InputMethod subtype metadata
                    ├── qwerty.xml        # QWERTY keyboard layout
                    ├── symbols.xml       # ?123 symbols layout
                    └── symbols_shift.xml # Extended symbols layout
```

---

## 🚀 How to Open and Run in Android Studio

1. **Open Android Studio**:
   - Select **Open** and choose the `/Users/ajith/CleverClone` directory.
2. **Gradle Sync**:
   - Android Studio will automatically sync Gradle dependencies and build the project.
3. **Run on Device or Emulator**:
   - Run the `app` configuration on an emulator (API 24+) or connected physical device.
4. **Setup Steps in App**:
   1. Tap **Enable Keyboard** -> Turn on **CleverKeyboard AI** in Android Language & Input settings.
   2. Tap **Select Keyboard** -> Choose **CleverKeyboard AI** as default.
   3. Paste your **Gemini API Key** from [Google AI Studio](https://aistudio.google.com/app/apikey) and tap **Save Key**.
   4. Customize colors in **Create Your Theme** or pick one of the presets (**Midnight Black**, **King Blue**, **Neon Gamer**).
5. **Test in Any App**:
   - Open WhatsApp, Google Chrome, or Notes.
   - Type or select text, and tap any AI button on the top toolbar:
     - ✨ **Fix Grammar**
     - 🎭 **Tone Change**
     - 🌐 **Translate** (Tamil ⟷ English)
     - 💬 **Smart Reply**
     - ↔️ **Short / Long**

