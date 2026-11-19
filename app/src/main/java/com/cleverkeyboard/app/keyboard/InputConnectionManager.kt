package com.cleverkeyboard.app.keyboard

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection

/**
 * Encapsulated Manager for Android InputConnection operations.
 * Handles text insertion, deletion, cursor navigation, selection, and editor actions
 * reliably across all host apps (WhatsApp, Chrome, Telegram, Gmail, Notes).
 */
class InputConnectionManager {

    /**
     * Inserts text at the current cursor position or replaces active selection.
     */
    fun commitText(ic: InputConnection?, text: CharSequence) {
        if (ic == null) return
        ic.commitText(text, 1)
    }

    /**
     * Deletes the character before the cursor or removes active selection.
     */
    fun deleteBackward(ic: InputConnection?): Boolean {
        if (ic == null) return false
        val selected = ic.getSelectedText(0)
        return if (!selected.isNullOrEmpty()) {
            ic.commitText("", 1)
            true
        } else {
            ic.deleteSurroundingText(1, 0)
        }
    }

    /**
     * Moves cursor relative to current position.
     * direction: -1 = left, +1 = right
     */
    fun moveCursor(ic: InputConnection?, direction: Int) {
        if (ic == null) return
        val eventCode = if (direction < 0) KeyEvent.KEYCODE_DPAD_LEFT else KeyEvent.KEYCODE_DPAD_RIGHT
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, eventCode))
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, eventCode))
    }

    /**
     * Gets the current word being typed before the cursor.
     */
    fun getCurrentWord(ic: InputConnection?): String {
        if (ic == null) return ""
        val before = ic.getTextBeforeCursor(50, 0)?.toString() ?: ""
        val match = Regex("[\\p{L}\\p{M}\\p{N}']+$").find(before)
        return match?.value ?: ""
    }

    /**
     * Replaces the currently typed word with a replacement string and trailing space.
     */
    fun replaceCurrentWord(ic: InputConnection?, currentWord: String, replacement: String) {
        if (ic == null) return
        if (currentWord.isNotEmpty()) {
            ic.deleteSurroundingText(currentWord.length, 0)
        }
        ic.commitText("$replacement ", 1)
    }

    /**
     * Handles Enter key action based on EditorInfo.
     * Inserts newline in multi-line editors; triggers action in single-line fields.
     */
    fun handleEnter(ic: InputConnection?, info: EditorInfo?, performEditorAction: (Int) -> Unit) {
        if (ic == null) return
        val isMultiLine = (info?.inputType?.and(EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE) ?: 0) != 0

        if (isMultiLine) {
            ic.commitText("\n", 1)
        } else {
            val action = info?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: EditorInfo.IME_ACTION_NONE
            if (action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
                performEditorAction(action)
            } else {
                ic.commitText("\n", 1)
            }
        }
    }
}
