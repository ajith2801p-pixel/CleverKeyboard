package com.cleverkeyboard.app.intelligence;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u00132\u00020\u0001:\u0001\u0013B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J,\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0012\u001a\u00020\u000bR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u0014"}, d2 = {"Lcom/cleverkeyboard/app/intelligence/GrammarCorrectionEngine;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "aiService", "Lcom/cleverkeyboard/app/network/AiService;", "settings", "Lcom/cleverkeyboard/app/intelligence/IntelligenceSettingsManager;", "checkCloudGrammar", "Lkotlin/Result;", "", "text", "apiKey", "checkCloudGrammar-0E7RQCE", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "checkLocalGrammar", "Lcom/cleverkeyboard/app/intelligence/GrammarFix;", "sentence", "Companion", "app_debug"})
public final class GrammarCorrectionEngine {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.cleverkeyboard.app.network.AiService aiService = null;
    @org.jetbrains.annotations.NotNull()
    private final com.cleverkeyboard.app.intelligence.IntelligenceSettingsManager settings = null;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.cleverkeyboard.app.intelligence.GrammarCorrectionEngine instance;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<kotlin.Pair<kotlin.text.Regex, kotlin.jvm.functions.Function1<kotlin.text.MatchResult, java.lang.String>>> LOCAL_RULES = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.cleverkeyboard.app.intelligence.GrammarCorrectionEngine.Companion Companion = null;
    
    private GrammarCorrectionEngine(android.content.Context context) {
        super();
    }
    
    /**
     * Checks sentence for local grammar issues in real-time (<5ms).
     * Returns null if no errors detected.
     */
    @org.jetbrains.annotations.Nullable()
    public final com.cleverkeyboard.app.intelligence.GrammarFix checkLocalGrammar(@org.jetbrains.annotations.NotNull()
    java.lang.String sentence) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\f\u001a\u0004\u0018\u00010\r2\u0006\u0010\u000e\u001a\u00020\tJ\u000e\u0010\u000f\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u0011R,\u0010\u0003\u001a \u0012\u001c\u0012\u001a\u0012\u0004\u0012\u00020\u0006\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u00070\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lcom/cleverkeyboard/app/intelligence/GrammarCorrectionEngine$Companion;", "", "()V", "LOCAL_RULES", "", "Lkotlin/Pair;", "Lkotlin/text/Regex;", "Lkotlin/Function1;", "Lkotlin/text/MatchResult;", "", "instance", "Lcom/cleverkeyboard/app/intelligence/GrammarCorrectionEngine;", "findGrammarIssue", "Lcom/cleverkeyboard/app/intelligence/GrammarFix;", "sentence", "getInstance", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.cleverkeyboard.app.intelligence.GrammarCorrectionEngine getInstance(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final com.cleverkeyboard.app.intelligence.GrammarFix findGrammarIssue(@org.jetbrains.annotations.NotNull()
        java.lang.String sentence) {
            return null;
        }
    }
}