package com.cleverkeyboard.app.network;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u001e\u0018\u0000 72\u00020\u0001:\u00017B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u0007H\u0002J\u0018\u0010\u000b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u0007H\u0002J4\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00070\r2\u0006\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0007H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0012\u0010\u0013J4\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00070\r2\u0006\u0010\u000e\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0007H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u0010\u0010\u0017\u001a\u00020\u00072\u0006\u0010\u0018\u001a\u00020\u0007H\u0002J,\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001a0\r2\u0006\u0010\u001b\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0007H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001c\u0010\u001dJ4\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00070\r2\u0006\u0010\u001f\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0007H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b \u0010\u0016J,\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00070\r2\u0006\u0010\"\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0007H\u0082@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b#\u0010\u001dJ,\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00070\r2\u0006\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0007H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b%\u0010\u001dJ,\u0010&\u001a\b\u0012\u0004\u0012\u00020\u00070\r2\u0006\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0007H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\'\u0010\u001dJ4\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00070\r2\u0006\u0010\u000e\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0007H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b)\u0010\u0016J\u0016\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010+\u001a\u00020\u0007H\u0002J\u0018\u0010,\u001a\u00020\u001a2\u0006\u0010+\u001a\u00020\u00072\u0006\u0010-\u001a\u00020\u0007H\u0002J4\u0010.\u001a\b\u0012\u0004\u0012\u00020\u00070\r2\u0006\u0010\u000e\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0007H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b/\u0010\u0016J2\u00100\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00060\r2\u0006\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0007H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b1\u0010\u001dJ,\u00102\u001a\b\u0012\u0004\u0012\u00020\u00070\r2\u0006\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0007H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b3\u0010\u001dJ4\u00104\u001a\b\u0012\u0004\u0012\u00020\u00070\r2\u0006\u0010\u000e\u001a\u00020\u00072\u0006\u00105\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0007H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b6\u0010\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u00068"}, d2 = {"Lcom/cleverkeyboard/app/network/AiService;", "", "()V", "api", "Lcom/cleverkeyboard/app/network/GeminiApiService;", "candidateModels", "", "", "buildReplyPrompt", "inputText", "tone", "buildRewritePrompt", "changeLength", "Lkotlin/Result;", "text", "makeLonger", "", "apiKey", "changeLength-BWLJW6A", "(Ljava/lang/String;ZLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "changeTone", "changeTone-BWLJW6A", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "cleanResultText", "raw", "convertTanglish", "Lcom/cleverkeyboard/app/network/TanglishConversionResult;", "speechText", "convertTanglish-0E7RQCE", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "emailAssistant", "points", "emailAssistant-BWLJW6A", "executePrompt", "promptText", "executePrompt-0E7RQCE", "explain", "explain-0E7RQCE", "fixGrammar", "fixGrammar-0E7RQCE", "generateReplyWithTone", "generateReplyWithTone-BWLJW6A", "parseJsonArrayReplies", "rawText", "parseTanglishResult", "originalSpoken", "rewriteWithTone", "rewriteWithTone-BWLJW6A", "smartReply", "smartReply-0E7RQCE", "summarize", "summarize-0E7RQCE", "translate", "targetLang", "translate-BWLJW6A", "Companion", "app_debug"})
public final class AiService {
    @org.jetbrains.annotations.NotNull()
    private final com.cleverkeyboard.app.network.GeminiApiService api = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> candidateModels = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "AiService";
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.cleverkeyboard.app.network.AiService instance;
    @org.jetbrains.annotations.NotNull()
    public static final com.cleverkeyboard.app.network.AiService.Companion Companion = null;
    
    private AiService() {
        super();
    }
    
    private final java.lang.String buildReplyPrompt(java.lang.String inputText, java.lang.String tone) {
        return null;
    }
    
    private final java.lang.String buildRewritePrompt(java.lang.String inputText, java.lang.String tone) {
        return null;
    }
    
    private final java.lang.String cleanResultText(java.lang.String raw) {
        return null;
    }
    
    private final java.util.List<java.lang.String> parseJsonArrayReplies(java.lang.String rawText) {
        return null;
    }
    
    private final com.cleverkeyboard.app.network.TanglishConversionResult parseTanglishResult(java.lang.String rawText, java.lang.String originalSpoken) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0007\u001a\u00020\u0006R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/cleverkeyboard/app/network/AiService$Companion;", "", "()V", "TAG", "", "instance", "Lcom/cleverkeyboard/app/network/AiService;", "getInstance", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.cleverkeyboard.app.network.AiService getInstance() {
            return null;
        }
    }
}