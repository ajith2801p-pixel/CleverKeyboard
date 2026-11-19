package com.cleverkeyboard.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.cleverkeyboard.app.data.db.dao.CustomThemeDao;
import com.cleverkeyboard.app.data.db.dao.CustomThemeDao_Impl;
import com.cleverkeyboard.app.data.db.dao.SavedSnippetDao;
import com.cleverkeyboard.app.data.db.dao.SavedSnippetDao_Impl;
import com.cleverkeyboard.app.data.db.dao.UserDictionaryDao;
import com.cleverkeyboard.app.data.db.dao.UserDictionaryDao_Impl;
import com.cleverkeyboard.app.data.db.dao.WordShortcutDao;
import com.cleverkeyboard.app.data.db.dao.WordShortcutDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile UserDictionaryDao _userDictionaryDao;

  private volatile WordShortcutDao _wordShortcutDao;

  private volatile CustomThemeDao _customThemeDao;

  private volatile SavedSnippetDao _savedSnippetDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_dictionary` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `word` TEXT NOT NULL, `frequency` INTEGER NOT NULL, `shortcut` TEXT, `language` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_user_dictionary_word` ON `user_dictionary` (`word`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_user_dictionary_language` ON `user_dictionary` (`language`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `word_shortcuts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `shortcut` TEXT NOT NULL, `expansion` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_word_shortcuts_shortcut` ON `word_shortcuts` (`shortcut`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `custom_themes` (`themeId` TEXT NOT NULL, `name` TEXT NOT NULL, `keyboardBgColor` INTEGER NOT NULL, `keyBgColor` INTEGER NOT NULL, `keyTextColor` INTEGER NOT NULL, `accentColor` INTEGER NOT NULL, `photoPath` TEXT, `photoDarkness` REAL NOT NULL, `photoBlur` REAL NOT NULL, `keyBordersMode` TEXT NOT NULL, `isFavorite` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`themeId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `saved_snippets` (`id` TEXT NOT NULL, `text` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `isPinned` INTEGER NOT NULL, `isSensitive` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'bd220d1cd7ff41a83b3b89b49c28717b')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `user_dictionary`");
        db.execSQL("DROP TABLE IF EXISTS `word_shortcuts`");
        db.execSQL("DROP TABLE IF EXISTS `custom_themes`");
        db.execSQL("DROP TABLE IF EXISTS `saved_snippets`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsUserDictionary = new HashMap<String, TableInfo.Column>(6);
        _columnsUserDictionary.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserDictionary.put("word", new TableInfo.Column("word", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserDictionary.put("frequency", new TableInfo.Column("frequency", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserDictionary.put("shortcut", new TableInfo.Column("shortcut", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserDictionary.put("language", new TableInfo.Column("language", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserDictionary.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserDictionary = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserDictionary = new HashSet<TableInfo.Index>(2);
        _indicesUserDictionary.add(new TableInfo.Index("index_user_dictionary_word", true, Arrays.asList("word"), Arrays.asList("ASC")));
        _indicesUserDictionary.add(new TableInfo.Index("index_user_dictionary_language", false, Arrays.asList("language"), Arrays.asList("ASC")));
        final TableInfo _infoUserDictionary = new TableInfo("user_dictionary", _columnsUserDictionary, _foreignKeysUserDictionary, _indicesUserDictionary);
        final TableInfo _existingUserDictionary = TableInfo.read(db, "user_dictionary");
        if (!_infoUserDictionary.equals(_existingUserDictionary)) {
          return new RoomOpenHelper.ValidationResult(false, "user_dictionary(com.cleverkeyboard.app.data.db.entity.UserDictionaryEntity).\n"
                  + " Expected:\n" + _infoUserDictionary + "\n"
                  + " Found:\n" + _existingUserDictionary);
        }
        final HashMap<String, TableInfo.Column> _columnsWordShortcuts = new HashMap<String, TableInfo.Column>(4);
        _columnsWordShortcuts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWordShortcuts.put("shortcut", new TableInfo.Column("shortcut", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWordShortcuts.put("expansion", new TableInfo.Column("expansion", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWordShortcuts.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWordShortcuts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWordShortcuts = new HashSet<TableInfo.Index>(1);
        _indicesWordShortcuts.add(new TableInfo.Index("index_word_shortcuts_shortcut", true, Arrays.asList("shortcut"), Arrays.asList("ASC")));
        final TableInfo _infoWordShortcuts = new TableInfo("word_shortcuts", _columnsWordShortcuts, _foreignKeysWordShortcuts, _indicesWordShortcuts);
        final TableInfo _existingWordShortcuts = TableInfo.read(db, "word_shortcuts");
        if (!_infoWordShortcuts.equals(_existingWordShortcuts)) {
          return new RoomOpenHelper.ValidationResult(false, "word_shortcuts(com.cleverkeyboard.app.data.db.entity.WordShortcutEntity).\n"
                  + " Expected:\n" + _infoWordShortcuts + "\n"
                  + " Found:\n" + _existingWordShortcuts);
        }
        final HashMap<String, TableInfo.Column> _columnsCustomThemes = new HashMap<String, TableInfo.Column>(12);
        _columnsCustomThemes.put("themeId", new TableInfo.Column("themeId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomThemes.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomThemes.put("keyboardBgColor", new TableInfo.Column("keyboardBgColor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomThemes.put("keyBgColor", new TableInfo.Column("keyBgColor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomThemes.put("keyTextColor", new TableInfo.Column("keyTextColor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomThemes.put("accentColor", new TableInfo.Column("accentColor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomThemes.put("photoPath", new TableInfo.Column("photoPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomThemes.put("photoDarkness", new TableInfo.Column("photoDarkness", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomThemes.put("photoBlur", new TableInfo.Column("photoBlur", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomThemes.put("keyBordersMode", new TableInfo.Column("keyBordersMode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomThemes.put("isFavorite", new TableInfo.Column("isFavorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomThemes.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCustomThemes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCustomThemes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCustomThemes = new TableInfo("custom_themes", _columnsCustomThemes, _foreignKeysCustomThemes, _indicesCustomThemes);
        final TableInfo _existingCustomThemes = TableInfo.read(db, "custom_themes");
        if (!_infoCustomThemes.equals(_existingCustomThemes)) {
          return new RoomOpenHelper.ValidationResult(false, "custom_themes(com.cleverkeyboard.app.data.db.entity.CustomThemeEntity).\n"
                  + " Expected:\n" + _infoCustomThemes + "\n"
                  + " Found:\n" + _existingCustomThemes);
        }
        final HashMap<String, TableInfo.Column> _columnsSavedSnippets = new HashMap<String, TableInfo.Column>(5);
        _columnsSavedSnippets.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedSnippets.put("text", new TableInfo.Column("text", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedSnippets.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedSnippets.put("isPinned", new TableInfo.Column("isPinned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedSnippets.put("isSensitive", new TableInfo.Column("isSensitive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSavedSnippets = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSavedSnippets = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSavedSnippets = new TableInfo("saved_snippets", _columnsSavedSnippets, _foreignKeysSavedSnippets, _indicesSavedSnippets);
        final TableInfo _existingSavedSnippets = TableInfo.read(db, "saved_snippets");
        if (!_infoSavedSnippets.equals(_existingSavedSnippets)) {
          return new RoomOpenHelper.ValidationResult(false, "saved_snippets(com.cleverkeyboard.app.data.db.entity.SavedSnippetEntity).\n"
                  + " Expected:\n" + _infoSavedSnippets + "\n"
                  + " Found:\n" + _existingSavedSnippets);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "bd220d1cd7ff41a83b3b89b49c28717b", "262a9e1d85765308b5a6a3126aa7283f");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "user_dictionary","word_shortcuts","custom_themes","saved_snippets");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `user_dictionary`");
      _db.execSQL("DELETE FROM `word_shortcuts`");
      _db.execSQL("DELETE FROM `custom_themes`");
      _db.execSQL("DELETE FROM `saved_snippets`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UserDictionaryDao.class, UserDictionaryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WordShortcutDao.class, WordShortcutDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CustomThemeDao.class, CustomThemeDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SavedSnippetDao.class, SavedSnippetDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public UserDictionaryDao userDictionaryDao() {
    if (_userDictionaryDao != null) {
      return _userDictionaryDao;
    } else {
      synchronized(this) {
        if(_userDictionaryDao == null) {
          _userDictionaryDao = new UserDictionaryDao_Impl(this);
        }
        return _userDictionaryDao;
      }
    }
  }

  @Override
  public WordShortcutDao wordShortcutDao() {
    if (_wordShortcutDao != null) {
      return _wordShortcutDao;
    } else {
      synchronized(this) {
        if(_wordShortcutDao == null) {
          _wordShortcutDao = new WordShortcutDao_Impl(this);
        }
        return _wordShortcutDao;
      }
    }
  }

  @Override
  public CustomThemeDao customThemeDao() {
    if (_customThemeDao != null) {
      return _customThemeDao;
    } else {
      synchronized(this) {
        if(_customThemeDao == null) {
          _customThemeDao = new CustomThemeDao_Impl(this);
        }
        return _customThemeDao;
      }
    }
  }

  @Override
  public SavedSnippetDao savedSnippetDao() {
    if (_savedSnippetDao != null) {
      return _savedSnippetDao;
    } else {
      synchronized(this) {
        if(_savedSnippetDao == null) {
          _savedSnippetDao = new SavedSnippetDao_Impl(this);
        }
        return _savedSnippetDao;
      }
    }
  }
}
