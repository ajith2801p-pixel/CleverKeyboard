package com.cleverkeyboard.app.data.db.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ \u0010\t\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0014\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00110\u00100\u000fH\'J\u001e\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\b\b\u0002\u0010\u0013\u001a\u00020\u0014H\u00a7@\u00a2\u0006\u0002\u0010\u0015J\u0016\u0010\u0016\u001a\u00020\u00032\u0006\u0010\u0017\u001a\u00020\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u0018J\u001e\u0010\u0019\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u001a\u001a\u00020\u001bH\u00a7@\u00a2\u0006\u0002\u0010\u001c\u00a8\u0006\u001d"}, d2 = {"Lcom/cleverkeyboard/app/data/db/dao/SavedSnippetDao;", "", "clearUnpinned", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteById", "id", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteExpiredSensitive", "currentTime", "", "maxAgeMs", "(JJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllSnippetsFlow", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/cleverkeyboard/app/data/db/entity/SavedSnippetEntity;", "getRecentSnippets", "limit", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertOrUpdate", "snippet", "(Lcom/cleverkeyboard/app/data/db/entity/SavedSnippetEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setPinned", "pinned", "", "(Ljava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface SavedSnippetDao {
    
    @androidx.room.Query(value = "SELECT * FROM saved_snippets ORDER BY isPinned DESC, timestamp DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.cleverkeyboard.app.data.db.entity.SavedSnippetEntity>> getAllSnippetsFlow();
    
    @androidx.room.Query(value = "SELECT * FROM saved_snippets ORDER BY isPinned DESC, timestamp DESC LIMIT :limit")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getRecentSnippets(int limit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.cleverkeyboard.app.data.db.entity.SavedSnippetEntity>> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertOrUpdate(@org.jetbrains.annotations.NotNull()
    com.cleverkeyboard.app.data.db.entity.SavedSnippetEntity snippet, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE saved_snippets SET isPinned = :pinned WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object setPinned(@org.jetbrains.annotations.NotNull()
    java.lang.String id, boolean pinned, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM saved_snippets WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM saved_snippets WHERE isPinned = 0")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearUnpinned(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM saved_snippets WHERE isSensitive = 1 AND isPinned = 0 AND (:currentTime - timestamp) > :maxAgeMs")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteExpiredSensitive(long currentTime, long maxAgeMs, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}