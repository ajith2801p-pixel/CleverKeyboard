package com.cleverkeyboard.app.clipboard;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\rR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/cleverkeyboard/app/clipboard/ScreenshotManager;", "", "()V", "TAG", "", "getLatestScreenshot", "Lcom/cleverkeyboard/app/clipboard/ScreenshotItem;", "context", "Landroid/content/Context;", "isScreenshotVeryRecent", "", "item", "maxSeconds", "", "app_debug"})
public final class ScreenshotManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "ScreenshotManager";
    @org.jetbrains.annotations.NotNull()
    public static final com.cleverkeyboard.app.clipboard.ScreenshotManager INSTANCE = null;
    
    private ScreenshotManager() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.cleverkeyboard.app.clipboard.ScreenshotItem getLatestScreenshot(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    public final boolean isScreenshotVeryRecent(@org.jetbrains.annotations.NotNull()
    com.cleverkeyboard.app.clipboard.ScreenshotItem item, long maxSeconds) {
        return false;
    }
}