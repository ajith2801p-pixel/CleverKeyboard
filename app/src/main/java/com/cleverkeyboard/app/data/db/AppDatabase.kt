package com.cleverkeyboard.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cleverkeyboard.app.data.db.dao.CustomThemeDao
import com.cleverkeyboard.app.data.db.dao.SavedSnippetDao
import com.cleverkeyboard.app.data.db.dao.UserDictionaryDao
import com.cleverkeyboard.app.data.db.dao.WordShortcutDao
import com.cleverkeyboard.app.data.db.entity.CustomThemeEntity
import com.cleverkeyboard.app.data.db.entity.SavedSnippetEntity
import com.cleverkeyboard.app.data.db.entity.UserDictionaryEntity
import com.cleverkeyboard.app.data.db.entity.WordShortcutEntity

@Database(
    entities = [
        UserDictionaryEntity::class,
        WordShortcutEntity::class,
        CustomThemeEntity::class,
        SavedSnippetEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDictionaryDao(): UserDictionaryDao
    abstract fun wordShortcutDao(): WordShortcutDao
    abstract fun customThemeDao(): CustomThemeDao
    abstract fun savedSnippetDao(): SavedSnippetDao

    companion object {
        private const val DB_NAME = "clever_keyboard_database"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
