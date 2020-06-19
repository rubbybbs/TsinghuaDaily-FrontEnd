package com.example.tsinghuadaily.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.LiveData;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.tsinghuadaily.Database.AppDatabase;
import com.example.tsinghuadaily.Fragment.ArticleDetailFragment;
import com.example.tsinghuadaily.Fragment.MainPageFragment;
import com.example.tsinghuadaily.Fragment.MessageFragment;
import com.example.tsinghuadaily.base.BaseFragmentActivity;
import com.example.tsinghuadaily.models.ChatMessage;
import com.example.tsinghuadaily.services.WebSocketService;
import com.example.tsinghuadaily.utils.JWebSocketClient;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager;
import com.qmuiteam.qmui.arch.SwipeBackLayout;
import com.qmuiteam.qmui.arch.annotation.DefaultFirstFragment;
import com.qmuiteam.qmui.arch.annotation.FirstFragments;
import com.qmuiteam.qmui.arch.annotation.LatestVisitRecord;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@FirstFragments(
        value = {
                MainPageFragment.class,
                MessageFragment.class,
                ArticleDetailFragment.class
        })
@DefaultFirstFragment(MainPageFragment.class)
@LatestVisitRecord
public class MainPageActivity extends BaseFragmentActivity {

    // WebSocket使用
    private Context mContext;
    private JWebSocketClient client;
    private WebSocketService.JWebSocketClientBinder binder;
    private WebSocketService WSService;
    private MainPageActivity.ChatMessageReceiver chatMessageReceiver;

    AppDatabase db;

    private int UID;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化QMUISwipeBackActicityManager,防止崩溃
        QMUISwipeBackActivityManager.init(this.getApplication());
        SharedPreferences preferences = getSharedPreferences("userdata", MODE_PRIVATE);
        UID = preferences.getInt("uid", 0);
        username = preferences.getString("username", "");


        initChatLog();
        db = AppDatabase.getInstance(getApplicationContext(), UID);
        mContext = MainPageActivity.this;
        startWebSocketService();
        bindService();
        doRegisterReceiver();
    }

    @Override
    protected RootView onCreateRootView(int fragmentContainerId) {
        return new CustomRootView(this, fragmentContainerId);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public static Intent of(@NonNull Context context,
                            @NonNull Class<? extends QMUIFragment> firstFragment) {
        return QMUIFragmentActivity.intentOf(context, MainPageActivity.class, firstFragment);
    }

    public static Intent of(@NonNull Context context,
                            @NonNull Class<? extends QMUIFragment> firstFragment,
                            @Nullable Bundle fragmentArgs) {
        return QMUIFragmentActivity.intentOf(context, MainPageActivity.class, firstFragment, fragmentArgs);
    }

    class CustomRootView extends RootView {

        private FragmentContainerView fragmentContainer;

        public CustomRootView(Context context, int fragmentContainerId) {
            super(context, fragmentContainerId);

            fragmentContainer = new FragmentContainerView(context);
            fragmentContainer.setId(fragmentContainerId);
            fragmentContainer.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    for (int i = 0; i < getChildCount(); i++) {
                        SwipeBackLayout.updateLayoutInSwipeBack(getChildAt(i));
                    }
                }
            });
            addView(fragmentContainer, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        @Override
        public FragmentContainerView getFragmentContainerView() {
            return fragmentContainer;
        }
    }


    private void initChatLog() {
        File filesDir = getFilesDir();
        String filePath = filesDir.getAbsolutePath();
        String sepa = File.pathSeparator;
        File chatLogDir = new File(filePath + "ChatLog");
        while (!chatLogDir.isDirectory())
            chatLogDir.mkdir();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("MainPageActivity", "服务与活动成功绑定");
            binder = (WebSocketService.JWebSocketClientBinder) iBinder;
            WSService = binder.getService();
            client = WSService.client;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("MainPageActivity", "服务与活动成功断开");
        }
    };

    private class ChatMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //TODO: 聊天记录的持久化
            String message=intent.getStringExtra("message");
            Log.e("MainPageActivity", message);
            JSONObject obj = JSONObject.parseObject(message);
            if (obj.containsKey("messages"))
            {
                JSONArray msgList = (JSONArray) obj.get("messages");
                for (int i = 0; i < msgList.size(); i++)
                {
                    JSONObject o = (JSONObject) msgList.get(i);
                    String content = o.get("content").toString();
                    String time = o.get("send_time").toString();
                    int sender = (int) o.get("sender_id");
                    new InsertChatMsgTask(sender, 0, content, time).execute();
                }

            }
            else
            {
                String content = obj.get("content").toString();
                String time = obj.get("send_time").toString();
                int sender = (int) obj.get("sender_id");
                if (sender == UID)
                {
                    int to = (int) obj.get("to");
                    new InsertChatMsgTask(to, 1, content, time).execute();
                }
                else
                {
                    new InsertChatMsgTask(sender, 0, content, time).execute();
                }

            }
        }
    }


    private void startWebSocketService() {
        Intent intent = new Intent(mContext, WebSocketService.class);
        intent.putExtra("uid", String.valueOf(UID));
        startService(intent);
    }

    private void bindService() {
        Intent bindIntent = new Intent(mContext, WebSocketService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }



    private void doRegisterReceiver() {
        chatMessageReceiver = new MainPageActivity.ChatMessageReceiver();
        IntentFilter filter = new IntentFilter("com.xch.servicecallback.content");
        registerReceiver(chatMessageReceiver, filter);
    }

    private class InsertChatMsgTask extends AsyncTask<Void, Void, Void> {
        int sender;
        int isMeSend;
        String content;
        String time;

        public InsertChatMsgTask(final int sender, final int isMeSend, final String content, final String time) {
            this.sender = sender;
            this.isMeSend = isMeSend;
            this.content = content;
            this.time = time;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            db.chatMsgDao().insert(new ChatMessage(sender, isMeSend, content, time));
            return null;
        }
    }




}
