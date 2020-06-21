package com.example.tsinghuadaily.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
import com.example.tsinghuadaily.ViewModel.ChatMessageForOneContactViewModel;
import com.example.tsinghuadaily.ViewModel.VMFactory;
import com.example.tsinghuadaily.models.ChatMessage;
import com.example.tsinghuadaily.models.messageTest;
import com.example.tsinghuadaily.services.WebSocketService;
import com.example.tsinghuadaily.utils.JWebSocketClient;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int VIEW_TYPE_USER_MSG = 1;
    public static final int VIEW_TYPE_FRIEND_MSG = 0;
    public static final int VIEW_TYPE_USER_ARTICLE_MSG = 2;
    public static final int VIEW_TYPE_FRIEND_ARTICLE_MSG = 3;


    private QMUITopBar mTopBar;
    private RecyclerView messageRecylerView;
    private ImageButton sendBtn;
    private EditText messageEdit;

    private ArrayList<messageTest> testlist;
    private List<ChatMessage> msgList;

    private ListMessageAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private String contact;
    private int contact_uid;
    private int UID;

    private Bitmap selfAvatar;
    private Bitmap contactAvatar;

    private boolean startFlag;

    private String shareMsg;
    private int mode;

    Handler handler;

    // WebSocket使用
    private Context mContext;
    private JWebSocketClient client;
    private WebSocketService.JWebSocketClientBinder binder;
    private WebSocketService WSService;
    //private ChatMessageReceiver chatMessageReceiver;


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        contact = intent.getStringExtra("CONTACT_NAME");
        contact_uid = intent.getIntExtra("CONTACT_UID", 0);
        mode = 0;
        if (intent.getIntExtra("MODE", 0) == 1)
        {
            mode = 1;
            shareMsg = intent.getStringExtra("SHARE_MSG");
        }
        UID = getSharedPreferences("userdata", MODE_PRIVATE).getInt("uid", 0);
        startFlag = true;
        msgList = new ArrayList<>();

        mTopBar = findViewById(R.id.topbarChatPage);
        messageRecylerView = findViewById(R.id.recyclerChat);
        sendBtn = findViewById(R.id.btnSend);
        messageEdit = findViewById(R.id.editWriteMessage);
        initBitmap();

        //initTestData();
        initTopBar();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                int code = data.getInt("code");
                switch(code) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        messageRecylerView.smoothScrollToPosition(msgList.size());
                        break;
                    case 2:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (mode == 1) {
                                    JSONObject obj = new JSONObject();
                                    obj.put("content", shareMsg);
                                    obj.put("to", contact_uid);
                                    String sendMsg = obj.toJSONString();
                                    if (client != null && client.isOpen())
                                        WSService.sendMsg(sendMsg);
                                }
                            }
                        }).start();
                    default:
                        break;
                }
            }
        };


        sendBtn.setOnClickListener(this);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        messageRecylerView = (RecyclerView)findViewById(R.id.recyclerChat);
        messageRecylerView.setLayoutManager(linearLayoutManager);
        adapter = new ListMessageAdapter(this, msgList);
        messageRecylerView.setAdapter(adapter);
        messageRecylerView.smoothScrollToPosition(msgList.size());

        mContext = ChatActivity.this;
        // startWebSocketService();
        bindService();
        //doRegisterReceiver();

        ChatMessageForOneContactViewModel chatMessageForOneContactViewModel = new ViewModelProvider(this, new VMFactory(getApplication(), contact_uid)).get(ChatMessageForOneContactViewModel.class);
        chatMessageForOneContactViewModel.getLiveDataChatMessage().observe(this, new Observer<List<ChatMessage>>() {
            @Override
            public void onChanged(List<ChatMessage> chatMessages) {
                msgList.clear();
                msgList.addAll(chatMessages);
                adapter.notifyDataSetChanged();
                if (startFlag)
                {
                    messageRecylerView.scrollToPosition(chatMessages.size() - 5);
                    startFlag = false;
                }
                else
                    messageRecylerView.smoothScrollToPosition(msgList.size());
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    private void initBitmap() {
        try {
            File f = new File(getFilesDir().getAbsolutePath() + "/Avatar/" + UID, "avatar.png");
            if (f.exists()) {
                FileInputStream fis = new FileInputStream(f);
                selfAvatar = BitmapFactory.decodeStream(fis);
            }
            else
                selfAvatar = BitmapFactory.decodeResource(getResources(), R.drawable.default_avata);
            byte[] bytes = getIntent().getByteArrayExtra("CONTACT_AVATAR");
            if (bytes == null) {
                contactAvatar = BitmapFactory.decodeResource(getResources(), R.drawable.default_avata);
            }
            else {
                contactAvatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTopBar() {
        mTopBar.setTitle(contact);
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //unbindService(serviceConnection);
                //unregisterReceiver(chatMessageReceiver);
                finish();
            }
        });
    }

//    private void initTestData() {
//        testlist = new ArrayList<>();
//        for (int i = 0; i < 10; i++)
//        {
//            testlist.add(new messageTest("hello", i % 2));
//        }
//
//    }



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
                obj.put("to", contact_uid);
                String sendMsg = obj.toJSONString();
                if (client != null && client.isOpen())
                    WSService.sendMsg(sendMsg);
//                testlist.add(new messageTest(msg, 0));
//                adapter.notifyDataSetChanged();
//                messageRecylerView.smoothScrollToPosition(testlist.size());
            }
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("ChatActivity", "服务与活动成功绑定");
            binder = (WebSocketService.JWebSocketClientBinder) iBinder;
            WSService = binder.getService();
            client = WSService.client;
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putInt("code", 2);
            msg.setData(data);
            handler.sendMessage(msg);
        }



        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("ChatActivity", "服务与活动成功断开");
        }
    };

//    private class ChatMessageReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String message=intent.getStringExtra("message");
//            Log.e("ChatActivity", message);
//        }
//    }
//
    private void bindService() {
        Intent bindIntent = new Intent(mContext, WebSocketService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }



//    private void doRegisterReceiver() {
//        chatMessageReceiver = new ChatMessageReceiver();
//        IntentFilter filter = new IntentFilter("com.xch.servicecallback.content");
//        registerReceiver(chatMessageReceiver, filter);
//    }




    class ListMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private List<ChatMessage> list;
        private DateFormat sdf;

        public ListMessageAdapter(Context context, List<ChatMessage> list) {
            this.context = context;
            this.list = list;
            this.sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
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
            else if (viewType == ChatActivity.VIEW_TYPE_USER_ARTICLE_MSG)
            {
                View view = LayoutInflater.from(context).inflate(R.layout.item_self_message_article, parent, false);
                return new UserArticleMessageItemHolder(view);
            }
            else
            {
                View view = LayoutInflater.from(context).inflate(R.layout.item_contact_message_article, parent, false);
                return new FriendArticleMessageItemHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Timestamp stmp = new Timestamp(list.get(position).time);
            if (holder instanceof UserMessageItemHolder) {
                ((UserMessageItemHolder) holder).avatar.setImageBitmap(selfAvatar);
                ((UserMessageItemHolder) holder).msg.setText(list.get(position).content);
                ((UserMessageItemHolder) holder).time.setText(sdf.format(stmp));
            }
            else if (holder instanceof FriendMessageItemHolder) {
                ((FriendMessageItemHolder) holder).avatar.setImageBitmap(contactAvatar);
                ((FriendMessageItemHolder) holder).msg.setText(list.get(position).content);
                ((FriendMessageItemHolder) holder).time.setText(sdf.format(stmp));
            }
            else if (holder instanceof UserArticleMessageItemHolder) {
                String json = list.get(position).content.substring(19);
                JSONObject obj = JSONObject.parseObject(json);
                ((UserArticleMessageItemHolder) holder).avatar.setImageBitmap(selfAvatar);
                ((UserArticleMessageItemHolder) holder).title.setText(obj.getString("a_title"));
            }
            else if (holder instanceof FriendArticleMessageItemHolder) {
                String json = list.get(position).content.substring(19);
                JSONObject obj = JSONObject.parseObject(json);
                ((FriendArticleMessageItemHolder) holder).avatar.setImageBitmap(contactAvatar);
                ((FriendArticleMessageItemHolder) holder).title.setText(obj.getString("a_title"));
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (list.get(position).isMeSend == ChatActivity.VIEW_TYPE_USER_MSG) {
                if (list.get(position).content.startsWith("ShareArticle0226:::"))
                    return ChatActivity.VIEW_TYPE_USER_ARTICLE_MSG;
                else
                    return ChatActivity.VIEW_TYPE_USER_MSG;
            }
            else {
                if (list.get(position).content.startsWith("ShareArticle0226:::"))
                    return ChatActivity.VIEW_TYPE_FRIEND_ARTICLE_MSG;
                else
                    return ChatActivity.VIEW_TYPE_FRIEND_MSG;
            }
        }
    }



    class UserMessageItemHolder extends RecyclerView.ViewHolder {

        public TextView msg;
        public CircleImageView avatar;
        public TextView time;

        public UserMessageItemHolder(@NonNull View itemView) {
            super(itemView);
            msg = (TextView) itemView.findViewById(R.id.msgContentUser);
            avatar = (CircleImageView) itemView.findViewById(R.id.userAvatar);
            time = (TextView) itemView.findViewById(R.id.textSendTime);

        }
    }

    class UserArticleMessageItemHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public CircleImageView avatar;

        public UserArticleMessageItemHolder(@NonNull View itemView) {
            super(itemView);
            avatar = (CircleImageView) itemView.findViewById(R.id.userAvatar);
            title = (TextView) itemView.findViewById(R.id.articleTitle);

        }
    }

    class FriendMessageItemHolder extends RecyclerView.ViewHolder {
        public TextView msg;
        public CircleImageView avatar;
        public TextView time;

        public FriendMessageItemHolder(@NonNull View itemView) {
            super(itemView);
            msg = (TextView) itemView.findViewById(R.id.msgContentFriend);
            avatar = (CircleImageView) itemView.findViewById(R.id.friendAvatar);
            time = (TextView) itemView.findViewById(R.id.textSendTime);
        }
    }

    class FriendArticleMessageItemHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public CircleImageView avatar;

        public FriendArticleMessageItemHolder(@NonNull View itemView) {
            super(itemView);
            avatar = (CircleImageView) itemView.findViewById(R.id.friendAvatar);
            title = (TextView) itemView.findViewById(R.id.articleTitle);

        }
    }



}


