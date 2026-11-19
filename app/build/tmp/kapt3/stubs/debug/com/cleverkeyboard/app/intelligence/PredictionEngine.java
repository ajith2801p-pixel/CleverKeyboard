package com.cleverkeyboard.app.intelligence;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\u0018\u0000 \u00142\u00020\u0001:\u0001\u0014B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001e\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\u0006\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u0013R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/cleverkeyboard/app/intelligence/PredictionEngine;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "grammarEngine", "Lcom/cleverkeyboard/app/intelligence/GrammarCorrectionEngine;", "personalVocab", "Lcom/cleverkeyboard/app/intelligence/PersonalVocabularyManager;", "sentenceEngine", "Lcom/cleverkeyboard/app/intelligence/SentenceCompletionEngine;", "settings", "Lcom/cleverkeyboard/app/intelligence/IntelligenceSettingsManager;", "getContextualSuggestions", "", "Lcom/cleverkeyboard/app/autocorrect/SuggestionItem;", "typingContext", "Lcom/cleverkeyboard/app/intelligence/TypingContext;", "maxSuggestions", "", "Companion", "app_debug"})
public final class PredictionEngine {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.cleverkeyboard.app.intelligence.PersonalVocabularyManager personalVocab = null;
    @org.jetbrains.annotations.NotNull()
    private final com.cleverkeyboard.app.intelligence.GrammarCorrectionEngine grammarEngine = null;
    @org.jetbrains.annotations.NotNull()
    private final com.cleverkeyboard.app.intelligence.SentenceCompletionEngine sentenceEngine = null;
    @org.jetbrains.annotations.NotNull()
    private final com.cleverkeyboard.app.intelligence.IntelligenceSettingsManager settings = null;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.cleverkeyboard.app.intelligence.PredictionEngine instance;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, java.util.List<java.lang.String>> ENGLISH_TRIGRAMS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> FALLBACK_WORDS = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.cleverkeyboard.app.intelligence.PredictionEngine.Companion Companion = null;
    
    private PredictionEngine(android.content.Context context) {
        super();
    }
    
    /**
     * Generates 3 to 5 ranked, context-aware suggestions for the keyboard suggestion bar.
     * Execution time: < 10ms.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.cleverkeyboard.app.autocorrect.SuggestionItem> getContextualSuggestions(@org.jetbrains.annotations.NotNull()
    com.cleverkeyboard.app.intelligence.TypingContext typingContext, int maxSuggestions) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\fR \u0010\u0003\u001a\u0014\u0012\u0004\u0012\u00020\u0005\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00060\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00050\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/cleverkeyboard/app/intelligence/PredictionEngine$Companion;", "", "()V", "ENGLISH_TRIGRAMS", "", "", "", "FALLBACK_WORDS", "instance", "Lcom/cleverkeyboard/app/intelligence/PredictionEngine;", "getInstance", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.cleverkeyboard.app.intelligence.PredictionEngine getInstance(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}