package com.example.tsinghuadaily.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.tsinghuadaily.Database.AppDatabase;
import com.example.tsinghuadaily.models.ChatMessage;

import java.util.List;

public class ChatMesssageDigestViewModel extends AndroidViewModel {

    private AppDatabase db;
    private LiveData<List<ChatMessage>> liveDataChatMsgDigest;

    public ChatMesssageDigestViewModel(@NonNull Application application, int uid) {
        super(application);
        db = AppDatabase.getInstance(application, uid);
        liveDataChatMsgDigest = db.chatMsgDao().getChatDigest();
    }

    public LiveData<List<ChatMessage>> getLiveDataChatMsgDigest() {return liveDataChatMsgDigest;}
}
