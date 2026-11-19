package com.cleverkeyboard.app.security;

/**
 * Dedicated Security & Privacy Guard for Sensitive Input Fields.
 * Prevents AI processing, suggestions persistence, clipboard harvesting,
 * and dictionary learning during password, PIN, OTP, and confidential entry.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006J\u0010\u0010\u0007\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006J\u0010\u0010\b\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006J\u0010\u0010\t\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a8\u0006\n"}, d2 = {"Lcom/cleverkeyboard/app/security/SensitiveFieldGuard;", "", "()V", "isAiAllowed", "", "info", "Landroid/view/inputmethod/EditorInfo;", "isLearningAllowed", "isSensitiveField", "isSuggestionsAllowed", "app_debug"})
public final class SensitiveFieldGuard {
    @org.jetbrains.annotations.NotNull()
    public static final com.cleverkeyboard.app.security.SensitiveFieldGuard INSTANCE = null;
    
    private SensitiveFieldGuard() {
        super();
    }
    
    /**
     * Determines whether the given editor represents a sensitive field (Password, PIN, OTP).
     */
    public final boolean isSensitiveField(@org.jetbrains.annotations.Nullable()
    android.view.inputmethod.EditorInfo info) {
        return false;
    }
    
    /**
     * Checks if AI writing or grammar fixing is permitted on this field.
     * Strictly blocked on passwords, PINs, and banking credentials.
     */
    public final boolean isAiAllowed(@org.jetbrains.annotations.Nullable()
    android.view.inputmethod.EditorInfo info) {
        return false;
    }
    
    /**
     * Checks if words typed in this field can be recorded in personal dictionary.
     */
    public final boolean isLearningAllowed(@org.jetbrains.annotations.Nullable()
    android.view.inputmethod.EditorInfo info) {
        return false;
    }
    
    /**
     * Checks if predictive suggestions bar should be displayed.
     */
    public final boolean isSuggestionsAllowed(@org.jetbrains.annotations.Nullable()
    android.view.inputmethod.EditorInfo info) {
        return false;
    }
}