package com.cleverkeyboard.app

import android.app.Application
import com.cleverkeyboard.app.data.ThemeManager

class CleverKeyboardApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize ThemeManager with application context
        ThemeManager.init(this)
    }
}

