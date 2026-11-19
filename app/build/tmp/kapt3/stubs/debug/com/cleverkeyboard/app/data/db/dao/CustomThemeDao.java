package com.cleverkeyboard.app.data.db.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\bH\'J\u0014\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\bH\'J\u0018\u0010\f\u001a\u0004\u0018\u00010\n2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u000f\u00a8\u0006\u0010"}, d2 = {"Lcom/cleverkeyboard/app/data/db/dao/CustomThemeDao;", "", "deleteThemeById", "", "id", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllThemesFlow", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/cleverkeyboard/app/data/db/entity/CustomThemeEntity;", "getFavoriteThemesFlow", "getThemeById", "insertOrUpdateTheme", "theme", "(Lcom/cleverkeyboard/app/data/db/entity/CustomThemeEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface CustomThemeDao {
    
    @androidx.room.Query(value = "SELECT * FROM custom_themes ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.cleverkeyboard.app.data.db.entity.CustomThemeEntity>> getAllThemesFlow();
    
    @androidx.room.Query(value = "SELECT * FROM custom_themes WHERE isFavorite = 1 ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.cleverkeyboard.app.data.db.entity.CustomThemeEntity>> getFavoriteThemesFlow();
    
    @androidx.room.Query(value = "SELECT * FROM custom_themes WHERE themeId = :id LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getThemeById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.cleverkeyboard.app.data.db.entity.CustomThemeEntity> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertOrUpdateTheme(@org.jetbrains.annotations.NotNull()
    com.cleverkeyboard.app.data.db.entity.CustomThemeEntity theme, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM custom_themes WHERE themeId = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteThemeById(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}