package com.cleverkeyboard.app.intelligence;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 (2\u00020\u0001:\u0001(B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u000bH\u0086@\u00a2\u0006\u0002\u0010\u0014J\u0018\u0010\u0015\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u000b2\u0006\u0010\u0016\u001a\u00020\u000bH\u0002J\u0012\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001a0\u00190\u0018J\u000e\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u0013\u001a\u00020\u000bJ\u001e\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00192\u0006\u0010\u001e\u001a\u00020\u000b2\b\b\u0002\u0010\u001f\u001a\u00020\fJ\u000e\u0010 \u001a\u00020!2\u0006\u0010\u0013\u001a\u00020\u000bJ$\u0010\"\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u000b2\b\b\u0002\u0010#\u001a\u00020\u000b2\n\b\u0002\u0010$\u001a\u0004\u0018\u00010%J\u000e\u0010&\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010\'R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\f0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006)"}, d2 = {"Lcom/cleverkeyboard/app/intelligence/PersonalVocabularyManager;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "dao", "Lcom/cleverkeyboard/app/data/db/dao/UserDictionaryDao;", "db", "Lcom/cleverkeyboard/app/data/db/AppDatabase;", "memoryCache", "Ljava/util/concurrent/ConcurrentHashMap;", "", "", "scope", "Lkotlinx/coroutines/CoroutineScope;", "settings", "Lcom/cleverkeyboard/app/intelligence/IntelligenceSettingsManager;", "deleteWord", "", "word", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "formatPersonalWord", "inputPrefix", "getAllWordsFlow", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/cleverkeyboard/app/data/db/entity/UserDictionaryEntity;", "getBoostMultiplier", "", "getMatchingWords", "prefix", "limit", "isKnownWord", "", "learnWord", "language", "editorInfo", "Landroid/view/inputmethod/EditorInfo;", "resetVocabulary", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "app_debug"})
public final class PersonalVocabularyManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.cleverkeyboard.app.data.db.AppDatabase db = null;
    @org.jetbrains.annotations.NotNull()
    private final com.cleverkeyboard.app.data.db.dao.UserDictionaryDao dao = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.NotNull()
    private final com.cleverkeyboard.app.intelligence.IntelligenceSettingsManager settings = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.concurrent.ConcurrentHashMap<java.lang.String, java.lang.Integer> memoryCache = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "PersonalVocabMgr";
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, java.lang.String> ABBREVIATIONS = null;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.cleverkeyboard.app.intelligence.PersonalVocabularyManager instance;
    @org.jetbrains.annotations.NotNull()
    public static final com.cleverkeyboard.app.intelligence.PersonalVocabularyManager.Companion Companion = null;
    
    private PersonalVocabularyManager(android.content.Context context) {
        super();
    }
    
    /**
     * Learns a typed word or phrase into personal vocabulary with frequency tracking.
     * Guaranteed to skip sensitive/password/credit card/OTP fields.
     */
    public final void learnWord(@org.jetbrains.annotations.NotNull()
    java.lang.String word, @org.jetbrains.annotations.NotNull()
    java.lang.String language, @org.jetbrains.annotations.Nullable()
    android.view.inputmethod.EditorInfo editorInfo) {
    }
    
    /**
     * Gets a frequency boost factor for ranking suggestions.
     * Returns a multiplier between 1.0f (new/standard) and 3.0f (frequently typed).
     */
    public final float getBoostMultiplier(@org.jetbrains.annotations.NotNull()
    java.lang.String word) {
        return 0.0F;
    }
    
    /**
     * Checks if a word is in personal vocabulary (learned or seeded).
     */
    public final boolean isKnownWord(@org.jetbrains.annotations.NotNull()
    java.lang.String word) {
        return false;
    }
    
    /**
     * Returns personal vocabulary matching a prefix for instant autocomplete.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getMatchingWords(@org.jetbrains.annotations.NotNull()
    java.lang.String prefix, int limit) {
        return null;
    }
    
    private final java.lang.String formatPersonalWord(java.lang.String word, java.lang.String inputPrefix) {
        return null;
    }
    
    /**
     * Observes all user dictionary entries for settings / UI management.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.cleverkeyboard.app.data.db.entity.UserDictionaryEntity>> getAllWordsFlow() {
        return null;
    }
    
    /**
     * Deletes a specific word from personal vocabulary.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteWord(@org.jetbrains.annotations.NotNull()
    java.lang.String word, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Clears all learned words and resets personal vocabulary.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object resetVocabulary(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000bR\u001a\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/cleverkeyboard/app/intelligence/PersonalVocabularyManager$Companion;", "", "()V", "ABBREVIATIONS", "", "", "TAG", "instance", "Lcom/cleverkeyboard/app/intelligence/PersonalVocabularyManager;", "getInstance", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.cleverkeyboard.app.intelligence.PersonalVocabularyManager getInstance(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}