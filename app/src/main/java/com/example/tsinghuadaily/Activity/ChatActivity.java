package com.example.tsinghuadaily.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.models.messageTest;
import com.example.tsinghuadaily.services.WebSocketService;
import com.example.tsinghuadaily.utils.JWebSocketClient;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int VIEW_TYPE_USER_MSG = 0;
    public static final int VIEW_TYPE_FRIEND_MSG = 1;


    private QMUITopBar mTopBar;
    private RecyclerView messageRecylerView;
    private ImageButton sendBtn;
    private EditText messageEdit;

    private ArrayList<messageTest> testlist;

    private ListMessageAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private String contact;


    // WebSocket使用
    private Context mContext;
    private JWebSocketClient client;
    private WebSocketService.JWebSocketClientBinder binder;
    private WebSocketService WSService;
    private ChatMessageReceiver chatMessageReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        contact = intent.getStringExtra("CONTACT_NAME");


        mTopBar = findViewById(R.id.topbarChatPage);
        messageRecylerView = findViewById(R.id.recyclerChat);
        sendBtn = findViewById(R.id.btnSend);
        messageEdit = findViewById(R.id.editWriteMessage);

        initTestData();
        initTopBar();

        sendBtn.setOnClickListener(this);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        messageRecylerView = (RecyclerView)findViewById(R.id.recyclerChat);
        messageRecylerView.setLayoutManager(linearLayoutManager);
        adapter = new ListMessageAdapter(this, testlist);
        messageRecylerView.setAdapter(adapter);

        mContext = ChatActivity.this;
        startWebSocketService();
        bindService();
        doRegisterReceiver();



    }


    private void initTopBar() {
        mTopBar.setTitle(contact);
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> finish());
    }

    private void initTestData() {
        testlist = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            testlist.add(new messageTest("hello", i % 2));
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSend)
        {
            String msg = messageEdit.getText().toString().trim();
            if (msg.length() > 0)
            {
                messageEdit.setText("");
                JSONObject obj = new JSONObject();
                obj.put("content", msg);
                obj.put("to", "chatbot002");
                String sendMsg = obj.toJSONString();
                if (client != null && client.isOpen())
                    WSService.sendMsg(sendMsg);
                testlist.add(new messageTest(msg, 0));
                adapter.notifyDataSetChanged();
                messageRecylerView.smoothScrollToPosition(testlist.size());
            }
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("MainActivity", "服务与活动成功绑定");
            binder = (WebSocketService.JWebSocketClientBinder) iBinder;
            WSService = binder.getService();
            client = WSService.client;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("MainActivity", "服务与活动成功断开");
        }
    };

    private class ChatMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message=intent.getStringExtra("message");
            Log.e("ChatActivity", message);
        }
    }

    private void bindService() {
        Intent bindIntent = new Intent(mContext, WebSocketService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    private void startWebSocketService() {
        Intent intent = new Intent(mContext, WebSocketService.class);
        intent.putExtra("uid", "28");
        startService(intent);
    }

    private void doRegisterReceiver() {
        chatMessageReceiver = new ChatMessageReceiver();
        IntentFilter filter = new IntentFilter("com.xch.servicecallback.content");
        registerReceiver(chatMessageReceiver, filter);
    }

}






class ListMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<messageTest> list;

    public ListMessageAdapter(Context context, ArrayList<messageTest> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ChatActivity.VIEW_TYPE_USER_MSG)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_user, parent, false);
            return new UserMessageItemHolder(view);
        }
        else if (viewType == ChatActivity.VIEW_TYPE_FRIEND_MSG)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_friend, parent, false);
            return new FriendMessageItemHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserMessageItemHolder) {
            ((UserMessageItemHolder) holder).msg.setText(list.get(position).msg);
        }
        else if (holder instanceof FriendMessageItemHolder) {
            ((FriendMessageItemHolder) holder).msg.setText(list.get(position).msg);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).type == ChatActivity.VIEW_TYPE_USER_MSG ? ChatActivity.VIEW_TYPE_USER_MSG : ChatActivity.VIEW_TYPE_FRIEND_MSG;
    }
}



class UserMessageItemHolder extends RecyclerView.ViewHolder {

    public TextView msg;
    public CircleImageView avatar;

    public UserMessageItemHolder(@NonNull View itemView) {
        super(itemView);
        msg = (TextView) itemView.findViewById(R.id.msgContentUser);
        avatar = (CircleImageView) itemView.findViewById(R.id.userAvatar);

    }
}

class FriendMessageItemHolder extends RecyclerView.ViewHolder {
    public TextView msg;
    public CircleImageView avatar;

    public FriendMessageItemHolder(@NonNull View itemView) {
        super(itemView);
        msg = (TextView) itemView.findViewById(R.id.msgContentFriend);
        avatar = (CircleImageView) itemView.findViewById(R.id.friendAvatar);
    }
}
