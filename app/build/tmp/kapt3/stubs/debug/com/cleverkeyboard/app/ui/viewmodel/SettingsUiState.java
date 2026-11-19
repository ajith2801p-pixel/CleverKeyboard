package com.cleverkeyboard.app.ui.viewmodel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\bC\b\u0086\b\u0018\u00002\u00020\u0001B\u00c3\u0002\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\t\u001a\u00020\u0007\u0012\b\b\u0002\u0010\n\u001a\u00020\u0007\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0005\u0012\b\b\u0002\u0010\f\u001a\u00020\u0007\u0012\b\b\u0002\u0010\r\u001a\u00020\u0007\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u0007\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0007\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0007\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0007\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0007\u0012\b\b\u0002\u0010\u0013\u001a\u00020\u0007\u0012\b\b\u0002\u0010\u0014\u001a\u00020\u0015\u0012\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0017\u001a\u00020\u0007\u0012\b\b\u0002\u0010\u0018\u001a\u00020\u0007\u0012\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u001a\u0012\b\b\u0002\u0010\u001b\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u001c\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u001d\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u001e\u001a\u00020\u0007\u0012\b\b\u0002\u0010\u001f\u001a\u00020\u0007\u0012\b\b\u0002\u0010 \u001a\u00020\u0007\u0012\b\b\u0002\u0010!\u001a\u00020\u0007\u0012\b\b\u0002\u0010\"\u001a\u00020\u0007\u0012\b\b\u0002\u0010#\u001a\u00020\u0007\u0012\b\b\u0002\u0010$\u001a\u00020\u0005\u0012\n\b\u0002\u0010%\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010&J\t\u00109\u001a\u00020\u0003H\u00c6\u0003J\t\u0010:\u001a\u00020\u0007H\u00c6\u0003J\t\u0010;\u001a\u00020\u0007H\u00c6\u0003J\t\u0010<\u001a\u00020\u0007H\u00c6\u0003J\t\u0010=\u001a\u00020\u0007H\u00c6\u0003J\t\u0010>\u001a\u00020\u0007H\u00c6\u0003J\t\u0010?\u001a\u00020\u0007H\u00c6\u0003J\t\u0010@\u001a\u00020\u0015H\u00c6\u0003J\u000b\u0010A\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010B\u001a\u00020\u0007H\u00c6\u0003J\t\u0010C\u001a\u00020\u0007H\u00c6\u0003J\t\u0010D\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010E\u001a\u0004\u0018\u00010\u001aH\u00c6\u0003J\t\u0010F\u001a\u00020\u0005H\u00c6\u0003J\t\u0010G\u001a\u00020\u0005H\u00c6\u0003J\t\u0010H\u001a\u00020\u0003H\u00c6\u0003J\t\u0010I\u001a\u00020\u0007H\u00c6\u0003J\t\u0010J\u001a\u00020\u0007H\u00c6\u0003J\t\u0010K\u001a\u00020\u0007H\u00c6\u0003J\t\u0010L\u001a\u00020\u0007H\u00c6\u0003J\t\u0010M\u001a\u00020\u0007H\u00c6\u0003J\t\u0010N\u001a\u00020\u0007H\u00c6\u0003J\t\u0010O\u001a\u00020\u0007H\u00c6\u0003J\t\u0010P\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010Q\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010R\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010S\u001a\u00020\u0007H\u00c6\u0003J\t\u0010T\u001a\u00020\u0007H\u00c6\u0003J\t\u0010U\u001a\u00020\u0005H\u00c6\u0003J\t\u0010V\u001a\u00020\u0007H\u00c6\u0003J\t\u0010W\u001a\u00020\u0007H\u00c6\u0003J\u00c7\u0002\u0010X\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\t\u001a\u00020\u00072\b\b\u0002\u0010\n\u001a\u00020\u00072\b\b\u0002\u0010\u000b\u001a\u00020\u00052\b\b\u0002\u0010\f\u001a\u00020\u00072\b\b\u0002\u0010\r\u001a\u00020\u00072\b\b\u0002\u0010\u000e\u001a\u00020\u00072\b\b\u0002\u0010\u000f\u001a\u00020\u00072\b\b\u0002\u0010\u0010\u001a\u00020\u00072\b\b\u0002\u0010\u0011\u001a\u00020\u00072\b\b\u0002\u0010\u0012\u001a\u00020\u00072\b\b\u0002\u0010\u0013\u001a\u00020\u00072\b\b\u0002\u0010\u0014\u001a\u00020\u00152\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0017\u001a\u00020\u00072\b\b\u0002\u0010\u0018\u001a\u00020\u00072\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u001a2\b\b\u0002\u0010\u001b\u001a\u00020\u00052\b\b\u0002\u0010\u001c\u001a\u00020\u00052\b\b\u0002\u0010\u001d\u001a\u00020\u00032\b\b\u0002\u0010\u001e\u001a\u00020\u00072\b\b\u0002\u0010\u001f\u001a\u00020\u00072\b\b\u0002\u0010 \u001a\u00020\u00072\b\b\u0002\u0010!\u001a\u00020\u00072\b\b\u0002\u0010\"\u001a\u00020\u00072\b\b\u0002\u0010#\u001a\u00020\u00072\b\b\u0002\u0010$\u001a\u00020\u00052\n\b\u0002\u0010%\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001J\u0013\u0010Y\u001a\u00020\u00072\b\u0010Z\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010[\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\\\u001a\u00020\u0005H\u00d6\u0001R\u0013\u0010\u0019\u001a\u0004\u0018\u00010\u001a\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010(R\u0011\u0010\u001c\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010*R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010*R\u0013\u0010\b\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010*R\u0011\u0010\u0014\u001a\u00020\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010.R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u00100R\u0011\u0010\u000e\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u00100R\u0011\u0010\r\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u00100R\u0011\u0010\t\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u00100R\u0011\u0010\u000f\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u00100R\u0011\u0010 \u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b \u00100R\u0011\u0010\"\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u00100R\u0011\u0010\f\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u00100R\u0011\u0010\u0017\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u00100R\u0011\u0010\u0018\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u00100R\u0011\u0010\u0010\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u00100R\u0011\u0010\n\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u00100R\u0011\u0010!\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u00100R\u0011\u0010\u0011\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u00100R\u0011\u0010#\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u00100R\u0011\u0010\u001f\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u00100R\u0011\u0010\u0012\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u00100R\u0011\u0010\u001e\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u00100R\u0011\u0010\u0013\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u00100R\u0011\u0010\u000b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b1\u0010*R\u0011\u0010\u001b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010*R\u0011\u0010\u001d\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b3\u00104R\u0013\u0010\u0016\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b5\u0010*R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b6\u00104R\u0011\u0010$\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b7\u0010*R\u0013\u0010%\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b8\u0010*\u00a8\u0006]"}, d2 = {"Lcom/cleverkeyboard/app/ui/viewmodel/SettingsUiState;", "", "selectedTab", "", "apiKey", "", "hasApiKey", "", "apiKeySavedMessage", "isAutoCorrectEnabled", "isNumberRowEnabled", "keyBordersMode", "isKeyBordersEnabled", "isAutoCapitalizationEnabled", "isAiToolbarLabelsEnabled", "isAutoNumPadEnabled", "isNextWordPredictionEnabled", "isPersonalizedSuggestionsEnabled", "isSoundEnabled", "isVibrationEnabled", "currentTheme", "Lcom/cleverkeyboard/app/data/KeyboardTheme;", "selectedPresetId", "isKeyboardEnabled", "isKeyboardSelected", "activeColorTarget", "Lcom/cleverkeyboard/app/ui/viewmodel/ColorTarget;", "keyShape", "activeLanguage", "keyboardHeightPercent", "isSwipeTypingEnabled", "isSmartPunctuationEnabled", "isContextPredictionEnabled", "isPersonalizedLearningEnabled", "isGrammarCheckEnabled", "isSentenceCompletionEnabled", "tanglishOutputMode", "vocabularyResetMessage", "(ILjava/lang/String;ZLjava/lang/String;ZZLjava/lang/String;ZZZZZZZZLcom/cleverkeyboard/app/data/KeyboardTheme;Ljava/lang/String;ZZLcom/cleverkeyboard/app/ui/viewmodel/ColorTarget;Ljava/lang/String;Ljava/lang/String;IZZZZZZLjava/lang/String;Ljava/lang/String;)V", "getActiveColorTarget", "()Lcom/cleverkeyboard/app/ui/viewmodel/ColorTarget;", "getActiveLanguage", "()Ljava/lang/String;", "getApiKey", "getApiKeySavedMessage", "getCurrentTheme", "()Lcom/cleverkeyboard/app/data/KeyboardTheme;", "getHasApiKey", "()Z", "getKeyBordersMode", "getKeyShape", "getKeyboardHeightPercent", "()I", "getSelectedPresetId", "getSelectedTab", "getTanglishOutputMode", "getVocabularyResetMessage", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component20", "component21", "component22", "component23", "component24", "component25", "component26", "component27", "component28", "component29", "component3", "component30", "component31", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "app_debug"})
public final class SettingsUiState {
    private final int selectedTab = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String apiKey = null;
    private final boolean hasApiKey = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String apiKeySavedMessage = null;
    private final boolean isAutoCorrectEnabled = false;
    private final boolean isNumberRowEnabled = false;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String keyBordersMode = null;
    private final boolean isKeyBordersEnabled = false;
    private final boolean isAutoCapitalizationEnabled = false;
    private final boolean isAiToolbarLabelsEnabled = false;
    private final boolean isAutoNumPadEnabled = false;
    private final boolean isNextWordPredictionEnabled = false;
    private final boolean isPersonalizedSuggestionsEnabled = false;
    private final boolean isSoundEnabled = false;
    private final boolean isVibrationEnabled = false;
    @org.jetbrains.annotations.NotNull()
    private final com.cleverkeyboard.app.data.KeyboardTheme currentTheme = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String selectedPresetId = null;
    private final boolean isKeyboardEnabled = false;
    private final boolean isKeyboardSelected = false;
    @org.jetbrains.annotations.Nullable()
    private final com.cleverkeyboard.app.ui.viewmodel.ColorTarget activeColorTarget = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String keyShape = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String activeLanguage = null;
    private final int keyboardHeightPercent = 0;
    private final boolean isSwipeTypingEnabled = false;
    private final boolean isSmartPunctuationEnabled = false;
    private final boolean isContextPredictionEnabled = false;
    private final boolean isPersonalizedLearningEnabled = false;
    private final boolean isGrammarCheckEnabled = false;
    private final boolean isSentenceCompletionEnabled = false;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String tanglishOutputMode = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String vocabularyResetMessage = null;
    
    public SettingsUiState(int selectedTab, @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, boolean hasApiKey, @org.jetbrains.annotations.Nullable()
    java.lang.String apiKeySavedMessage, boolean isAutoCorrectEnabled, boolean isNumberRowEnabled, @org.jetbrains.annotations.NotNull()
    java.lang.String keyBordersMode, boolean isKeyBordersEnabled, boolean isAutoCapitalizationEnabled, boolean isAiToolbarLabelsEnabled, boolean isAutoNumPadEnabled, boolean isNextWordPredictionEnabled, boolean isPersonalizedSuggestionsEnabled, boolean isSoundEnabled, boolean isVibrationEnabled, @org.jetbrains.annotations.NotNull()
    com.cleverkeyboard.app.data.KeyboardTheme currentTheme, @org.jetbrains.annotations.Nullable()
    java.lang.String selectedPresetId, boolean isKeyboardEnabled, boolean isKeyboardSelected, @org.jetbrains.annotations.Nullable()
    com.cleverkeyboard.app.ui.viewmodel.ColorTarget activeColorTarget, @org.jetbrains.annotations.NotNull()
    java.lang.String keyShape, @org.jetbrains.annotations.NotNull()
    java.lang.String activeLanguage, int keyboardHeightPercent, boolean isSwipeTypingEnabled, boolean isSmartPunctuationEnabled, boolean isContextPredictionEnabled, boolean isPersonalizedLearningEnabled, boolean isGrammarCheckEnabled, boolean isSentenceCompletionEnabled, @org.jetbrains.annotations.NotNull()
    java.lang.String tanglishOutputMode, @org.jetbrains.annotations.Nullable()
    java.lang.String vocabularyResetMessage) {
        super();
    }
    
    public final int getSelectedTab() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getApiKey() {
        return null;
    }
    
    public final boolean getHasApiKey() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getApiKeySavedMessage() {
        return null;
    }
    
    public final boolean isAutoCorrectEnabled() {
        return false;
    }
    
    public final boolean isNumberRowEnabled() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getKeyBordersMode() {
        return null;
    }
    
    public final boolean isKeyBordersEnabled() {
        return false;
    }
    
    public final boolean isAutoCapitalizationEnabled() {
        return false;
    }
    
    public final boolean isAiToolbarLabelsEnabled() {
        return false;
    }
    
    public final boolean isAutoNumPadEnabled() {
        return false;
    }
    
    public final boolean isNextWordPredictionEnabled() {
        return false;
    }
    
    public final boolean isPersonalizedSuggestionsEnabled() {
        return false;
    }
    
    public final boolean isSoundEnabled() {
        return false;
    }
    
    public final boolean isVibrationEnabled() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.cleverkeyboard.app.data.KeyboardTheme getCurrentTheme() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSelectedPresetId() {
        return null;
    }
    
    public final boolean isKeyboardEnabled() {
        return false;
    }
    
    public final boolean isKeyboardSelected() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.cleverkeyboard.app.ui.viewmodel.ColorTarget getActiveColorTarget() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getKeyShape() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getActiveLanguage() {
        return null;
    }
    
    public final int getKeyboardHeightPercent() {
        return 0;
    }
    
    public final boolean isSwipeTypingEnabled() {
        return false;
    }
    
    public final boolean isSmartPunctuationEnabled() {
        return false;
    }
    
    public final boolean isContextPredictionEnabled() {
        return false;
    }
    
    public final boolean isPersonalizedLearningEnabled() {
        return false;
    }
    
    public final boolean isGrammarCheckEnabled() {
        return false;
    }
    
    public final boolean isSentenceCompletionEnabled() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTanglishOutputMode() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getVocabularyResetMessage() {
        return null;
    }
    
    public SettingsUiState() {
        super();
    }
    
    public final int component1() {
        return 0;
    }
    
    public final boolean component10() {
        return false;
    }
    
    public final boolean component11() {
        return false;
    }
    
    public final boolean component12() {
        return false;
    }
    
    public final boolean component13() {
        return false;
    }
    
    public final boolean component14() {
        return false;
    }
    
    public final boolean component15() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.cleverkeyboard.app.data.KeyboardTheme component16() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component17() {
        return null;
    }
    
    public final boolean component18() {
        return false;
    }
    
    public final boolean component19() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.cleverkeyboard.app.ui.viewmodel.ColorTarget component20() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component21() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component22() {
        return null;
    }
    
    public final int component23() {
        return 0;
    }
    
    public final boolean component24() {
        return false;
    }
    
    public final boolean component25() {
        return false;
    }
    
    public final boolean component26() {
        return false;
    }
    
    public final boolean component27() {
        return false;
    }
    
    public final boolean component28() {
        return false;
    }
    
    public final boolean component29() {
        return false;
    }
    
    public final boolean component3() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component30() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component31() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component4() {
        return null;
    }
    
    public final boolean component5() {
        return false;
    }
    
    public final boolean component6() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component7() {
        return null;
    }
    
    public final boolean component8() {
        return false;
    }
    
    public final boolean component9() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.cleverkeyboard.app.ui.viewmodel.SettingsUiState copy(int selectedTab, @org.jetbrains.annotations.NotNull()
    java.lang.String apiKey, boolean hasApiKey, @org.jetbrains.annotations.Nullable()
    java.lang.String apiKeySavedMessage, boolean isAutoCorrectEnabled, boolean isNumberRowEnabled, @org.jetbrains.annotations.NotNull()
    java.lang.String keyBordersMode, boolean isKeyBordersEnabled, boolean isAutoCapitalizationEnabled, boolean isAiToolbarLabelsEnabled, boolean isAutoNumPadEnabled, boolean isNextWordPredictionEnabled, boolean isPersonalizedSuggestionsEnabled, boolean isSoundEnabled, boolean isVibrationEnabled, @org.jetbrains.annotations.NotNull()
    com.cleverkeyboard.app.data.KeyboardTheme currentTheme, @org.jetbrains.annotations.Nullable()
    java.lang.String selectedPresetId, boolean isKeyboardEnabled, boolean isKeyboardSelected, @org.jetbrains.annotations.Nullable()
    com.cleverkeyboard.app.ui.viewmodel.ColorTarget activeColorTarget, @org.jetbrains.annotations.NotNull()
    java.lang.String keyShape, @org.jetbrains.annotations.NotNull()
    java.lang.String activeLanguage, int keyboardHeightPercent, boolean isSwipeTypingEnabled, boolean isSmartPunctuationEnabled, boolean isContextPredictionEnabled, boolean isPersonalizedLearningEnabled, boolean isGrammarCheckEnabled, boolean isSentenceCompletionEnabled, @org.jetbrains.annotations.NotNull()
    java.lang.String tanglishOutputMode, @org.jetbrains.annotations.Nullable()
    java.lang.String vocabularyResetMessage) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}