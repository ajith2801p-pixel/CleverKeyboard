package com.cleverkeyboard.app.ui.viewmodel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0016\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J\u0010\u0010\u0010\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J\u0006\u0010\u0011\u001a\u00020\u0012JB\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00172\u0006\u0010\u0019\u001a\u00020\u00172\u0006\u0010\u001a\u001a\u00020\u00172\b\b\u0002\u0010\u001b\u001a\u00020\u00152\b\b\u0002\u0010\u001c\u001a\u00020\u0015J\u000e\u0010\u001d\u001a\u00020\u00122\u0006\u0010\u001e\u001a\u00020\u0015J\u0006\u0010\u001f\u001a\u00020\u0012J\u000e\u0010 \u001a\u00020\u00122\u0006\u0010!\u001a\u00020\u0015J\u000e\u0010\"\u001a\u00020\u00122\u0006\u0010#\u001a\u00020\u0017J\u000e\u0010$\u001a\u00020\u00122\u0006\u0010%\u001a\u00020&J\u0006\u0010\'\u001a\u00020\u0012J\u0006\u0010(\u001a\u00020\u0012J\u0006\u0010)\u001a\u00020\u0012J\u0006\u0010*\u001a\u00020\u0012J\u000e\u0010+\u001a\u00020\u00122\u0006\u0010,\u001a\u00020-J\u000e\u0010.\u001a\u00020\u00122\u0006\u0010/\u001a\u00020\u0017J\u000e\u00100\u001a\u00020\u00122\u0006\u00101\u001a\u00020\u0015J\u000e\u00102\u001a\u00020\u00122\u0006\u00103\u001a\u00020\u0015J\u000e\u00104\u001a\u00020\u00122\u0006\u00103\u001a\u00020\u0015J\u000e\u00105\u001a\u00020\u00122\u0006\u00106\u001a\u00020\u0015J\u000e\u00107\u001a\u00020\u00122\u0006\u00108\u001a\u00020\u0017J$\u00109\u001a\u00020\u00122\b\u0010:\u001a\u0004\u0018\u00010\u00152\b\b\u0002\u0010;\u001a\u00020<2\b\b\u0002\u0010=\u001a\u00020<J\u0016\u0010>\u001a\u00020\u00122\u0006\u0010#\u001a\u00020\u00172\u0006\u0010\u0014\u001a\u00020\u0015J\u000e\u0010?\u001a\u00020\u00122\u0006\u00103\u001a\u00020@J\u000e\u0010A\u001a\u00020\u00122\u0006\u0010B\u001a\u00020\rJ\u000e\u0010C\u001a\u00020\u00122\u0006\u0010B\u001a\u00020\rJ\u000e\u0010D\u001a\u00020\u00122\u0006\u0010B\u001a\u00020\rJ\u000e\u0010E\u001a\u00020\u00122\u0006\u0010B\u001a\u00020\rJ\u000e\u0010F\u001a\u00020\u00122\u0006\u0010B\u001a\u00020\rJ\u000e\u0010G\u001a\u00020\u00122\u0006\u0010B\u001a\u00020\rJ\u000e\u0010H\u001a\u00020\u00122\u0006\u0010B\u001a\u00020\rJ\u000e\u0010I\u001a\u00020\u00122\u0006\u0010B\u001a\u00020\rJ\u000e\u0010J\u001a\u00020\u00122\u0006\u0010B\u001a\u00020\rJ\u000e\u0010K\u001a\u00020\u00122\u0006\u0010B\u001a\u00020\rJ\u000e\u0010L\u001a\u00020\u00122\u0006\u0010B\u001a\u00020\rJ\u000e\u0010M\u001a\u00020\u00122\u0006\u0010B\u001a\u00020\rJ\u000e\u0010N\u001a\u00020\u00122\u0006\u0010B\u001a\u00020\rJ\u000e\u0010O\u001a\u00020\u00122\u0006\u0010B\u001a\u00020\rJ\u000e\u0010P\u001a\u00020\u00122\u0006\u0010B\u001a\u00020\rJ\u000e\u0010Q\u001a\u00020\u00122\u0006\u0010B\u001a\u00020\rJ\u000e\u0010R\u001a\u00020\u00122\u0006\u0010=\u001a\u00020<J\u000e\u0010S\u001a\u00020\u00122\u0006\u0010;\u001a\u00020<J\u000e\u0010T\u001a\u00020\u00122\u0006\u0010U\u001a\u00020<R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006V"}, d2 = {"Lcom/cleverkeyboard/app/ui/viewmodel/SettingsViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/cleverkeyboard/app/ui/viewmodel/SettingsUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "checkKeyboardEnabled", "", "context", "Landroid/content/Context;", "checkKeyboardSelected", "closeColorPicker", "", "createAndSaveCustomTheme", "name", "", "keyboardBgColor", "", "keyBgColor", "keyTextColor", "accentColor", "keyShape", "keyBordersMode", "deleteCustomTheme", "themeId", "loadInitialState", "onApiKeyChanged", "newKey", "onColorSelected", "colorInt", "openColorPicker", "target", "Lcom/cleverkeyboard/app/ui/viewmodel/ColorTarget;", "refreshKeyboardStatus", "removePhotoTheme", "resetPersonalVocabulary", "saveApiKey", "selectPreset", "preset", "Lcom/cleverkeyboard/app/data/KeyboardTheme;", "selectTab", "tabIndex", "setActiveLanguage", "lang", "setDismissButtonMode", "mode", "setKeyBordersMode", "setKeyShape", "shape", "setKeyboardHeight", "percent", "setPhotoTheme", "photoPath", "darkness", "", "blur", "setSolidColor", "setTanglishOutputMode", "Lcom/cleverkeyboard/app/intelligence/TanglishOutputMode;", "toggleAiToolbarLabels", "enabled", "toggleAutoCapitalization", "toggleAutoCorrect", "toggleAutoNumPad", "toggleContextPrediction", "toggleGrammarCheck", "toggleKeyBorders", "toggleNextWordPrediction", "toggleNumberRow", "togglePersonalizedLearning", "togglePersonalizedSuggestions", "toggleSentenceCompletion", "toggleSmartPunctuation", "toggleSound", "toggleSwipeTyping", "toggleVibration", "updatePhotoBlur", "updatePhotoDarkness", "updatePhotoOpacity", "opacity", "app_debug"})
public final class SettingsViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.cleverkeyboard.app.ui.viewmodel.SettingsUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.cleverkeyboard.app.ui.viewmodel.SettingsUiState> uiState = null;
    
    public SettingsViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.cleverkeyboard.app.ui.viewmodel.SettingsUiState> getUiState() {
        return null;
    }
    
    public final void selectTab(int tabIndex) {
    }
    
    public final void loadInitialState() {
    }
    
    public final void toggleContextPrediction(boolean enabled) {
    }
    
    public final void togglePersonalizedLearning(boolean enabled) {
    }
    
    public final void toggleGrammarCheck(boolean enabled) {
    }
    
    public final void toggleSentenceCompletion(boolean enabled) {
    }
    
    public final void setTanglishOutputMode(@org.jetbrains.annotations.NotNull()
    com.cleverkeyboard.app.intelligence.TanglishOutputMode mode) {
    }
    
    public final void resetPersonalVocabulary() {
    }
    
    public final void setKeyShape(@org.jetbrains.annotations.NotNull()
    java.lang.String shape) {
    }
    
    public final void setActiveLanguage(@org.jetbrains.annotations.NotNull()
    java.lang.String lang) {
    }
    
    public final void setKeyboardHeight(int percent) {
    }
    
    public final void toggleSwipeTyping(boolean enabled) {
    }
    
    public final void toggleSmartPunctuation(boolean enabled) {
    }
    
    public final void toggleAutoCorrect(boolean enabled) {
    }
    
    public final void toggleNumberRow(boolean enabled) {
    }
    
    public final void setKeyBordersMode(@org.jetbrains.annotations.NotNull()
    java.lang.String mode) {
    }
    
    public final void toggleKeyBorders(boolean enabled) {
    }
    
    public final void toggleAutoCapitalization(boolean enabled) {
    }
    
    public final void toggleAiToolbarLabels(boolean enabled) {
    }
    
    public final void toggleAutoNumPad(boolean enabled) {
    }
    
    public final void toggleNextWordPrediction(boolean enabled) {
    }
    
    public final void togglePersonalizedSuggestions(boolean enabled) {
    }
    
    public final void setDismissButtonMode(@org.jetbrains.annotations.NotNull()
    java.lang.String mode) {
    }
    
    public final void toggleSound(boolean enabled) {
    }
    
    public final void toggleVibration(boolean enabled) {
    }
    
    public final void onApiKeyChanged(@org.jetbrains.annotations.NotNull()
    java.lang.String newKey) {
    }
    
    public final void saveApiKey() {
    }
    
    public final void selectPreset(@org.jetbrains.annotations.NotNull()
    com.cleverkeyboard.app.data.KeyboardTheme preset) {
    }
    
    public final void createAndSaveCustomTheme(@org.jetbrains.annotations.NotNull()
    java.lang.String name, int keyboardBgColor, int keyBgColor, int keyTextColor, int accentColor, @org.jetbrains.annotations.NotNull()
    java.lang.String keyShape, @org.jetbrains.annotations.NotNull()
    java.lang.String keyBordersMode) {
    }
    
    public final void deleteCustomTheme(@org.jetbrains.annotations.NotNull()
    java.lang.String themeId) {
    }
    
    public final void setSolidColor(int colorInt, @org.jetbrains.annotations.NotNull()
    java.lang.String name) {
    }
    
    public final void setPhotoTheme(@org.jetbrains.annotations.Nullable()
    java.lang.String photoPath, float darkness, float blur) {
    }
    
    public final void updatePhotoDarkness(float darkness) {
    }
    
    public final void updatePhotoOpacity(float opacity) {
    }
    
    public final void updatePhotoBlur(float blur) {
    }
    
    public final void removePhotoTheme() {
    }
    
    public final void openColorPicker(@org.jetbrains.annotations.NotNull()
    com.cleverkeyboard.app.ui.viewmodel.ColorTarget target) {
    }
    
    public final void closeColorPicker() {
    }
    
    public final void onColorSelected(int colorInt) {
    }
    
    public final void refreshKeyboardStatus() {
    }
    
    private final boolean checkKeyboardEnabled(android.content.Context context) {
        return false;
    }
    
    private final boolean checkKeyboardSelected(android.content.Context context) {
        return false;
    }
}