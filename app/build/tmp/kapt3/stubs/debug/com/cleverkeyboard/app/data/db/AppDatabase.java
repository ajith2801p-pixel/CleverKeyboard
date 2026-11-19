package com.cleverkeyboard.app.data.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \u000b2\u00020\u0001:\u0001\u000bB\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\nH&\u00a8\u0006\f"}, d2 = {"Lcom/cleverkeyboard/app/data/db/AppDatabase;", "Landroidx/room/RoomDatabase;", "()V", "customThemeDao", "Lcom/cleverkeyboard/app/data/db/dao/CustomThemeDao;", "savedSnippetDao", "Lcom/cleverkeyboard/app/data/db/dao/SavedSnippetDao;", "userDictionaryDao", "Lcom/cleverkeyboard/app/data/db/dao/UserDictionaryDao;", "wordShortcutDao", "Lcom/cleverkeyboard/app/data/db/dao/WordShortcutDao;", "Companion", "app_debug"})
@androidx.room.Database(entities = {com.cleverkeyboard.app.data.db.entity.UserDictionaryEntity.class, com.cleverkeyboard.app.data.db.entity.WordShortcutEntity.class, com.cleverkeyboard.app.data.db.entity.CustomThemeEntity.class, com.cleverkeyboard.app.data.db.entity.SavedSnippetEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends androidx.room.RoomDatabase {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String DB_NAME = "clever_keyboard_database";
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.cleverkeyboard.app.data.db.AppDatabase INSTANCE;
    @org.jetbrains.annotations.NotNull()
    public static final com.cleverkeyboard.app.data.db.AppDatabase.Companion Companion = null;
    
    public AppDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.cleverkeyboard.app.data.db.dao.UserDictionaryDao userDictionaryDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.cleverkeyboard.app.data.db.dao.WordShortcutDao wordShortcutDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.cleverkeyboard.app.data.db.dao.CustomThemeDao customThemeDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.cleverkeyboard.app.data.db.dao.SavedSnippetDao savedSnippetDao();
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\tR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/cleverkeyboard/app/data/db/AppDatabase$Companion;", "", "()V", "DB_NAME", "", "INSTANCE", "Lcom/cleverkeyboard/app/data/db/AppDatabase;", "getInstance", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.cleverkeyboard.app.data.db.AppDatabase getInstance(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}