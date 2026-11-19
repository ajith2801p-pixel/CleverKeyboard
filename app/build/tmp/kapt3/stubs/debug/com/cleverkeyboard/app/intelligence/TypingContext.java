package com.cleverkeyboard.app.intelligence;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\"\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\u000b\n\u0002\b\u001f\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001Bi\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\n\u0012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00030\f\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u000e\u0012\u0006\u0010\u0010\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0011J\t\u0010\u001f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010 \u001a\u00020\u000eH\u00c6\u0003J\t\u0010!\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\"\u001a\u00020\u0003H\u00c6\u0003J\t\u0010#\u001a\u00020\u0003H\u00c6\u0003J\t\u0010$\u001a\u00020\u0003H\u00c6\u0003J\t\u0010%\u001a\u00020\u0003H\u00c6\u0003J\t\u0010&\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\'\u001a\b\u0012\u0004\u0012\u00020\u00030\nH\u00c6\u0003J\u000f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00030\fH\u00c6\u0003J\t\u0010)\u001a\u00020\u000eH\u00c6\u0003J\u0083\u0001\u0010*\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\n2\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00030\f2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u000e2\b\b\u0002\u0010\u0010\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010+\u001a\u00020\u000e2\b\u0010,\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010-\u001a\u00020.H\u00d6\u0001J\t\u0010/\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u0011\u0010\u0010\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0013R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0013R\u0011\u0010\u000f\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0017R\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u0017R\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0013R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0013R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0013R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00030\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001e\u00a8\u00060"}, d2 = {"Lcom/cleverkeyboard/app/intelligence/TypingContext;", "", "fullTextBeforeCursor", "", "currentWord", "previousWord", "twoWordsBack", "currentSentence", "previousSentence", "semanticKeywords", "", "wordsInSentence", "", "isSentenceStart", "", "isIncompleteSentence", "detectedLanguage", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/Set;Ljava/util/List;ZZLjava/lang/String;)V", "getCurrentSentence", "()Ljava/lang/String;", "getCurrentWord", "getDetectedLanguage", "getFullTextBeforeCursor", "()Z", "getPreviousSentence", "getPreviousWord", "getSemanticKeywords", "()Ljava/util/Set;", "getTwoWordsBack", "getWordsInSentence", "()Ljava/util/List;", "component1", "component10", "component11", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
public final class TypingContext {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String fullTextBeforeCursor = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String currentWord = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String previousWord = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String twoWordsBack = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String currentSentence = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String previousSentence = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.String> semanticKeywords = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> wordsInSentence = null;
    private final boolean isSentenceStart = false;
    private final boolean isIncompleteSentence = false;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String detectedLanguage = null;
    
    public TypingContext(@org.jetbrains.annotations.NotNull()
    java.lang.String fullTextBeforeCursor, @org.jetbrains.annotations.NotNull()
    java.lang.String currentWord, @org.jetbrains.annotations.NotNull()
    java.lang.String previousWord, @org.jetbrains.annotations.NotNull()
    java.lang.String twoWordsBack, @org.jetbrains.annotations.NotNull()
    java.lang.String currentSentence, @org.jetbrains.annotations.NotNull()
    java.lang.String previousSentence, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> semanticKeywords, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> wordsInSentence, boolean isSentenceStart, boolean isIncompleteSentence, @org.jetbrains.annotations.NotNull()
    java.lang.String detectedLanguage) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getFullTextBeforeCursor() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCurrentWord() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPreviousWord() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTwoWordsBack() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCurrentSentence() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPreviousSentence() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getSemanticKeywords() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getWordsInSentence() {
        return null;
    }
    
    public final boolean isSentenceStart() {
        return false;
    }
    
    public final boolean isIncompleteSentence() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDetectedLanguage() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    public final boolean component10() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component11() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component8() {
        return null;
    }
    
    public final boolean component9() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.cleverkeyboard.app.intelligence.TypingContext copy(@org.jetbrains.annotations.NotNull()
    java.lang.String fullTextBeforeCursor, @org.jetbrains.annotations.NotNull()
    java.lang.String currentWord, @org.jetbrains.annotations.NotNull()
    java.lang.String previousWord, @org.jetbrains.annotations.NotNull()
    java.lang.String twoWordsBack, @org.jetbrains.annotations.NotNull()
    java.lang.String currentSentence, @org.jetbrains.annotations.NotNull()
    java.lang.String previousSentence, @org.jetbrains.annotations.NotNull()
    java.util.Set<java.lang.String> semanticKeywords, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> wordsInSentence, boolean isSentenceStart, boolean isIncompleteSentence, @org.jetbrains.annotations.NotNull()
    java.lang.String detectedLanguage) {
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