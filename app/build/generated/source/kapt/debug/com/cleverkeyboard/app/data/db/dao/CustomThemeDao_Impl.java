package com.cleverkeyboard.app.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.cleverkeyboard.app.data.db.entity.CustomThemeEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@SuppressWarnings({"unchecked", "deprecation"})
public final class CustomThemeDao_Impl implements CustomThemeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CustomThemeEntity> __insertionAdapterOfCustomThemeEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteThemeById;

  public CustomThemeDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCustomThemeEntity = new EntityInsertionAdapter<CustomThemeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `custom_themes` (`themeId`,`name`,`keyboardBgColor`,`keyBgColor`,`keyTextColor`,`accentColor`,`photoPath`,`photoDarkness`,`photoBlur`,`keyBordersMode`,`isFavorite`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CustomThemeEntity entity) {
        if (entity.getThemeId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getThemeId());
        }
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        statement.bindLong(3, entity.getKeyboardBgColor());
        statement.bindLong(4, entity.getKeyBgColor());
        statement.bindLong(5, entity.getKeyTextColor());
        statement.bindLong(6, entity.getAccentColor());
        if (entity.getPhotoPath() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPhotoPath());
        }
        statement.bindDouble(8, entity.getPhotoDarkness());
        statement.bindDouble(9, entity.getPhotoBlur());
        if (entity.getKeyBordersMode() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getKeyBordersMode());
        }
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(11, _tmp);
        statement.bindLong(12, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfDeleteThemeById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM custom_themes WHERE themeId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdateTheme(final CustomThemeEntity theme,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCustomThemeEntity.insert(theme);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteThemeById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteThemeById.acquire();
        int _argIndex = 1;
        if (id == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, id);
        }
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteThemeById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CustomThemeEntity>> getAllThemesFlow() {
    final String _sql = "SELECT * FROM custom_themes ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"custom_themes"}, new Callable<List<CustomThemeEntity>>() {
      @Override
      @NonNull
      public List<CustomThemeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfThemeId = CursorUtil.getColumnIndexOrThrow(_cursor, "themeId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfKeyboardBgColor = CursorUtil.getColumnIndexOrThrow(_cursor, "keyboardBgColor");
          final int _cursorIndexOfKeyBgColor = CursorUtil.getColumnIndexOrThrow(_cursor, "keyBgColor");
          final int _cursorIndexOfKeyTextColor = CursorUtil.getColumnIndexOrThrow(_cursor, "keyTextColor");
          final int _cursorIndexOfAccentColor = CursorUtil.getColumnIndexOrThrow(_cursor, "accentColor");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfPhotoDarkness = CursorUtil.getColumnIndexOrThrow(_cursor, "photoDarkness");
          final int _cursorIndexOfPhotoBlur = CursorUtil.getColumnIndexOrThrow(_cursor, "photoBlur");
          final int _cursorIndexOfKeyBordersMode = CursorUtil.getColumnIndexOrThrow(_cursor, "keyBordersMode");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<CustomThemeEntity> _result = new ArrayList<CustomThemeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CustomThemeEntity _item;
            final String _tmpThemeId;
            if (_cursor.isNull(_cursorIndexOfThemeId)) {
              _tmpThemeId = null;
            } else {
              _tmpThemeId = _cursor.getString(_cursorIndexOfThemeId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final int _tmpKeyboardBgColor;
            _tmpKeyboardBgColor = _cursor.getInt(_cursorIndexOfKeyboardBgColor);
            final int _tmpKeyBgColor;
            _tmpKeyBgColor = _cursor.getInt(_cursorIndexOfKeyBgColor);
            final int _tmpKeyTextColor;
            _tmpKeyTextColor = _cursor.getInt(_cursorIndexOfKeyTextColor);
            final int _tmpAccentColor;
            _tmpAccentColor = _cursor.getInt(_cursorIndexOfAccentColor);
            final String _tmpPhotoPath;
            if (_cursor.isNull(_cursorIndexOfPhotoPath)) {
              _tmpPhotoPath = null;
            } else {
              _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            }
            final float _tmpPhotoDarkness;
            _tmpPhotoDarkness = _cursor.getFloat(_cursorIndexOfPhotoDarkness);
            final float _tmpPhotoBlur;
            _tmpPhotoBlur = _cursor.getFloat(_cursorIndexOfPhotoBlur);
            final String _tmpKeyBordersMode;
            if (_cursor.isNull(_cursorIndexOfKeyBordersMode)) {
              _tmpKeyBordersMode = null;
            } else {
              _tmpKeyBordersMode = _cursor.getString(_cursorIndexOfKeyBordersMode);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new CustomThemeEntity(_tmpThemeId,_tmpName,_tmpKeyboardBgColor,_tmpKeyBgColor,_tmpKeyTextColor,_tmpAccentColor,_tmpPhotoPath,_tmpPhotoDarkness,_tmpPhotoBlur,_tmpKeyBordersMode,_tmpIsFavorite,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<CustomThemeEntity>> getFavoriteThemesFlow() {
    final String _sql = "SELECT * FROM custom_themes WHERE isFavorite = 1 ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"custom_themes"}, new Callable<List<CustomThemeEntity>>() {
      @Override
      @NonNull
      public List<CustomThemeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfThemeId = CursorUtil.getColumnIndexOrThrow(_cursor, "themeId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfKeyboardBgColor = CursorUtil.getColumnIndexOrThrow(_cursor, "keyboardBgColor");
          final int _cursorIndexOfKeyBgColor = CursorUtil.getColumnIndexOrThrow(_cursor, "keyBgColor");
          final int _cursorIndexOfKeyTextColor = CursorUtil.getColumnIndexOrThrow(_cursor, "keyTextColor");
          final int _cursorIndexOfAccentColor = CursorUtil.getColumnIndexOrThrow(_cursor, "accentColor");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfPhotoDarkness = CursorUtil.getColumnIndexOrThrow(_cursor, "photoDarkness");
          final int _cursorIndexOfPhotoBlur = CursorUtil.getColumnIndexOrThrow(_cursor, "photoBlur");
          final int _cursorIndexOfKeyBordersMode = CursorUtil.getColumnIndexOrThrow(_cursor, "keyBordersMode");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<CustomThemeEntity> _result = new ArrayList<CustomThemeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CustomThemeEntity _item;
            final String _tmpThemeId;
            if (_cursor.isNull(_cursorIndexOfThemeId)) {
              _tmpThemeId = null;
            } else {
              _tmpThemeId = _cursor.getString(_cursorIndexOfThemeId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final int _tmpKeyboardBgColor;
            _tmpKeyboardBgColor = _cursor.getInt(_cursorIndexOfKeyboardBgColor);
            final int _tmpKeyBgColor;
            _tmpKeyBgColor = _cursor.getInt(_cursorIndexOfKeyBgColor);
            final int _tmpKeyTextColor;
            _tmpKeyTextColor = _cursor.getInt(_cursorIndexOfKeyTextColor);
            final int _tmpAccentColor;
            _tmpAccentColor = _cursor.getInt(_cursorIndexOfAccentColor);
            final String _tmpPhotoPath;
            if (_cursor.isNull(_cursorIndexOfPhotoPath)) {
              _tmpPhotoPath = null;
            } else {
              _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            }
            final float _tmpPhotoDarkness;
            _tmpPhotoDarkness = _cursor.getFloat(_cursorIndexOfPhotoDarkness);
            final float _tmpPhotoBlur;
            _tmpPhotoBlur = _cursor.getFloat(_cursorIndexOfPhotoBlur);
            final String _tmpKeyBordersMode;
            if (_cursor.isNull(_cursorIndexOfKeyBordersMode)) {
              _tmpKeyBordersMode = null;
            } else {
              _tmpKeyBordersMode = _cursor.getString(_cursorIndexOfKeyBordersMode);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new CustomThemeEntity(_tmpThemeId,_tmpName,_tmpKeyboardBgColor,_tmpKeyBgColor,_tmpKeyTextColor,_tmpAccentColor,_tmpPhotoPath,_tmpPhotoDarkness,_tmpPhotoBlur,_tmpKeyBordersMode,_tmpIsFavorite,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getThemeById(final String id,
      final Continuation<? super CustomThemeEntity> $completion) {
    final String _sql = "SELECT * FROM custom_themes WHERE themeId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (id == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, id);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CustomThemeEntity>() {
      @Override
      @Nullable
      public CustomThemeEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfThemeId = CursorUtil.getColumnIndexOrThrow(_cursor, "themeId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfKeyboardBgColor = CursorUtil.getColumnIndexOrThrow(_cursor, "keyboardBgColor");
          final int _cursorIndexOfKeyBgColor = CursorUtil.getColumnIndexOrThrow(_cursor, "keyBgColor");
          final int _cursorIndexOfKeyTextColor = CursorUtil.getColumnIndexOrThrow(_cursor, "keyTextColor");
          final int _cursorIndexOfAccentColor = CursorUtil.getColumnIndexOrThrow(_cursor, "accentColor");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfPhotoDarkness = CursorUtil.getColumnIndexOrThrow(_cursor, "photoDarkness");
          final int _cursorIndexOfPhotoBlur = CursorUtil.getColumnIndexOrThrow(_cursor, "photoBlur");
          final int _cursorIndexOfKeyBordersMode = CursorUtil.getColumnIndexOrThrow(_cursor, "keyBordersMode");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final CustomThemeEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpThemeId;
            if (_cursor.isNull(_cursorIndexOfThemeId)) {
              _tmpThemeId = null;
            } else {
              _tmpThemeId = _cursor.getString(_cursorIndexOfThemeId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final int _tmpKeyboardBgColor;
            _tmpKeyboardBgColor = _cursor.getInt(_cursorIndexOfKeyboardBgColor);
            final int _tmpKeyBgColor;
            _tmpKeyBgColor = _cursor.getInt(_cursorIndexOfKeyBgColor);
            final int _tmpKeyTextColor;
            _tmpKeyTextColor = _cursor.getInt(_cursorIndexOfKeyTextColor);
            final int _tmpAccentColor;
            _tmpAccentColor = _cursor.getInt(_cursorIndexOfAccentColor);
            final String _tmpPhotoPath;
            if (_cursor.isNull(_cursorIndexOfPhotoPath)) {
              _tmpPhotoPath = null;
            } else {
              _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            }
            final float _tmpPhotoDarkness;
            _tmpPhotoDarkness = _cursor.getFloat(_cursorIndexOfPhotoDarkness);
            final float _tmpPhotoBlur;
            _tmpPhotoBlur = _cursor.getFloat(_cursorIndexOfPhotoBlur);
            final String _tmpKeyBordersMode;
            if (_cursor.isNull(_cursorIndexOfKeyBordersMode)) {
              _tmpKeyBordersMode = null;
            } else {
              _tmpKeyBordersMode = _cursor.getString(_cursorIndexOfKeyBordersMode);
            }
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new CustomThemeEntity(_tmpThemeId,_tmpName,_tmpKeyboardBgColor,_tmpKeyBgColor,_tmpKeyTextColor,_tmpAccentColor,_tmpPhotoPath,_tmpPhotoDarkness,_tmpPhotoBlur,_tmpKeyBordersMode,_tmpIsFavorite,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
