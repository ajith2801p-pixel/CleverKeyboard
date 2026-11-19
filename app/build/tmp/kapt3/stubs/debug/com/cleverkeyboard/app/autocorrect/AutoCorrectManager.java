package com.cleverkeyboard.app.autocorrect;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0010 \n\u0002\b\f\n\u0002\u0010\f\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0010\u0002\n\u0002\b\f\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J&\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00050\u00062\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020\u00052\u0006\u0010%\u001a\u00020\u0005H\u0002J\u000e\u0010&\u001a\u00020\u00052\u0006\u0010\"\u001a\u00020#J\u001e\u0010\'\u001a\b\u0012\u0004\u0012\u00020\u00050\u00062\u0006\u0010(\u001a\u00020\u00052\b\b\u0002\u0010%\u001a\u00020\u0005J\u001c\u0010)\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\"\u001a\u0004\u0018\u00010#2\u0006\u0010*\u001a\u00020\u0005J \u0010+\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u00062\u0006\u0010$\u001a\u00020\u00052\u0006\u0010%\u001a\u00020\u0005H\u0002J\u0018\u0010,\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u00062\u0006\u0010-\u001a\u00020\u0005H\u0002J \u0010.\u001a\b\u0012\u0004\u0012\u00020/0\u00062\n\b\u0002\u0010\"\u001a\u0004\u0018\u00010#2\u0006\u00100\u001a\u00020\u0005J\u001c\u00101\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\"\u001a\u00020#H\u0002J\u0010\u00102\u001a\u00020\u001b2\u0006\u0010\"\u001a\u00020#H\u0002J\u001e\u00103\u001a\b\u0012\u0004\u0012\u00020\u00050\u00062\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020\u0005H\u0002J\u0014\u00104\u001a\b\u0012\u0004\u0012\u00020\u00050\u00062\u0006\u0010$\u001a\u00020\u0005J\u0010\u00105\u001a\u00020\u001b2\u0006\u0010\"\u001a\u00020#H\u0002J\u001c\u00106\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\"\u001a\u00020#H\u0002J\u001c\u00107\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\"\u001a\u00020#H\u0002J\u0014\u00108\u001a\b\u0012\u0004\u0012\u00020\u00050\u00062\u0006\u0010\"\u001a\u00020#J\u0010\u00109\u001a\u00020:2\b\u0010;\u001a\u0004\u0018\u00010<J\u000e\u0010=\u001a\u00020:2\u0006\u0010\"\u001a\u00020#J\u000e\u0010>\u001a\u00020:2\u0006\u0010\"\u001a\u00020#J\u000e\u0010?\u001a\u00020:2\u0006\u0010(\u001a\u00020\u0005J\u000e\u0010@\u001a\u00020:2\u0006\u0010\"\u001a\u00020#J\u000e\u0010A\u001a\u00020:2\u0006\u0010\"\u001a\u00020#J\u000e\u0010B\u001a\u00020:2\u0006\u0010\"\u001a\u00020#J\u0016\u0010C\u001a\u00020\u00192\u0006\u0010D\u001a\u00020\u00052\u0006\u0010E\u001a\u00020\u0005J$\u0010F\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\"\u001a\u00020#2\u0006\u0010G\u001a\u00020\u0005H\u0002J\u0016\u0010H\u001a\u00020\u00052\u0006\u0010I\u001a\u00020\u00052\u0006\u0010J\u001a\u00020\u0005J$\u0010K\u001a\b\u0012\u0004\u0012\u00020\u00050\u00062\b\u0010\"\u001a\u0004\u0018\u00010#2\f\u0010L\u001a\b\u0012\u0004\u0012\u00020\u00130\u0006J\u000e\u0010M\u001a\u00020N2\u0006\u0010\"\u001a\u00020#J\"\u0010O\u001a\u00020N2\u0006\u0010\"\u001a\u00020#2\u0006\u0010(\u001a\u00020\u00052\n\b\u0002\u0010;\u001a\u0004\u0018\u00010<J\u0016\u0010P\u001a\u00020N2\u0006\u0010\"\u001a\u00020#2\u0006\u0010%\u001a\u00020\u0005J\u0016\u0010Q\u001a\u00020N2\u0006\u0010\"\u001a\u00020#2\u0006\u0010R\u001a\u00020:J\u0016\u0010S\u001a\u00020N2\u0006\u0010\"\u001a\u00020#2\u0006\u0010R\u001a\u00020:J\u0016\u0010T\u001a\u00020N2\u0006\u0010\"\u001a\u00020#2\u0006\u0010R\u001a\u00020:J\u0016\u0010U\u001a\u00020N2\u0006\u0010\"\u001a\u00020#2\u0006\u0010R\u001a\u00020:J\u0016\u0010V\u001a\u00020N2\u0006\u0010\"\u001a\u00020#2\u0006\u0010R\u001a\u00020:J\u001b\u0010W\u001a\u00020N2\f\u0010X\u001a\b\u0012\u0004\u0012\u00020\u00050\u0006H\u0000\u00a2\u0006\u0002\bYR \u0010\u0003\u001a\u0014\u0012\u0004\u0012\u00020\u0005\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00060\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R \u0010\u0012\u001a\u0014\u0012\u0004\u0012\u00020\u0013\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u00060\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R \u0010\u0015\u001a\u0014\u0012\u0004\u0012\u00020\u0005\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00060\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0016\u001a\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00190\u0018X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u001bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u001d\u001a\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u001e\u001a\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u001f\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006Z"}, d2 = {"Lcom/cleverkeyboard/app/autocorrect/AutoCorrectManager;", "", "()V", "ENGLISH_BIGRAMS", "", "", "", "KEY_ACTIVE_LANGUAGE", "KEY_AUTO_CORRECT_ENABLED", "KEY_NEXT_WORD_PREDICTION", "KEY_PERSONALIZED_SUGGESTIONS", "KEY_SMART_PUNCTUATION", "KEY_SWIPE_TYPING", "LANG_EN", "LANG_TA", "LANG_TANG", "PREFS_NAME", "PREFS_PERSONAL_DICT", "QWERTY_NEIGHBORS", "", "TAG", "TANGLISH_BIGRAMS", "englishDict", "frequentWordsMap", "Ljava/util/concurrent/ConcurrentHashMap;", "", "personalPrefs", "Landroid/content/SharedPreferences;", "sharedPreferences", "tamilDict", "tanglishDict", "wordsList", "wordsLock", "findPrefixMatches", "context", "Landroid/content/Context;", "prefix", "lang", "getActiveLanguage", "getBigramPredictions", "word", "getCorrection", "rawWord", "getCuratedPrefixMatches", "getCuratedTypoSuggestions", "query", "getDynamicSuggestions", "Lcom/cleverkeyboard/app/autocorrect/SuggestionItem;", "textBeforeCursor", "getEnglishMap", "getPersonalPrefs", "getPersonalWords", "getPersonalWordsInMemory", "getPrefs", "getTamilMap", "getTanglishMap", "getWordsList", "isAutoCorrectAllowed", "", "info", "Landroid/view/inputmethod/EditorInfo;", "isAutoCorrectEnabled", "isNextWordPredictionEnabled", "isPersonalWord", "isPersonalizedSuggestionsEnabled", "isSmartPunctuationEnabled", "isSwipeTypingEnabled", "levenshteinDistance", "s1", "s2", "loadJsonMap", "filename", "matchCase", "original", "target", "matchSwipeTrajectory", "visitedChars", "preload", "", "recordWordSelection", "setActiveLanguage", "setAutoCorrectEnabled", "enabled", "setNextWordPredictionEnabled", "setPersonalizedSuggestionsEnabled", "setSmartPunctuationEnabled", "setSwipeTypingEnabled", "setTestWordsList", "words", "setTestWordsList$app_debug", "app_debug"})
public final class AutoCorrectManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "AutoCorrectManager";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "clever_keyboard_autocorrect_prefs";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_AUTO_CORRECT_ENABLED = "auto_correct_enabled";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_ACTIVE_LANGUAGE = "autocorrect_active_language";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_NEXT_WORD_PREDICTION = "next_word_prediction";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_PERSONALIZED_SUGGESTIONS = "personalized_suggestions";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_SWIPE_TYPING = "swipe_typing_enabled";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_SMART_PUNCTUATION = "smart_punctuation_enabled";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_PERSONAL_DICT = "clever_personal_dict_prefs";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String LANG_EN = "EN";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String LANG_TA = "TA";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String LANG_TANG = "TANG";
    @org.jetbrains.annotations.Nullable()
    private static android.content.SharedPreferences sharedPreferences;
    @org.jetbrains.annotations.Nullable()
    private static android.content.SharedPreferences personalPrefs;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.concurrent.ConcurrentHashMap<java.lang.String, java.lang.Integer> frequentWordsMap = null;
    @org.jetbrains.annotations.Nullable()
    private static java.util.Map<java.lang.String, java.lang.String> englishDict;
    @org.jetbrains.annotations.Nullable()
    private static java.util.Map<java.lang.String, java.lang.String> tanglishDict;
    @org.jetbrains.annotations.Nullable()
    private static java.util.Map<java.lang.String, java.lang.String> tamilDict;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, java.util.List<java.lang.String>> ENGLISH_BIGRAMS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, java.util.List<java.lang.String>> TANGLISH_BIGRAMS = null;
    @org.jetbrains.annotations.Nullable()
    private static java.util.List<java.lang.String> wordsList;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.Object wordsLock = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.Character, java.util.List<java.lang.Character>> QWERTY_NEIGHBORS = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.cleverkeyboard.app.autocorrect.AutoCorrectManager INSTANCE = null;
    
    private AutoCorrectManager() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getBigramPredictions(@org.jetbrains.annotations.NotNull()
    java.lang.String word, @org.jetbrains.annotations.NotNull()
    java.lang.String lang) {
        return null;
    }
    
    private final android.content.SharedPreferences getPrefs(android.content.Context context) {
        return null;
    }
    
    private final android.content.SharedPreferences getPersonalPrefs(android.content.Context context) {
        return null;
    }
    
    public final boolean isAutoCorrectEnabled(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    public final void setAutoCorrectEnabled(@org.jetbrains.annotations.NotNull()
    android.content.Context context, boolean enabled) {
    }
    
    public final boolean isNextWordPredictionEnabled(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    public final void setNextWordPredictionEnabled(@org.jetbrains.annotations.NotNull()
    android.content.Context context, boolean enabled) {
    }
    
    public final boolean isPersonalizedSuggestionsEnabled(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    public final void setPersonalizedSuggestionsEnabled(@org.jetbrains.annotations.NotNull()
    android.content.Context context, boolean enabled) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getActiveLanguage(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    public final void setActiveLanguage(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String lang) {
    }
    
    public final boolean isAutoCorrectAllowed(@org.jetbrains.annotations.Nullable()
    android.view.inputmethod.EditorInfo info) {
        return false;
    }
    
    private final java.util.Map<java.lang.String, java.lang.String> loadJsonMap(android.content.Context context, java.lang.String filename) {
        return null;
    }
    
    /**
     * Preloads dictionaries in background on keyboard start.
     */
    public final void preload(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    @kotlin.jvm.Synchronized()
    @org.jetbrains.annotations.NotNull()
    public final synchronized java.util.List<java.lang.String> getWordsList(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    public final void setTestWordsList$app_debug(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> words) {
    }
    
    @kotlin.jvm.Synchronized()
    private final synchronized java.util.Map<java.lang.String, java.lang.String> getEnglishMap(android.content.Context context) {
        return null;
    }
    
    @kotlin.jvm.Synchronized()
    private final synchronized java.util.Map<java.lang.String, java.lang.String> getTanglishMap(android.content.Context context) {
        return null;
    }
    
    @kotlin.jvm.Synchronized()
    private final synchronized java.util.Map<java.lang.String, java.lang.String> getTamilMap(android.content.Context context) {
        return null;
    }
    
    /**
     * Records when user taps/selects a suggestion.
     * Updates fast in-memory map and persists to Room Database asynchronously.
     * Strictly blocked on password and sensitive fields.
     */
    public final void recordWordSelection(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String word, @org.jetbrains.annotations.Nullable()
    android.view.inputmethod.EditorInfo info) {
    }
    
    public final boolean isPersonalWord(@org.jetbrains.annotations.NotNull()
    java.lang.String word) {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getPersonalWordsInMemory(@org.jetbrains.annotations.NotNull()
    java.lang.String prefix) {
        return null;
    }
    
    private final java.util.List<java.lang.String> getPersonalWords(android.content.Context context, java.lang.String prefix) {
        return null;
    }
    
    /**
     * Looks up correction for a word for Space press auto-replace.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCorrection(@org.jetbrains.annotations.Nullable()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String rawWord) {
        return null;
    }
    
    /**
     * Fast Levenshtein Edit Distance calculation.
     */
    public final int levenshteinDistance(@org.jetbrains.annotations.NotNull()
    java.lang.String s1, @org.jetbrains.annotations.NotNull()
    java.lang.String s2) {
        return 0;
    }
    
    private final java.util.List<java.lang.String> getCuratedTypoSuggestions(java.lang.String query) {
        return null;
    }
    
    /**
     * Dynamic Smart Suggestions on EVERY keypress!
     * Returns exactly 5 distinct items: [s1, s2, s3 (Center Primary), s4, s5]
     * Never duplicates!
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.cleverkeyboard.app.autocorrect.SuggestionItem> getDynamicSuggestions(@org.jetbrains.annotations.Nullable()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String textBeforeCursor) {
        return null;
    }
    
    private final java.util.List<java.lang.String> getCuratedPrefixMatches(java.lang.String prefix, java.lang.String lang) {
        return null;
    }
    
    private final java.util.List<java.lang.String> findPrefixMatches(android.content.Context context, java.lang.String prefix, java.lang.String lang) {
        return null;
    }
    
    public final boolean isSwipeTypingEnabled(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    public final void setSwipeTypingEnabled(@org.jetbrains.annotations.NotNull()
    android.content.Context context, boolean enabled) {
    }
    
    public final boolean isSmartPunctuationEnabled(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    public final void setSmartPunctuationEnabled(@org.jetbrains.annotations.NotNull()
    android.content.Context context, boolean enabled) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> matchSwipeTrajectory(@org.jetbrains.annotations.Nullable()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Character> visitedChars) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String matchCase(@org.jetbrains.annotations.NotNull()
    java.lang.String original, @org.jetbrains.annotations.NotNull()
    java.lang.String target) {
        return null;
    }
}