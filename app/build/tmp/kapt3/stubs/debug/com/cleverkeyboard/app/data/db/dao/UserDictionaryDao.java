package com.cleverkeyboard.app.data.db.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0005\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0018\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\r0\fH\'J&\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\n0\r2\u0006\u0010\u000f\u001a\u00020\u00072\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u0012J\u001c\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\n0\r2\u0006\u0010\u0014\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ \u0010\u0015\u001a\u00020\u00112\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\u0016\u001a\u00020\u0017H\u00a7@\u00a2\u0006\u0002\u0010\u0018J\u0016\u0010\u0019\u001a\u00020\u00032\u0006\u0010\u001a\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u001b\u00a8\u0006\u001c"}, d2 = {"Lcom/cleverkeyboard/app/data/db/dao/UserDictionaryDao;", "", "clearAll", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteWord", "word", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "findWord", "Lcom/cleverkeyboard/app/data/db/entity/UserDictionaryEntity;", "getAllWordsFlow", "Lkotlinx/coroutines/flow/Flow;", "", "getSuggestionsWithPrefix", "prefix", "limit", "", "(Ljava/lang/String;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getWordsForLanguage", "lang", "incrementFrequency", "timestamp", "", "(Ljava/lang/String;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertOrUpdate", "entity", "(Lcom/cleverkeyboard/app/data/db/entity/UserDictionaryEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface UserDictionaryDao {
    
    @androidx.room.Query(value = "SELECT * FROM user_dictionary ORDER BY frequency DESC, updatedAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.cleverkeyboard.app.data.db.entity.UserDictionaryEntity>> getAllWordsFlow();
    
    @androidx.room.Query(value = "SELECT * FROM user_dictionary WHERE language = :lang ORDER BY frequency DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getWordsForLanguage(@org.jetbrains.annotations.NotNull()
    java.lang.String lang, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.cleverkeyboard.app.data.db.entity.UserDictionaryEntity>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM user_dictionary WHERE word LIKE :prefix || \'%\' ORDER BY frequency DESC LIMIT :limit")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getSuggestionsWithPrefix(@org.jetbrains.annotations.NotNull()
    java.lang.String prefix, int limit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.cleverkeyboard.app.data.db.entity.UserDictionaryEntity>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM user_dictionary WHERE word = :word LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object findWord(@org.jetbrains.annotations.NotNull()
    java.lang.String word, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.cleverkeyboard.app.data.db.entity.UserDictionaryEntity> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertOrUpdate(@org.jetbrains.annotations.NotNull()
    com.cleverkeyboard.app.data.db.entity.UserDictionaryEntity entity, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE user_dictionary SET frequency = frequency + 1, updatedAt = :timestamp WHERE word = :word")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object incrementFrequency(@org.jetbrains.annotations.NotNull()
    java.lang.String word, long timestamp, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Query(value = "DELETE FROM user_dictionary WHERE word = :word")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteWord(@org.jetbrains.annotations.NotNull()
    java.lang.String word, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM user_dictionary")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}