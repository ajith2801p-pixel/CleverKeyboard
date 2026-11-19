package com.cleverkeyboard.app.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J(\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u00052\b\b\u0002\u0010\u000f\u001a\u00020\u0005H\u0086@\u00a2\u0006\u0002\u0010\u0010J\u0016\u0010\u0011\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0086@\u00a2\u0006\u0002\u0010\u0012J\u001e\u0010\u0013\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0005H\u0086@\u00a2\u0006\u0002\u0010\u0014J\u001a\u0010\u0015\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\t0\u00162\u0006\u0010\f\u001a\u00020\rJ\u0006\u0010\u0018\u001a\u00020\u0006J\"\u0010\u0019\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u00052\n\b\u0002\u0010\u001a\u001a\u0004\u0018\u00010\u0005H\u0086@\u00a2\u0006\u0002\u0010\u001bJ\u001a\u0010\u001c\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u001d\u001a\u00020\u00052\u0006\u0010\u001e\u001a\u00020\u0005H\u0002R\u001a\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00060\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001f"}, d2 = {"Lcom/cleverkeyboard/app/data/DictionaryManager;", "", "()V", "LOCAL_DICTIONARY", "", "", "Lcom/cleverkeyboard/app/data/WordDefinition;", "TAG", "WORDS_OF_THE_DAY", "", "addPersonalWord", "", "context", "Landroid/content/Context;", "word", "lang", "(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "clearPersonalDictionary", "(Landroid/content/Context;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deletePersonalWord", "(Landroid/content/Context;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getPersonalWordsFlow", "Lkotlinx/coroutines/flow/Flow;", "Lcom/cleverkeyboard/app/data/db/entity/UserDictionaryEntity;", "getWordOfTheDay", "lookupWord", "apiKey", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "parseDefinitionJson", "raw", "originalWord", "app_debug"})
public final class DictionaryManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "DictionaryManager";
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<com.cleverkeyboard.app.data.WordDefinition> WORDS_OF_THE_DAY = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, com.cleverkeyboard.app.data.WordDefinition> LOCAL_DICTIONARY = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.cleverkeyboard.app.data.DictionaryManager INSTANCE = null;
    
    private DictionaryManager() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.cleverkeyboard.app.data.WordDefinition getWordOfTheDay() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object lookupWord(@org.jetbrains.annotations.NotNull()
    java.lang.String word, @org.jetbrains.annotations.Nullable()
    java.lang.String apiKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.cleverkeyboard.app.data.WordDefinition> $completion) {
        return null;
    }
    
    private final com.cleverkeyboard.app.data.WordDefinition parseDefinitionJson(java.lang.String raw, java.lang.String originalWord) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.cleverkeyboard.app.data.db.entity.UserDictionaryEntity>> getPersonalWordsFlow(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addPersonalWord(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String word, @org.jetbrains.annotations.NotNull()
    java.lang.String lang, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deletePersonalWord(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String word, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object clearPersonalDictionary(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}