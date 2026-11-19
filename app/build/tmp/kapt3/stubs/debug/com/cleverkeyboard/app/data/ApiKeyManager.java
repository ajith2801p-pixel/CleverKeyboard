package com.cleverkeyboard.app.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\u0010\u0010\f\u001a\u0004\u0018\u00010\u00042\u0006\u0010\n\u001a\u00020\u000bJ\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\n\u001a\u00020\u000bH\u0002J\u000e\u0010\u000f\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\u000bJ\u0016\u0010\u0011\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0012\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/cleverkeyboard/app/data/ApiKeyManager;", "", "()V", "FALLBACK_PREFS_NAME", "", "KEY_GEMINI_API_KEY", "SECURE_PREFS_NAME", "TAG", "clearApiKey", "", "context", "Landroid/content/Context;", "getApiKey", "getPreferences", "Landroid/content/SharedPreferences;", "hasApiKey", "", "saveApiKey", "apiKey", "app_debug"})
public final class ApiKeyManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "ApiKeyManager";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String SECURE_PREFS_NAME = "clever_keyboard_secure_prefs";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String FALLBACK_PREFS_NAME = "clever_keyboard_fallback_prefs";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_GEMINI_API_KEY = "gemini_api_key";
    @org.jetbrains.annotations.NotNull()
    public static final com.cleverkeyboard.app.data.ApiKeyManager INSTANCE = null;
    
    private ApiKeyManager() {
        super();
    }
    
    private final android.content.SharedPreferences getPreferences(android.content.Context context) {
        return null;
    }
    
    public final void saveApiKey(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getApiKey(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    public final boolean hasApiKey(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    public final void clearApiKey(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
}