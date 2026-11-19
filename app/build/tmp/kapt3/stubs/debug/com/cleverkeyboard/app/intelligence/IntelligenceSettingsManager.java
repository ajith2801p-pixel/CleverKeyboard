package com.cleverkeyboard.app.intelligence;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 \u00192\u00020\u0001:\u0001\u0019B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R$\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00068F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR$\u0010\u000b\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00068F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b\u000b\u0010\b\"\u0004\b\f\u0010\nR$\u0010\r\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00068F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b\r\u0010\b\"\u0004\b\u000e\u0010\nR$\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00068F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b\u000f\u0010\b\"\u0004\b\u0010\u0010\nR\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R$\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0005\u001a\u00020\u00138F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018\u00a8\u0006\u001a"}, d2 = {"Lcom/cleverkeyboard/app/intelligence/IntelligenceSettingsManager;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "value", "", "isContextPredictionEnabled", "()Z", "setContextPredictionEnabled", "(Z)V", "isGrammarCheckEnabled", "setGrammarCheckEnabled", "isPersonalizedLearningEnabled", "setPersonalizedLearningEnabled", "isSentenceCompletionEnabled", "setSentenceCompletionEnabled", "prefs", "Landroid/content/SharedPreferences;", "Lcom/cleverkeyboard/app/intelligence/TanglishOutputMode;", "tanglishOutputMode", "getTanglishOutputMode", "()Lcom/cleverkeyboard/app/intelligence/TanglishOutputMode;", "setTanglishOutputMode", "(Lcom/cleverkeyboard/app/intelligence/TanglishOutputMode;)V", "Companion", "app_debug"})
public final class IntelligenceSettingsManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.SharedPreferences prefs = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "clever_intelligence_prefs";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_CONTEXT_PREDICTION = "key_context_prediction";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_PERSONALIZED_LEARNING = "key_personalized_learning";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_GRAMMAR_CHECK = "key_grammar_check";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_SENTENCE_COMPLETION = "key_sentence_completion";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_TANGLISH_OUTPUT_MODE = "key_tanglish_output_mode";
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.cleverkeyboard.app.intelligence.IntelligenceSettingsManager instance;
    @org.jetbrains.annotations.NotNull()
    public static final com.cleverkeyboard.app.intelligence.IntelligenceSettingsManager.Companion Companion = null;
    
    private IntelligenceSettingsManager(android.content.Context context) {
        super();
    }
    
    public final boolean isContextPredictionEnabled() {
        return false;
    }
    
    public final void setContextPredictionEnabled(boolean value) {
    }
    
    public final boolean isPersonalizedLearningEnabled() {
        return false;
    }
    
    public final void setPersonalizedLearningEnabled(boolean value) {
    }
    
    public final boolean isGrammarCheckEnabled() {
        return false;
    }
    
    public final void setGrammarCheckEnabled(boolean value) {
    }
    
    public final boolean isSentenceCompletionEnabled() {
        return false;
    }
    
    public final void setSentenceCompletionEnabled(boolean value) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.cleverkeyboard.app.intelligence.TanglishOutputMode getTanglishOutputMode() {
        return null;
    }
    
    public final void setTanglishOutputMode(@org.jetbrains.annotations.NotNull()
    com.cleverkeyboard.app.intelligence.TanglishOutputMode value) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000eR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/cleverkeyboard/app/intelligence/IntelligenceSettingsManager$Companion;", "", "()V", "KEY_CONTEXT_PREDICTION", "", "KEY_GRAMMAR_CHECK", "KEY_PERSONALIZED_LEARNING", "KEY_SENTENCE_COMPLETION", "KEY_TANGLISH_OUTPUT_MODE", "PREFS_NAME", "instance", "Lcom/cleverkeyboard/app/intelligence/IntelligenceSettingsManager;", "getInstance", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.cleverkeyboard.app.intelligence.IntelligenceSettingsManager getInstance(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}