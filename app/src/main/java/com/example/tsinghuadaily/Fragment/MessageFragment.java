package com.example.tsinghuadaily.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.tsinghuadaily.Activity.ChatActivity;
import com.example.tsinghuadaily.Activity.UserInfoActivity;
import com.example.tsinghuadaily.Database.AppDatabase;
import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.ViewModel.ChatDigestVMFactory;
import com.example.tsinghuadaily.ViewModel.ChatMesssageDigestViewModel;
import com.example.tsinghuadaily.models.ChatMessage;
import com.example.tsinghuadaily.models.UserInfo;
import com.example.tsinghuadaily.utils.OkHttpUtil;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private HashMap<Integer, byte[]> uidAvatarStreamMap;

    private MessageListAdapter adapter;

    AppDatabase db;
    Handler handler;

    int UID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(getContext(), "started by scheme", Toast.LENGTH_SHORT).show();

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
        uidAvatarStreamMap = new HashMap<Integer, byte[]>();
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
                    case 2:
                        String res = data.getString("requestRes");
                        JSONObject obj = JSONObject.parseObject(res);
                        if (obj == null || !obj.containsKey("code"))
                        {
                            Toast.makeText(getContext(), "请求失败，请重试", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!obj.get("code").equals(200))
                        {
                            Toast.makeText(getContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        JSONObject info = obj.getJSONObject("info");
                        String avatar_getter = "";  //可能为空
                        String username;
                        String status = "";  //可能为空
                        String uid;
                        String dept_name;
                        String id;
                        String type;
                        if (info.containsKey("avatar"))
                            avatar_getter = info.getString("avatar");
                        if (info.containsKey("status"))
                            status = info.getString("status");
                        dept_name = info.getString("dept_name");
                        username = info.getString("username");
                        uid = info.getString("user_id");
                        id = info.getString("id_num");
                        if (info.getString("type").equals("Staff"))
                            type = "教职工";
                        else
                            type = "学生";
                        Intent intent = new Intent(getContext(), UserInfoActivity.class);
                        intent.putExtra("avatar_getter", avatar_getter);
                        intent.putExtra("status", status);
                        intent.putExtra("dept_name", dept_name);
                        intent.putExtra("username", username);
                        intent.putExtra("uid", uid);
                        intent.putExtra("id", id);
                        intent.putExtra("type", type);
                        startActivity(intent);
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
                        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
                        builder.setTitle("按学号查找用户")
                                .setPlaceholder("请输入用户的学号")
                                .setInputType(InputType.TYPE_CLASS_NUMBER)
                                .addAction("取消", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        dialog.dismiss();
                                    }
                                })
                                .addAction("确定", new QMUIDialogAction.ActionListener() {

                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                String id = builder.getEditText().getText().toString();
                                new SearchInfoByIDTask(id).execute();
                                dialog.dismiss();
                            }
                        })
                                .show();
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
                if (!uidUsernameMap.containsKey(uid) || !uidAvatarStreamMap.containsKey(uid))
                {
                    String baseUrl = "http://175.24.61.249:8080/user/get-info?user_id=";
                    String res = OkHttpUtil.get(baseUrl + uid);
                    JSONObject obj = JSONObject.parseObject(res);
                    uidUsernameMap.put(uid, obj.getJSONObject("info").get("username").toString());
                    if (!uidAvatarStreamMap.containsKey(uid)) {
                        if (obj.getJSONObject("info").containsKey("avatar")) {
                            String avatar_getter = obj.getJSONObject("info").get("avatar").toString();
                            String bUrl = "http://175.24.61.249:8080/media/get?";
                            byte[] bytes = OkHttpUtil.downloadMedia(bUrl + avatar_getter);
                            uidAvatarStreamMap.put(uid, bytes);
                        }
                        else {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_avata);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
                            uidAvatarStreamMap.put(uid, baos.toByteArray());
                        }
                    }
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

    private class SearchInfoByIDTask extends AsyncTask<Void, Void, Void> {

        private String ID;
        public SearchInfoByIDTask(String ID) {
            this.ID = ID;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String baseurl = "http://175.24.61.249:8080/user/get-info?id_num=";
            String res = OkHttpUtil.get(baseurl + ID);
            Message msg2 = new Message();
            Bundle data2 = new Bundle();
            data2.putInt("code", 2);
            data2.putString("requestRes", res);
            msg2.setData(data2);
            handler.sendMessage(msg2);
            return null;
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
            byte[] abyte = uidAvatarStreamMap.get(msgD.uid);
            if (abyte == null)
                ((MessageDigestViewHolder) holder).avata.setImageResource(R.drawable.default_avata);
            else
                ((MessageDigestViewHolder) holder).avata.setImageBitmap(BitmapFactory.decodeByteArray(abyte, 0, abyte.length));
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
                    intent.putExtra("CONTACT_AVATAR", abyte);
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


}



