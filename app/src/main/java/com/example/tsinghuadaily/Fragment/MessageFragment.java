package com.example.tsinghuadaily.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.tsinghuadaily.Activity.ChatActivity;
import com.example.tsinghuadaily.Activity.MainPageActivity;
import com.example.tsinghuadaily.Database.AppDatabase;
import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.ViewModel.ChatDigestVMFactory;
import com.example.tsinghuadaily.ViewModel.ChatMessageForOneContactViewModel;
import com.example.tsinghuadaily.ViewModel.ChatMesssageDigestViewModel;
import com.example.tsinghuadaily.ViewModel.VMFactory;
import com.example.tsinghuadaily.models.ChatMessage;
import com.example.tsinghuadaily.utils.OkHttpUtil;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends QMUIFragment {
    @BindView(R.id.recycleListMessage)
    RecyclerView MessageRecycleList;
    @BindView(R.id.pullRefresh)
    QMUIPullRefreshLayout PullRefreshLayout;
    @BindView(R.id.topbarMessagePage)
    QMUITopBarLayout mTopBar;

    private ArrayList<String> testData;

    private List<ChatMessage> msgDigest;
    private HashMap<Integer, String> uidUsernameMap;

    private MessageListAdapter adapter;

    AppDatabase db;
    Handler handler;

    int UID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getContext(), "started by scheme", Toast.LENGTH_SHORT).show();

    }

    @SuppressLint("HandlerLeak")
    @Override
    protected View onCreateView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_message, null);
        ButterKnife.bind(this, rootView);
        initTopBar();
        PullRefreshLayout.setEnabled(false);
        UID = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE).getInt("uid", 0);
        db = AppDatabase.getInstance(getActivity().getApplicationContext(), UID);
        msgDigest = new ArrayList<>();
        uidUsernameMap = new HashMap<>();
        initRecyleView();
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                int code = data.getInt("code");
                switch(code) {
                    case 0:
                        PullRefreshLayout.setToRefreshDirectly();
                        break;
                    case 1:
                        adapter.notifyDataSetChanged();
                        PullRefreshLayout.finishRefresh();
                        break;
                    default:
                        break;
                }
            }
        };
        ChatMesssageDigestViewModel chatMsgDigestModel = new ViewModelProvider(this, new ChatDigestVMFactory(getActivity().getApplication(), UID)).get(ChatMesssageDigestViewModel.class);
        chatMsgDigestModel.getLiveDataChatMsgDigest().observe(getViewLifecycleOwner(), new Observer<List<ChatMessage>>() {
            @Override
            public void onChanged(List<ChatMessage> chatMessages) {
                msgDigest.clear();
                msgDigest.addAll(chatMessages);
                PullRefreshLayout.setToRefreshDirectly();
                new InitTask().execute();
            }
        });
        return rootView;
    }


    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });

        mTopBar.setTitle("消息列表");
        mTopBar.addRightTextButton("搜索", QMUIViewHelper.generateViewId())
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
    }


    private void initRecyleView() {
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        MessageRecycleList.setLayoutManager(llm);
        adapter = new MessageListAdapter(getContext(), msgDigest, uidUsernameMap, this);
        MessageRecycleList.setAdapter(adapter);
    }

    private class InitTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < msgDigest.size(); i++)
            {
                int uid = msgDigest.get(i).uid;
                if (!uidUsernameMap.containsKey(uid))
                {
                    String baseUrl = "http://175.24.61.249:8080/user/get-info?user_id=";
                    String res = OkHttpUtil.get(baseUrl + uid);
                    JSONObject obj = JSONObject.parseObject(res);
                    uidUsernameMap.put(uid, obj.getJSONObject("info").get("username").toString());
                }
            }
            Message msg2 = new Message();
            Bundle data2 = new Bundle();
            data2.putInt("code", 1);
            msg2.setData(data2);
            handler.sendMessage(msg2);
            return null;
        }
    }
}


class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<ChatMessage> msgDigest;
    private HashMap<Integer, String> uidUsernameMap;
    private MessageFragment fragment;
    private DateFormat sdf;

    public MessageListAdapter(Context context, List<ChatMessage> list, HashMap<Integer, String> uidUsernameMap, MessageFragment fragment) {
        this.context = context;
        this.fragment = fragment;
        this.msgDigest = list;
        this.uidUsernameMap = uidUsernameMap;
        this.sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message_digest, parent, false);
        return new MessageDigestViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage msgD = msgDigest.get(position);
        Timestamp stmp = new Timestamp(msgD.time);
        ((MessageDigestViewHolder) holder).avata.setImageResource(R.drawable.default_avata);
        ((MessageDigestViewHolder) holder).txtMessage.setText(msgD.content);
        ((MessageDigestViewHolder) holder).txtName.setText(uidUsernameMap.get(msgD.uid));
        ((MessageDigestViewHolder) holder).txtTime.setText(sdf.format(stmp));
        ((MessageDigestViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                //intent.putExtra("CONTACT_NAME", ((MessageDigestViewHolder) holder).txtName.getText().toString().trim());
                intent.putExtra("CONTACT_NAME", ((MessageDigestViewHolder) holder).txtName.getText().toString().trim());
                intent.putExtra("CONTACT_UID", msgDigest.get(position).uid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return msgDigest.size();
    }
}


class MessageDigestViewHolder extends RecyclerView.ViewHolder{
    public CircleImageView avata;
    public TextView txtName, txtTime, txtMessage;
    private Context context;

    MessageDigestViewHolder(Context context, View itemView) {
        super(itemView);
        avata = (CircleImageView) itemView.findViewById(R.id.icon_avata);
        txtName = (TextView) itemView.findViewById(R.id.txtName);
        txtTime = (TextView) itemView.findViewById(R.id.txtTime);
        txtMessage = (TextView) itemView.findViewById(R.id.txtMessage);
        this.context = context;
    }
}
