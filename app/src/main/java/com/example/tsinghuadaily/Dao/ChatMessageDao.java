package com.example.tsinghuadaily.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.tsinghuadaily.models.ChatMessage;

import java.util.List;

@Dao
public interface ChatMessageDao {
    @Query("SELECT * FROM ChatMessage WHERE uid = (:uid)")
    LiveData<List<ChatMessage>> getChatHistoryByUID(int uid);

    @Query("SELECT * FROM ChatMessage")
    List<ChatMessage> getAllChatHistory();

    @Query("with T(uid, time) as( select uid, max(time) from ChatMessage group by uid )\n" +
            "select * from T natural join ChatMessage order by time desc")
    LiveData<List<ChatMessage>> getChatDigest();

    @Query("select uid from ChatMessage group by uid")
    List<Integer> getContact();

    @Insert
    void insert(ChatMessage msg);

    @Insert
    void insertAll(ChatMessage... msg);


}
