package com.example.tsinghuadaily.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.tsinghuadaily.Dao.ChatMessageDao;
import com.example.tsinghuadaily.models.ChatMessage;

import java.util.List;

@Database(entities = {ChatMessage.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase sInstance;

    private static final String DATABASE_NAME = "tsinghua-daily-db";

    public abstract ChatMessageDao chatMsgDao();


    public static AppDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private static AppDatabase buildDatabase(final Context appContext) {
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

}
