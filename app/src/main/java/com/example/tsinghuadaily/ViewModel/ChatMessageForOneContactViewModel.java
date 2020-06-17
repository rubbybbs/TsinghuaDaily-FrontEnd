package com.example.tsinghuadaily.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.tsinghuadaily.Database.AppDatabase;
import com.example.tsinghuadaily.models.ChatMessage;

import java.util.List;

public class ChatMessageForOneContactViewModel extends AndroidViewModel {
    private AppDatabase db;
    private LiveData<List<ChatMessage>> liveDataChatMessage;

    public ChatMessageForOneContactViewModel(@NonNull Application application, int uid) {
        super(application);
        db = AppDatabase.getInstance(application);
        liveDataChatMessage = db.chatMsgDao().getChatHistoryByUID(uid);
    }

    public LiveData<List<ChatMessage>> getLiveDataChatMessage() {
        return liveDataChatMessage;
    }
}
