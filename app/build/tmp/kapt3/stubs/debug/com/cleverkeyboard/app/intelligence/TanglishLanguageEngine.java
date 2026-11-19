package com.cleverkeyboard.app.intelligence;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\u0018\u0000 \u00122\u00020\u0001:\u0001\u0012B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J,\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\rH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\f\u001a\u00020\rR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u0013"}, d2 = {"Lcom/cleverkeyboard/app/intelligence/TanglishLanguageEngine;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "aiService", "Lcom/cleverkeyboard/app/network/AiService;", "personalVocab", "Lcom/cleverkeyboard/app/intelligence/PersonalVocabularyManager;", "convertCloud", "Lkotlin/Result;", "Lcom/cleverkeyboard/app/intelligence/TanglishConversion;", "text", "", "apiKey", "convertCloud-0E7RQCE", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "convertLocal", "Companion", "app_debug"})
public final class TanglishLanguageEngine {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.cleverkeyboard.app.network.AiService aiService = null;
    @org.jetbrains.annotations.NotNull()
    private final com.cleverkeyboard.app.intelligence.PersonalVocabularyManager personalVocab = null;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.cleverkeyboard.app.intelligence.TanglishLanguageEngine instance;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, kotlin.Pair<java.lang.String, java.lang.String>> TANGLISH_PHRASES = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.cleverkeyboard.app.intelligence.TanglishLanguageEngine.Companion Companion = null;
    
    private TanglishLanguageEngine(android.content.Context context) {
        super();
    }
    
    /**
     * Resolves Tanglish input locally with instant response (<2ms).
     */
    @org.jetbrains.annotations.Nullable()
    public final com.cleverkeyboard.app.intelligence.TanglishConversion convertLocal(@org.jetbrains.annotations.NotNull()
    java.lang.String text) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000bJ\u0010\u0010\f\u001a\u0004\u0018\u00010\r2\u0006\u0010\u000e\u001a\u00020\u0005R&\u0010\u0003\u001a\u001a\u0012\u0004\u0012\u00020\u0005\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u00060\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/cleverkeyboard/app/intelligence/TanglishLanguageEngine$Companion;", "", "()V", "TANGLISH_PHRASES", "", "", "Lkotlin/Pair;", "instance", "Lcom/cleverkeyboard/app/intelligence/TanglishLanguageEngine;", "getInstance", "context", "Landroid/content/Context;", "matchLocalTanglish", "Lcom/cleverkeyboard/app/intelligence/TanglishConversion;", "text", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.cleverkeyboard.app.intelligence.TanglishLanguageEngine getInstance(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final com.cleverkeyboard.app.intelligence.TanglishConversion matchLocalTanglish(@org.jetbrains.annotations.NotNull()
        java.lang.String text) {
            return null;
        }
    }
}