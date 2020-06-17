package com.example.tsinghuadaily.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Timestamp;

@Entity(tableName = "ChatMessage")
public class ChatMessage {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "uid")
    public int uid;
    @ColumnInfo(name = "isMeSend")
    public int isMeSend;
    @ColumnInfo(name = "content")
    public String content;
    @ColumnInfo(name = "time")
    public long time;


    public ChatMessage() {}

    public ChatMessage(int uid, int isMeSend, String content, String time)
    {
        this.uid = uid;
        this.isMeSend = isMeSend;
        this.content = content;
        this.time = Timestamp.valueOf(time).getTime();
    }

    public int getId() {
        return id;
    }

    public int getUid() {
        return uid;
    }

    public int getIsMeSend() {
        return isMeSend;
    }

    public String getContent() {
        return content;
    }

    public long getTime() {
        return time;
    }

}
