package com.cleverkeyboard.app.autofill;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00040\u000e2\u0006\u0010\u000b\u001a\u00020\fJ\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00040\u000e2\u0006\u0010\u000b\u001a\u00020\fJ\u0010\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013J\u0010\u0010\u0014\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013J\u001e\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0017\u001a\u00020\u00042\u0006\u0010\u0018\u001a\u00020\u0004J\u0016\u0010\u0019\u001a\u00020\u00162\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u001a\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001b"}, d2 = {"Lcom/cleverkeyboard/app/autofill/UserAutofillManager;", "", "()V", "KEY_USER_FIRST_NAME", "", "KEY_USER_FULL_NAME", "KEY_USER_PHONE_PRIMARY", "KEY_USER_PHONE_SECONDARY", "PREFS_NAME", "getPrefs", "Landroid/content/SharedPreferences;", "context", "Landroid/content/Context;", "getUserNames", "", "getUserPhoneNumbers", "isNameField", "", "info", "Landroid/view/inputmethod/EditorInfo;", "isPhoneOrNumberField", "saveUserName", "", "firstName", "fullName", "saveUserPhone", "phone", "app_debug"})
public final class UserAutofillManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "clever_user_autofill_prefs";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_USER_FIRST_NAME = "user_first_name";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_USER_FULL_NAME = "user_full_name";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_USER_PHONE_PRIMARY = "user_phone_primary";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_USER_PHONE_SECONDARY = "user_phone_secondary";
    @org.jetbrains.annotations.NotNull()
    public static final com.cleverkeyboard.app.autofill.UserAutofillManager INSTANCE = null;
    
    private UserAutofillManager() {
        super();
    }
    
    private final android.content.SharedPreferences getPrefs(android.content.Context context) {
        return null;
    }
    
    public final boolean isNameField(@org.jetbrains.annotations.Nullable()
    android.view.inputmethod.EditorInfo info) {
        return false;
    }
    
    public final boolean isPhoneOrNumberField(@org.jetbrains.annotations.Nullable()
    android.view.inputmethod.EditorInfo info) {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getUserNames(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getUserPhoneNumbers(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    public final void saveUserName(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String firstName, @org.jetbrains.annotations.NotNull()
    java.lang.String fullName) {
    }
    
    public final void saveUserPhone(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String phone) {
    }
}