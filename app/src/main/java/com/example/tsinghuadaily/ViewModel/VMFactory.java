package com.example.tsinghuadaily.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class VMFactory implements ViewModelProvider.Factory {
    private Application application;
    private final int uid;

    public VMFactory(Application application, int uid) {
        this.application = application;
        this.uid = uid;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ChatMessageForOneContactViewModel(application, uid);
    }
}
