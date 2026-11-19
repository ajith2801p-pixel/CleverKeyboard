package com.cleverkeyboard.app.keyboard

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.ViewConfiguration

object NavigationUtils {

    /**
     * Determines whether the host device already provides an OS-level keyboard dismiss
     * button (such as the down arrow ⌄ in the bottom navigation bar on Samsung, OnePlus, Oppo,
     * Realme, or the system back button in 3-button and 2-button navigation).
     *
     * Returns true if a system dismiss button is present -> No keyboard dismiss option needed.
     * Returns false if the device does NOT have a system dismiss button (e.g. Google Pixel gesture
     * navigation, Motorola, Stock AOSP, or hidden navigation bar) -> Show keyboard dismiss button.
     */
    fun isSystemDismissButtonAvailable(context: Context): Boolean {
        try {
            val cr = context.contentResolver

            // 1. Navigation mode:
            // 0 = 3-button navigation (Back button transforms to ⌄ to dismiss keyboard)
            // 1 = 2-button navigation (Back button is present to dismiss keyboard)
            // 2 = Full-screen gesture navigation
            val navMode = try {
                Settings.Secure.getInt(cr, "navigation_mode", -1)
            } catch (_: Exception) {
                -1
            }

            if (navMode == 0 || navMode == 1) {
                return true
            }

            // Check if device has physical hardware back/menu buttons
            val hasPermanentMenu = ViewConfiguration.get(context).hasPermanentMenuKey()
            val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
            if (hasPermanentMenu && hasBackKey) {
                return true
            }

            // 2. Gesture Navigation: Check OEM-specific implementations
            val brand = Build.MANUFACTURER.lowercase()

            // Samsung OneUI: "Show button to hide keyboard"
            if (brand.contains("samsung")) {
                val showKbBtn = try { Settings.Secure.getInt(cr, "show_keyboard_button", 0) } catch (_: Exception) { 0 }
                val gestureHint = try { Settings.Global.getInt(cr, "navigation_bar_gesture_hint", 1) } catch (_: Exception) { 1 }
                return showKbBtn == 1 && gestureHint == 1
            }

            // Vivo / iQOO (Funtouch OS / OriginOS) - provides system dismiss down arrow in nav bar
            if (brand.contains("vivo") || brand.contains("iqoo")) {
                return true
            }

            // OnePlus / Oppo / Realme (ColorOS / OxygenOS)
            if (brand.contains("oneplus") || brand.contains("oppo") || brand.contains("realme")) {
                val navBarDismiss = try { Settings.Secure.getInt(cr, "nav_bar_keyboard_dismiss", 1) } catch (_: Exception) { 1 }
                val hideNavBar = try { Settings.Secure.getInt(cr, "hide_navigation_bar_enable", 0) } catch (_: Exception) { 0 }
                if (navBarDismiss == 1 && hideNavBar == 0) {
                    return true
                }
                val navKeys = try { Settings.System.getInt(cr, "buttons_show_on_screen_navkeys", 0) } catch (_: Exception) { 0 }
                if (navKeys == 1) {
                    return true
                }
            }

            // Xiaomi / Redmi / Poco (MIUI / HyperOS)
            if (brand.contains("xiaomi") || brand.contains("redmi") || brand.contains("poco")) {
                val forceFsg = try { Settings.Global.getInt(cr, "force_fsg_nav_bar", 0) } catch (_: Exception) { 0 }
                if (forceFsg == 1) {
                    return false
                }
                return true
            }

            // Huawei / Honor (EMUI / MagicOS)
            if (brand.contains("huawei") || brand.contains("honor")) {
                return true
            }

            // Transsion: Tecno / Infinix / Itel (HiOS / XOS)
            if (brand.contains("tecno") || brand.contains("infinix") || brand.contains("itel") || brand.contains("transsion")) {
                return true
            }

            // Generic / Stock Android / Google Pixel gesture navigation does NOT have a dismiss button
            if (navMode == 2) {
                return false
            }

            return false
        } catch (e: Exception) {
            return false
        }
    }
}
