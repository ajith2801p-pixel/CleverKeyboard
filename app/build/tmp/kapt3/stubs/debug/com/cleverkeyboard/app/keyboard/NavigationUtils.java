package com.cleverkeyboard.app.keyboard;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/cleverkeyboard/app/keyboard/NavigationUtils;", "", "()V", "isSystemDismissButtonAvailable", "", "context", "Landroid/content/Context;", "app_debug"})
public final class NavigationUtils {
    @org.jetbrains.annotations.NotNull()
    public static final com.cleverkeyboard.app.keyboard.NavigationUtils INSTANCE = null;
    
    private NavigationUtils() {
        super();
    }
    
    /**
     * Determines whether the host device already provides an OS-level keyboard dismiss
     * button (such as the down arrow ⌄ in the bottom navigation bar on Samsung, OnePlus, Oppo,
     * Realme, or the system back button in 3-button and 2-button navigation).
     *
     * Returns true if a system dismiss button is present -> No keyboard dismiss option needed.
     * Returns false if the device does NOT have a system dismiss button (e.g. Google Pixel gesture
     * navigation, Motorola, Stock AOSP, or hidden navigation bar) -> Show keyboard dismiss button.
     */
    public final boolean isSystemDismissButtonAvailable(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
}