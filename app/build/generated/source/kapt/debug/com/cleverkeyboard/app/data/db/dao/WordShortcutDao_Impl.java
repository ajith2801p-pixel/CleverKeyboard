package com.cleverkeyboard.app.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.cleverkeyboard.app.data.db.entity.WordShortcutEntity;
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
public final class WordShortcutDao_Impl implements WordShortcutDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WordShortcutEntity> __insertionAdapterOfWordShortcutEntity;

  private final EntityDeletionOrUpdateAdapter<WordShortcutEntity> __deletionAdapterOfWordShortcutEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByShortcut;

  public WordShortcutDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWordShortcutEntity = new EntityInsertionAdapter<WordShortcutEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `word_shortcuts` (`id`,`shortcut`,`expansion`,`createdAt`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WordShortcutEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getShortcut() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getShortcut());
        }
        if (entity.getExpansion() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getExpansion());
        }
        statement.bindLong(4, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfWordShortcutEntity = new EntityDeletionOrUpdateAdapter<WordShortcutEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `word_shortcuts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WordShortcutEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteByShortcut = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM word_shortcuts WHERE shortcut = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertShortcut(final WordShortcutEntity shortcut,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWordShortcutEntity.insert(shortcut);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteShortcut(final WordShortcutEntity shortcut,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfWordShortcutEntity.handle(shortcut);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByShortcut(final String shortcut,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByShortcut.acquire();
        int _argIndex = 1;
        if (shortcut == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, shortcut);
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
          __preparedStmtOfDeleteByShortcut.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<WordShortcutEntity>> getAllShortcutsFlow() {
    final String _sql = "SELECT * FROM word_shortcuts ORDER BY shortcut ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"word_shortcuts"}, new Callable<List<WordShortcutEntity>>() {
      @Override
      @NonNull
      public List<WordShortcutEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfShortcut = CursorUtil.getColumnIndexOrThrow(_cursor, "shortcut");
          final int _cursorIndexOfExpansion = CursorUtil.getColumnIndexOrThrow(_cursor, "expansion");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<WordShortcutEntity> _result = new ArrayList<WordShortcutEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WordShortcutEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpShortcut;
            if (_cursor.isNull(_cursorIndexOfShortcut)) {
              _tmpShortcut = null;
            } else {
              _tmpShortcut = _cursor.getString(_cursorIndexOfShortcut);
            }
            final String _tmpExpansion;
            if (_cursor.isNull(_cursorIndexOfExpansion)) {
              _tmpExpansion = null;
            } else {
              _tmpExpansion = _cursor.getString(_cursorIndexOfExpansion);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new WordShortcutEntity(_tmpId,_tmpShortcut,_tmpExpansion,_tmpCreatedAt);
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
  public Object findShortcut(final String shortcut,
      final Continuation<? super WordShortcutEntity> $completion) {
    final String _sql = "SELECT * FROM word_shortcuts WHERE shortcut = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (shortcut == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, shortcut);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<WordShortcutEntity>() {
      @Override
      @Nullable
      public WordShortcutEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfShortcut = CursorUtil.getColumnIndexOrThrow(_cursor, "shortcut");
          final int _cursorIndexOfExpansion = CursorUtil.getColumnIndexOrThrow(_cursor, "expansion");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final WordShortcutEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpShortcut;
            if (_cursor.isNull(_cursorIndexOfShortcut)) {
              _tmpShortcut = null;
            } else {
              _tmpShortcut = _cursor.getString(_cursorIndexOfShortcut);
            }
            final String _tmpExpansion;
            if (_cursor.isNull(_cursorIndexOfExpansion)) {
              _tmpExpansion = null;
            } else {
              _tmpExpansion = _cursor.getString(_cursorIndexOfExpansion);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new WordShortcutEntity(_tmpId,_tmpShortcut,_tmpExpansion,_tmpCreatedAt);
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
