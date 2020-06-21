package com.example.tsinghuadaily.Dao;

import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.tsinghuadaily.models.ChatMessage;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class ChatMessageDao_Impl implements ChatMessageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ChatMessage> __insertionAdapterOfChatMessage;

  public ChatMessageDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfChatMessage = new EntityInsertionAdapter<ChatMessage>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `ChatMessage` (`id`,`uid`,`isMeSend`,`content`,`time`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ChatMessage value) {
        stmt.bindLong(1, value.id);
        stmt.bindLong(2, value.uid);
        stmt.bindLong(3, value.isMeSend);
        if (value.content == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.content);
        }
        stmt.bindLong(5, value.time);
      }
    };
  }

  @Override
  public void insert(final ChatMessage msg) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfChatMessage.insert(msg);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertAll(final ChatMessage... msg) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfChatMessage.insert(msg);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<ChatMessage>> getChatHistoryByUID(final int uid) {
    final String _sql = "SELECT * FROM ChatMessage WHERE uid = (?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, uid);
    return __db.getInvalidationTracker().createLiveData(new String[]{"ChatMessage"}, false, new Callable<List<ChatMessage>>() {
      @Override
      public List<ChatMessage> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUid = CursorUtil.getColumnIndexOrThrow(_cursor, "uid");
          final int _cursorIndexOfIsMeSend = CursorUtil.getColumnIndexOrThrow(_cursor, "isMeSend");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
          final List<ChatMessage> _result = new ArrayList<ChatMessage>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final ChatMessage _item;
            _item = new ChatMessage();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            _item.uid = _cursor.getInt(_cursorIndexOfUid);
            _item.isMeSend = _cursor.getInt(_cursorIndexOfIsMeSend);
            _item.content = _cursor.getString(_cursorIndexOfContent);
            _item.time = _cursor.getLong(_cursorIndexOfTime);
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
  public List<ChatMessage> getAllChatHistory() {
    final String _sql = "SELECT * FROM ChatMessage";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfUid = CursorUtil.getColumnIndexOrThrow(_cursor, "uid");
      final int _cursorIndexOfIsMeSend = CursorUtil.getColumnIndexOrThrow(_cursor, "isMeSend");
      final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
      final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
      final List<ChatMessage> _result = new ArrayList<ChatMessage>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ChatMessage _item;
        _item = new ChatMessage();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        _item.uid = _cursor.getInt(_cursorIndexOfUid);
        _item.isMeSend = _cursor.getInt(_cursorIndexOfIsMeSend);
        _item.content = _cursor.getString(_cursorIndexOfContent);
        _item.time = _cursor.getLong(_cursorIndexOfTime);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public LiveData<List<ChatMessage>> getChatDigest() {
    final String _sql = "with T(uid, time) as( select uid, max(time) from ChatMessage group by uid )\n"
            + "select * from T natural join ChatMessage order by time desc";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"ChatMessage"}, false, new Callable<List<ChatMessage>>() {
      @Override
      public List<ChatMessage> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfUid = CursorUtil.getColumnIndexOrThrow(_cursor, "uid");
          final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIsMeSend = CursorUtil.getColumnIndexOrThrow(_cursor, "isMeSend");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final List<ChatMessage> _result = new ArrayList<ChatMessage>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final ChatMessage _item;
            _item = new ChatMessage();
            _item.uid = _cursor.getInt(_cursorIndexOfUid);
            _item.time = _cursor.getLong(_cursorIndexOfTime);
            _item.id = _cursor.getInt(_cursorIndexOfId);
            _item.isMeSend = _cursor.getInt(_cursorIndexOfIsMeSend);
            _item.content = _cursor.getString(_cursorIndexOfContent);
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
  public List<Integer> getContact() {
    final String _sql = "select uid from ChatMessage group by uid";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final List<Integer> _result = new ArrayList<Integer>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Integer _item;
        if (_cursor.isNull(0)) {
          _item = null;
        } else {
          _item = _cursor.getInt(0);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
