package com.cleverkeyboard.app.keyboard

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.cleverkeyboard.app.R

/**
 * Custom container layout for CleverKeyboard that strictly guarantees the keyboard's height
 * is governed solely by [keyboard_root] (the keys and toolbar).
 *
 * Any background layers (such as the photo ImageView and overlay tint) are constrained
 * to exactly match the measured dimensions of [keyboard_root]. This guarantees that
 * setting any photo (regardless of image aspect ratio or resolution) will NEVER expand
 * the InputMethodService window to full screen.
 */
class KeyboardContainerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val root = findViewById<View>(R.id.keyboard_root)
        val bgImage = findViewById<View>(R.id.keyboard_bg_image)
        val bgOverlay = findViewById<View>(R.id.keyboard_bg_overlay)

        if (root != null) {
            // 1. Measure keyboard_root first to determine natural keyboard height
            measureChildWithMargins(root, widthMeasureSpec, 0, heightMeasureSpec, 0)
            val measuredW = root.measuredWidth
            val measuredH = root.measuredHeight

            // 2. Force photo background and overlay to exactly match keyboard_root size
            val exactWidthSpec = MeasureSpec.makeMeasureSpec(measuredW, MeasureSpec.EXACTLY)
            val exactHeightSpec = MeasureSpec.makeMeasureSpec(measuredH, MeasureSpec.EXACTLY)

            bgImage?.measure(exactWidthSpec, exactHeightSpec)
            bgOverlay?.measure(exactWidthSpec, exactHeightSpec)

            // 3. Set container size strictly to keyboard_root dimensions
            setMeasuredDimension(
                resolveSizeAndState(measuredW, widthMeasureSpec, 0),
                resolveSizeAndState(measuredH, heightMeasureSpec, 0)
            )
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}
