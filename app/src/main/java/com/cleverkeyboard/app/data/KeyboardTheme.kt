package com.cleverkeyboard.app.data

import android.graphics.Color

data class KeyboardTheme(
    val id: String = "dark_theme",
    val name: String = "Dark Theme",
    val category: String = "Dark Themes",
    val keyboardBgColor: Int = Color.parseColor("#000000"),
    val keyBgColor: Int = Color.parseColor("#1C1C1E"),
    val keyTextColor: Int = Color.parseColor("#FFFFFF"),
    val accentColor: Int = Color.parseColor("#5B5BF0"),
    val gradientStartColor: Int? = null,
    val gradientEndColor: Int? = null,
    val photoPath: String? = null,
    val photoDarkness: Float = 0.40f, // 40% Black overlay default (Clean Gboard look)
    val photoBlur: Float = 25f,       // 25 blur radius default
    val keyBordersMode: String = BORDERS_AUTO, // "AUTO", "ON", "OFF"
    val showNumberRow: Boolean = true, // Permanent number row
    val autoCapitalization: Boolean = true, // Auto-capitalize first letter / after dot
    val showAiToolbarLabels: Boolean = true, // Show labels under AI bar icons (Default ON)
    val autoNumPadForOtp: Boolean = true, // Auto number pad for OTP/Phone fields (Default ON)
    val nextWordPrediction: Boolean = true, // Next word prediction (Default ON)
    val personalizedSuggestions: Boolean = true, // Personalized suggestions (Default ON)
    val dismissButtonMode: String = DISMISS_AUTO, // "AUTO", "ALWAYS", "NEVER"
    val keyShape: String = KEY_SHAPE_ROUNDED, // "SQUARE", "ROUNDED", "SUPER_ROUNDED", "PILL"
    val keyboardHeightPercent: Int = 100, // 80% to 120%
    val isCustom: Boolean = false,
    val isFavorite: Boolean = false,
    val isDoubleSpacePeriodEnabled: Boolean = true,
    val hiddenToolbarActions: String = ""
) {
    val keyBorders: Boolean
        get() = isBordersActive()

    val photoOpacity: Float get() = photoDarkness

    val hasGradient: Boolean
        get() = gradientStartColor != null && gradientEndColor != null

    fun isBordersActive(): Boolean {
        return when (keyBordersMode) {
            BORDERS_ON -> true
            BORDERS_OFF -> false
            else -> photoPath.isNullOrBlank() // AUTO: ON for Dark/Light, OFF for Photo Theme
        }
    }

    fun shouldShowDismissButton(context: android.content.Context): Boolean {
        return when (dismissButtonMode) {
            DISMISS_ALWAYS -> true
            DISMISS_NEVER -> false
            else -> !com.cleverkeyboard.app.keyboard.NavigationUtils.isSystemDismissButtonAvailable(context)
        }
    }

    companion object {
        const val BORDERS_AUTO = "AUTO"
        const val BORDERS_ON = "ON"
        const val BORDERS_OFF = "OFF"

        const val DISMISS_AUTO = "AUTO"
        const val DISMISS_ALWAYS = "ALWAYS"
        const val DISMISS_NEVER = "NEVER"

        const val KEY_SHAPE_SQUARE = "SQUARE"
        const val KEY_SHAPE_ROUNDED = "ROUNDED"
        const val KEY_SHAPE_SUPER_ROUNDED = "SUPER_ROUNDED"
        const val KEY_SHAPE_PILL = "PILL"

        // =====================================================================
        // 1. GRADIENT THEMES
        // =====================================================================
        val GRADIENT_OCEAN_BLUE = KeyboardTheme(
            id = "grad_ocean_blue",
            name = "Ocean Blue",
            category = "Gradient Themes",
            keyboardBgColor = Color.parseColor("#0052D4"),
            keyBgColor = Color.parseColor("#26FFFFFF"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#60A5FA"),
            gradientStartColor = Color.parseColor("#0052D4"),
            gradientEndColor = Color.parseColor("#4364F7")
        )

        val GRADIENT_AURORA_GREEN = KeyboardTheme(
            id = "grad_aurora_green",
            name = "Aurora Green",
            category = "Gradient Themes",
            keyboardBgColor = Color.parseColor("#00BF8F"),
            keyBgColor = Color.parseColor("#26FFFFFF"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#2DD4BF"),
            gradientStartColor = Color.parseColor("#00BF8F"),
            gradientEndColor = Color.parseColor("#001510")
        )

        val GRADIENT_SUNSET_ORANGE = KeyboardTheme(
            id = "grad_sunset_orange",
            name = "Sunset Orange",
            category = "Gradient Themes",
            keyboardBgColor = Color.parseColor("#FF512F"),
            keyBgColor = Color.parseColor("#26FFFFFF"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#FDBA74"),
            gradientStartColor = Color.parseColor("#FF512F"),
            gradientEndColor = Color.parseColor("#DD2476")
        )

        val GRADIENT_ROYAL_PURPLE = KeyboardTheme(
            id = "grad_royal_purple",
            name = "Royal Purple",
            category = "Gradient Themes",
            keyboardBgColor = Color.parseColor("#654EA3"),
            keyBgColor = Color.parseColor("#26FFFFFF"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#C084FC"),
            gradientStartColor = Color.parseColor("#654EA3"),
            gradientEndColor = Color.parseColor("#EAAFC8")
        )

        val GRADIENT_PINK_BLOOM = KeyboardTheme(
            id = "grad_pink_bloom",
            name = "Pink Bloom",
            category = "Gradient Themes",
            keyboardBgColor = Color.parseColor("#FF758C"),
            keyBgColor = Color.parseColor("#26FFFFFF"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#FBCFE8"),
            gradientStartColor = Color.parseColor("#FF758C"),
            gradientEndColor = Color.parseColor("#FF7EB3")
        )

        val GRADIENT_SKY_CYAN = KeyboardTheme(
            id = "grad_sky_cyan",
            name = "Sky Cyan",
            category = "Gradient Themes",
            keyboardBgColor = Color.parseColor("#00C6FF"),
            keyBgColor = Color.parseColor("#26FFFFFF"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#38BDF8"),
            gradientStartColor = Color.parseColor("#00C6FF"),
            gradientEndColor = Color.parseColor("#0072FF")
        )

        val GRADIENT_THEMES = listOf(
            GRADIENT_OCEAN_BLUE,
            GRADIENT_AURORA_GREEN,
            GRADIENT_SUNSET_ORANGE,
            GRADIENT_ROYAL_PURPLE,
            GRADIENT_PINK_BLOOM,
            GRADIENT_SKY_CYAN
        )

        // =====================================================================
        // 2. DARK THEMES
        // =====================================================================
        val DARK_THEME = KeyboardTheme(
            id = "dark_theme",
            name = "Deep Black",
            category = "Dark Themes",
            keyboardBgColor = Color.parseColor("#000000"),
            keyBgColor = Color.parseColor("#1C1C1E"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#5B5BF0")
        )

        val DARK_GRAPHITE = KeyboardTheme(
            id = "dark_graphite",
            name = "Graphite",
            category = "Dark Themes",
            keyboardBgColor = Color.parseColor("#1F2421"),
            keyBgColor = Color.parseColor("#2E3430"),
            keyTextColor = Color.parseColor("#E5E7EB"),
            accentColor = Color.parseColor("#10B981")
        )

        val DARK_MIDNIGHT_BLUE = KeyboardTheme(
            id = "dark_midnight_blue",
            name = "Midnight Blue",
            category = "Dark Themes",
            keyboardBgColor = Color.parseColor("#0B132B"),
            keyBgColor = Color.parseColor("#1C2541"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#48CAE4")
        )

        val DARK_EMERALD_DARK = KeyboardTheme(
            id = "dark_emerald_dark",
            name = "Emerald Dark",
            category = "Dark Themes",
            keyboardBgColor = Color.parseColor("#062C21"),
            keyBgColor = Color.parseColor("#0E4435"),
            keyTextColor = Color.parseColor("#ECFDF5"),
            accentColor = Color.parseColor("#10B981")
        )

        val DARK_BURGUNDY = KeyboardTheme(
            id = "dark_burgundy",
            name = "Burgundy",
            category = "Dark Themes",
            keyboardBgColor = Color.parseColor("#2C0B14"),
            keyBgColor = Color.parseColor("#4A1525"),
            keyTextColor = Color.parseColor("#FCE7F3"),
            accentColor = Color.parseColor("#F43F5E")
        )

        val DARK_INDIGO = KeyboardTheme(
            id = "dark_indigo",
            name = "Indigo",
            category = "Dark Themes",
            keyboardBgColor = Color.parseColor("#1A1B4B"),
            keyBgColor = Color.parseColor("#2E3079"),
            keyTextColor = Color.parseColor("#EEF2FF"),
            accentColor = Color.parseColor("#6366F1")
        )

        val DARK_CHOCOLATE = KeyboardTheme(
            id = "dark_chocolate",
            name = "Chocolate",
            category = "Dark Themes",
            keyboardBgColor = Color.parseColor("#231710"),
            keyBgColor = Color.parseColor("#3D291D"),
            keyTextColor = Color.parseColor("#FEF3C7"),
            accentColor = Color.parseColor("#D97706")
        )

        val DARK_CARBON = KeyboardTheme(
            id = "dark_carbon",
            name = "Carbon",
            category = "Dark Themes",
            keyboardBgColor = Color.parseColor("#18191A"),
            keyBgColor = Color.parseColor("#242526"),
            keyTextColor = Color.parseColor("#E4E6EB"),
            accentColor = Color.parseColor("#9CA3AF")
        )

        val DARK_THEMES = listOf(
            DARK_THEME,
            DARK_GRAPHITE,
            DARK_MIDNIGHT_BLUE,
            DARK_EMERALD_DARK,
            DARK_BURGUNDY,
            DARK_INDIGO,
            DARK_CHOCOLATE,
            DARK_CARBON
        )

        // =====================================================================
        // 3. LIGHT THEMES
        // =====================================================================
        val LIGHT_THEME = KeyboardTheme(
            id = "light_theme",
            name = "Pure White",
            category = "Light Themes",
            keyboardBgColor = Color.parseColor("#FFFFFF"),
            keyBgColor = Color.parseColor("#F0F0F0"),
            keyTextColor = Color.parseColor("#111827"),
            accentColor = Color.parseColor("#5B5BF0")
        )

        val LIGHT_SOFT_BLUE = KeyboardTheme(
            id = "light_soft_blue",
            name = "Soft Blue",
            category = "Light Themes",
            keyboardBgColor = Color.parseColor("#F0F9FF"),
            keyBgColor = Color.parseColor("#E0F2FE"),
            keyTextColor = Color.parseColor("#0369A1"),
            accentColor = Color.parseColor("#0284C7")
        )

        val LIGHT_MINT_GREEN = KeyboardTheme(
            id = "light_mint_green",
            name = "Mint Green",
            category = "Light Themes",
            keyboardBgColor = Color.parseColor("#F0FDF4"),
            keyBgColor = Color.parseColor("#DCFCE7"),
            keyTextColor = Color.parseColor("#14532D"),
            accentColor = Color.parseColor("#16A34A")
        )

        val LIGHT_WARM_PEACH = KeyboardTheme(
            id = "light_warm_peach",
            name = "Warm Peach",
            category = "Light Themes",
            keyboardBgColor = Color.parseColor("#FFF7ED"),
            keyBgColor = Color.parseColor("#FFEDD5"),
            keyTextColor = Color.parseColor("#7C2D12"),
            accentColor = Color.parseColor("#EA580C")
        )

        val LIGHT_LAVENDER = KeyboardTheme(
            id = "light_lavender",
            name = "Lavender",
            category = "Light Themes",
            keyboardBgColor = Color.parseColor("#FAF5FF"),
            keyBgColor = Color.parseColor("#F3E8FF"),
            keyTextColor = Color.parseColor("#581C87"),
            accentColor = Color.parseColor("#9333EA")
        )

        val LIGHT_ROSE_PINK = KeyboardTheme(
            id = "light_rose_pink",
            name = "Rose Pink",
            category = "Light Themes",
            keyboardBgColor = Color.parseColor("#FDF2F8"),
            keyBgColor = Color.parseColor("#FCE7F3"),
            keyTextColor = Color.parseColor("#831843"),
            accentColor = Color.parseColor("#DB2777")
        )

        val LIGHT_SKY_LIGHT = KeyboardTheme(
            id = "light_sky_light",
            name = "Sky Light",
            category = "Light Themes",
            keyboardBgColor = Color.parseColor("#F0FDFA"),
            keyBgColor = Color.parseColor("#CCFBF1"),
            keyTextColor = Color.parseColor("#134E4A"),
            accentColor = Color.parseColor("#0D9488")
        )

        val LIGHT_CREAM = KeyboardTheme(
            id = "light_cream",
            name = "Cream",
            category = "Light Themes",
            keyboardBgColor = Color.parseColor("#FEFCE8"),
            keyBgColor = Color.parseColor("#FEF9C3"),
            keyTextColor = Color.parseColor("#713F12"),
            accentColor = Color.parseColor("#CA8A04")
        )

        val LIGHT_THEMES = listOf(
            LIGHT_THEME,
            LIGHT_SOFT_BLUE,
            LIGHT_MINT_GREEN,
            LIGHT_WARM_PEACH,
            LIGHT_LAVENDER,
            LIGHT_ROSE_PINK,
            LIGHT_SKY_LIGHT,
            LIGHT_CREAM
        )

        // =====================================================================
        // 4. NEON & GLOW THEMES
        // =====================================================================
        val NEON_CYAN = KeyboardTheme(
            id = "neon_cyan",
            name = "Neon Cyan",
            category = "Neon & Glow Themes",
            keyboardBgColor = Color.parseColor("#000B14"),
            keyBgColor = Color.parseColor("#002233"),
            keyTextColor = Color.parseColor("#00FFFF"),
            accentColor = Color.parseColor("#00E5FF")
        )

        val NEON_GREEN = KeyboardTheme(
            id = "neon_green",
            name = "Neon Green",
            category = "Neon & Glow Themes",
            keyboardBgColor = Color.parseColor("#001204"),
            keyBgColor = Color.parseColor("#00290A"),
            keyTextColor = Color.parseColor("#39FF14"),
            accentColor = Color.parseColor("#00FF66")
        )

        val NEON_PURPLE = KeyboardTheme(
            id = "neon_purple",
            name = "Neon Purple",
            category = "Neon & Glow Themes",
            keyboardBgColor = Color.parseColor("#0E001A"),
            keyBgColor = Color.parseColor("#22003D"),
            keyTextColor = Color.parseColor("#DF00FF"),
            accentColor = Color.parseColor("#BD00FF")
        )

        val NEON_PINK = KeyboardTheme(
            id = "neon_pink",
            name = "Neon Pink",
            category = "Neon & Glow Themes",
            keyboardBgColor = Color.parseColor("#1A000E"),
            keyBgColor = Color.parseColor("#3B0020"),
            keyTextColor = Color.parseColor("#FF007F"),
            accentColor = Color.parseColor("#FF1493")
        )

        val NEON_ELECTRIC_BLUE = KeyboardTheme(
            id = "neon_electric_blue",
            name = "Electric Blue",
            category = "Neon & Glow Themes",
            keyboardBgColor = Color.parseColor("#000E26"),
            keyBgColor = Color.parseColor("#001D4D"),
            keyTextColor = Color.parseColor("#00D4FF"),
            accentColor = Color.parseColor("#0066FF")
        )

        val NEON_LIME_GLOW = KeyboardTheme(
            id = "neon_lime_glow",
            name = "Lime Glow",
            category = "Neon & Glow Themes",
            keyboardBgColor = Color.parseColor("#0C1200"),
            keyBgColor = Color.parseColor("#1E2B00"),
            keyTextColor = Color.parseColor("#CCFF00"),
            accentColor = Color.parseColor("#A6FF00")
        )

        val NEON_THEMES = listOf(
            NEON_CYAN,
            NEON_GREEN,
            NEON_PURPLE,
            NEON_PINK,
            NEON_ELECTRIC_BLUE,
            NEON_LIME_GLOW
        )

        // =====================================================================
        // 5. NATURE THEMES
        // =====================================================================
        val NATURE_FOREST_GREEN = KeyboardTheme(
            id = "nature_forest_green",
            name = "Forest Green",
            category = "Nature Themes",
            keyboardBgColor = Color.parseColor("#0B291B"),
            keyBgColor = Color.parseColor("#174D34"),
            keyTextColor = Color.parseColor("#D1FAE5"),
            accentColor = Color.parseColor("#10B981")
        )

        val NATURE_OCEAN_WAVE = KeyboardTheme(
            id = "nature_ocean_wave",
            name = "Ocean Wave",
            category = "Nature Themes",
            keyboardBgColor = Color.parseColor("#0A3641"),
            keyBgColor = Color.parseColor("#155E75"),
            keyTextColor = Color.parseColor("#CFFAFE"),
            accentColor = Color.parseColor("#06B6D4")
        )

        val NATURE_MOUNTAIN_BLUE = KeyboardTheme(
            id = "nature_mountain_blue",
            name = "Mountain Blue",
            category = "Nature Themes",
            keyboardBgColor = Color.parseColor("#1E293B"),
            keyBgColor = Color.parseColor("#334155"),
            keyTextColor = Color.parseColor("#F1F5F9"),
            accentColor = Color.parseColor("#64748B")
        )

        val NATURE_DESERT_SAND = KeyboardTheme(
            id = "nature_desert_sand",
            name = "Desert Sand",
            category = "Nature Themes",
            keyboardBgColor = Color.parseColor("#422006"),
            keyBgColor = Color.parseColor("#713F12"),
            keyTextColor = Color.parseColor("#FEF3C7"),
            accentColor = Color.parseColor("#CA8A04")
        )

        val NATURE_CHERRY_BLOSSOM = KeyboardTheme(
            id = "nature_cherry_blossom",
            name = "Cherry Blossom",
            category = "Nature Themes",
            keyboardBgColor = Color.parseColor("#4A1528"),
            keyBgColor = Color.parseColor("#702440"),
            keyTextColor = Color.parseColor("#FCE7F3"),
            accentColor = Color.parseColor("#F472B6")
        )

        val NATURE_NIGHT_SKY = KeyboardTheme(
            id = "nature_night_sky",
            name = "Night Sky",
            category = "Nature Themes",
            keyboardBgColor = Color.parseColor("#050814"),
            keyBgColor = Color.parseColor("#0F172A"),
            keyTextColor = Color.parseColor("#E2E8F0"),
            accentColor = Color.parseColor("#818CF8")
        )

        val NATURE_THEMES = listOf(
            NATURE_FOREST_GREEN,
            NATURE_OCEAN_WAVE,
            NATURE_MOUNTAIN_BLUE,
            NATURE_DESERT_SAND,
            NATURE_CHERRY_BLOSSOM,
            NATURE_NIGHT_SKY
        )

        // =====================================================================
        // 6. MINIMAL & GLASS THEMES
        // =====================================================================
        val GLASS_FROSTED = KeyboardTheme(
            id = "glass_frosted",
            name = "Frosted Glass",
            category = "Minimal & Glass Themes",
            keyboardBgColor = Color.parseColor("#1E293B"),
            keyBgColor = Color.parseColor("#334155"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#38BDF8")
        )

        val GLASS_BLUR = KeyboardTheme(
            id = "glass_blur",
            name = "Blur Glass",
            category = "Minimal & Glass Themes",
            keyboardBgColor = Color.parseColor("#182234"),
            keyBgColor = Color.parseColor("#25334D"),
            keyTextColor = Color.parseColor("#F8FAFC"),
            accentColor = Color.parseColor("#60A5FA")
        )

        val GLASS_BLUE = KeyboardTheme(
            id = "glass_blue",
            name = "Glass Blue",
            category = "Minimal & Glass Themes",
            keyboardBgColor = Color.parseColor("#082136"),
            keyBgColor = Color.parseColor("#0E3A5E"),
            keyTextColor = Color.parseColor("#E0F2FE"),
            accentColor = Color.parseColor("#0284C7")
        )

        val GLASS_PURPLE = KeyboardTheme(
            id = "glass_purple",
            name = "Glass Purple",
            category = "Minimal & Glass Themes",
            keyboardBgColor = Color.parseColor("#210F38"),
            keyBgColor = Color.parseColor("#3B1B63"),
            keyTextColor = Color.parseColor("#F3E8FF"),
            accentColor = Color.parseColor("#A855F7")
        )

        val GLASS_TRANSPARENT = KeyboardTheme(
            id = "glass_transparent",
            name = "Transparent",
            category = "Minimal & Glass Themes",
            keyboardBgColor = Color.parseColor("#141B22"),
            keyBgColor = Color.parseColor("#2A3645"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#60A5FA")
        )

        val GLASS_MINIMAL_GRAY = KeyboardTheme(
            id = "glass_minimal_gray",
            name = "Minimal Gray",
            category = "Minimal & Glass Themes",
            keyboardBgColor = Color.parseColor("#16181A"),
            keyBgColor = Color.parseColor("#23272A"),
            keyTextColor = Color.parseColor("#CCCCCC"),
            accentColor = Color.parseColor("#888888")
        )

        val GLASS_THEMES = listOf(
            GLASS_FROSTED,
            GLASS_BLUR,
            GLASS_BLUE,
            GLASS_PURPLE,
            GLASS_TRANSPARENT,
            GLASS_MINIMAL_GRAY
        )

        // Legacy / Compatibility presets
        val MIDNIGHT_BLACK = KeyboardTheme(
            id = "midnight_black",
            name = "Midnight Black",
            category = "Dark Themes",
            keyboardBgColor = Color.parseColor("#000000"),
            keyBgColor = Color.parseColor("#1C1C1E"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#5B5BF0")
        )

        val KING_BLUE = KeyboardTheme(
            id = "king_blue",
            name = "King Blue",
            category = "Dark Themes",
            keyboardBgColor = Color.parseColor("#0A1931"),
            keyBgColor = Color.parseColor("#1A2A4A"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#5B5BF0")
        )

        val NEON_GAMER = KeyboardTheme(
            id = "neon_gamer",
            name = "Neon Gamer",
            category = "Neon & Glow Themes",
            keyboardBgColor = Color.parseColor("#000000"),
            keyBgColor = Color.parseColor("#1C1C1E"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#00FF88")
        )

        val DEFAULT_PRESETS = listOf(DARK_THEME, LIGHT_THEME, MIDNIGHT_BLACK, KING_BLUE, NEON_GAMER)

        // =====================================================================
        // SOLID COLOR THEMES - 18 Presets
        // =====================================================================
        val SOLID_BLUE = KeyboardTheme(
            id = "solid_blue",
            name = "Blue",
            category = "Solid Colors",
            keyboardBgColor = Color.parseColor("#1E3A8A"),
            keyBgColor = Color.parseColor("#2563EB"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#60A5FA")
        )

        val SOLID_TEAL = KeyboardTheme(
            id = "solid_teal",
            name = "Teal",
            category = "Solid Colors",
            keyboardBgColor = Color.parseColor("#042F2E"),
            keyBgColor = Color.parseColor("#0F766E"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#2DD4BF")
        )

        val SOLID_GREEN = KeyboardTheme(
            id = "solid_green",
            name = "Green",
            category = "Solid Colors",
            keyboardBgColor = Color.parseColor("#064E3B"),
            keyBgColor = Color.parseColor("#059669"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#34D399")
        )

        val SOLID_MINT = KeyboardTheme(
            id = "solid_mint",
            name = "Mint",
            category = "Solid Colors",
            keyboardBgColor = Color.parseColor("#022C22"),
            keyBgColor = Color.parseColor("#065F46"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#6EE7B7")
        )

        val SOLID_PURPLE = KeyboardTheme(
            id = "solid_purple",
            name = "Purple",
            category = "Solid Colors",
            keyboardBgColor = Color.parseColor("#4C1D95"),
            keyBgColor = Color.parseColor("#7C3AED"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#A78BFA")
        )

        val SOLID_LAVENDER = KeyboardTheme(
            id = "solid_lavender",
            name = "Lavender",
            category = "Solid Colors",
            keyboardBgColor = Color.parseColor("#2E1065"),
            keyBgColor = Color.parseColor("#6B21A8"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#C084FC")
        )

        val SOLID_PINK = KeyboardTheme(
            id = "solid_pink",
            name = "Pink",
            category = "Solid Colors",
            keyboardBgColor = Color.parseColor("#831843"),
            keyBgColor = Color.parseColor("#DB2777"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#F472B6")
        )

        val SOLID_ROSE = KeyboardTheme(
            id = "solid_rose",
            name = "Rose",
            category = "Solid Colors",
            keyboardBgColor = Color.parseColor("#4C0519"),
            keyBgColor = Color.parseColor("#9F1239"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#FB7185")
        )

        val SOLID_RED = KeyboardTheme(
            id = "solid_red",
            name = "Red",
            category = "Solid Colors",
            keyboardBgColor = Color.parseColor("#7F1D1D"),
            keyBgColor = Color.parseColor("#DC2626"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#F87185")
        )

        val SOLID_CRIMSON = KeyboardTheme(
            id = "solid_crimson",
            name = "Crimson",
            category = "Solid Colors",
            keyboardBgColor = Color.parseColor("#450A0A"),
            keyBgColor = Color.parseColor("#881337"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#FDA4AF")
        )

        val SOLID_ORANGE = KeyboardTheme(
            id = "solid_orange",
            name = "Orange",
            category = "Solid Colors",
            keyboardBgColor = Color.parseColor("#7C2D12"),
            keyBgColor = Color.parseColor("#EA580C"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#FB923C")
        )

        val SOLID_CORAL = KeyboardTheme(
            id = "solid_coral",
            name = "Coral",
            category = "Solid Colors",
            keyboardBgColor = Color.parseColor("#431407"),
            keyBgColor = Color.parseColor("#C2410C"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#FDBA74")
        )

        val SOLID_AMBER = KeyboardTheme(
            id = "solid_amber",
            name = "Gold",
            category = "Solid Colors",
            keyboardBgColor = Color.parseColor("#451A03"),
            keyBgColor = Color.parseColor("#B45309"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#FBBF24")
        )

        val SOLID_SKY = KeyboardTheme(
            id = "solid_sky",
            name = "Sky",
            category = "Solid Colors",
            keyboardBgColor = Color.parseColor("#082F49"),
            keyBgColor = Color.parseColor("#0284C7"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#38BDF8")
        )

        val SOLID_INDIGO = KeyboardTheme(
            id = "solid_indigo",
            name = "Indigo",
            category = "Solid Colors",
            keyboardBgColor = Color.parseColor("#1E1B4B"),
            keyBgColor = Color.parseColor("#4338CA"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#818CF8")
        )

        val SOLID_CYBERPUNK = KeyboardTheme(
            id = "solid_cyberpunk",
            name = "Cyber",
            category = "Solid Colors",
            keyboardBgColor = Color.parseColor("#130026"),
            keyBgColor = Color.parseColor("#2D0A4E"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#E024C3")
        )

        val SOLID_COFFEE = KeyboardTheme(
            id = "solid_coffee",
            name = "Coffee",
            category = "Solid Colors",
            keyboardBgColor = Color.parseColor("#231714"),
            keyBgColor = Color.parseColor("#44352D"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#D7B49E")
        )

        val SOLID_CHARCOAL = KeyboardTheme(
            id = "solid_charcoal",
            name = "Charcoal",
            category = "Solid Colors",
            keyboardBgColor = Color.parseColor("#18181B"),
            keyBgColor = Color.parseColor("#3F3F46"),
            keyTextColor = Color.parseColor("#FFFFFF"),
            accentColor = Color.parseColor("#A1A1AA")
        )

        val SOLID_PRESETS = listOf(
            SOLID_BLUE, SOLID_TEAL, SOLID_GREEN, SOLID_MINT,
            SOLID_PURPLE, SOLID_LAVENDER, SOLID_PINK, SOLID_ROSE,
            SOLID_RED, SOLID_CRIMSON, SOLID_ORANGE, SOLID_CORAL,
            SOLID_AMBER, SOLID_SKY, SOLID_INDIGO, SOLID_CYBERPUNK,
            SOLID_COFFEE, SOLID_CHARCOAL
        )

        val ALL_CATEGORIZED_THEMES: Map<String, List<KeyboardTheme>> = linkedMapOf(
            "Gradient Themes" to GRADIENT_THEMES,
            "Dark Themes" to DARK_THEMES,
            "Light Themes" to LIGHT_THEMES,
            "Neon & Glow Themes" to NEON_THEMES,
            "Nature Themes" to NATURE_THEMES,
            "Minimal & Glass Themes" to GLASS_THEMES,
            "Solid Colors" to SOLID_PRESETS
        )

        val PRESETS = GRADIENT_THEMES + DARK_THEMES + LIGHT_THEMES + NEON_THEMES + NATURE_THEMES + GLASS_THEMES + SOLID_PRESETS

        fun isColorLight(colorInt: Int): Boolean {
            val r = (colorInt shr 16) and 0xFF
            val g = (colorInt shr 8) and 0xFF
            val b = colorInt and 0xFF
            val darkness = 1 - (0.299 * r + 0.587 * g + 0.114 * b) / 255
            return darkness < 0.5
        }

        fun colorToHex(colorInt: Int): String {
            return String.format("#%06X", 0xFFFFFF and colorInt)
        }

        fun hexToColor(hex: String, defaultColor: Int): Int {
            return try {
                var clean = hex.trim()
                if (!clean.startsWith("#")) {
                    clean = "#$clean"
                }
                Color.parseColor(clean)
            } catch (e: Exception) {
                defaultColor
            }
        }
    }
}
