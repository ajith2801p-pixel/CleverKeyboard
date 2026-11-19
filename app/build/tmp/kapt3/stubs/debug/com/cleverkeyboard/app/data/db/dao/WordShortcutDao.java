package com.cleverkeyboard.app.data.db.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0018\u0010\n\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\r0\fH\'J\u0016\u0010\u000e\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\t\u00a8\u0006\u000f"}, d2 = {"Lcom/cleverkeyboard/app/data/db/dao/WordShortcutDao;", "", "deleteByShortcut", "", "shortcut", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteShortcut", "Lcom/cleverkeyboard/app/data/db/entity/WordShortcutEntity;", "(Lcom/cleverkeyboard/app/data/db/entity/WordShortcutEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "findShortcut", "getAllShortcutsFlow", "Lkotlinx/coroutines/flow/Flow;", "", "insertShortcut", "app_debug"})
@androidx.room.Dao()
public abstract interface WordShortcutDao {
    
    @androidx.room.Query(value = "SELECT * FROM word_shortcuts ORDER BY shortcut ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.cleverkeyboard.app.data.db.entity.WordShortcutEntity>> getAllShortcutsFlow();
    
    @androidx.room.Query(value = "SELECT * FROM word_shortcuts WHERE shortcut = :shortcut LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object findShortcut(@org.jetbrains.annotations.NotNull()
    java.lang.String shortcut, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.cleverkeyboard.app.data.db.entity.WordShortcutEntity> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertShortcut(@org.jetbrains.annotations.NotNull()
    com.cleverkeyboard.app.data.db.entity.WordShortcutEntity shortcut, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteShortcut(@org.jetbrains.annotations.NotNull()
    com.cleverkeyboard.app.data.db.entity.WordShortcutEntity shortcut, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM word_shortcuts WHERE shortcut = :shortcut")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteByShortcut(@org.jetbrains.annotations.NotNull()
    java.lang.String shortcut, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}