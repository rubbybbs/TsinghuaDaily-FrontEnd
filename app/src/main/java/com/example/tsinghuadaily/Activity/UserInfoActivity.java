package com.example.tsinghuadaily.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.tsinghuadaily.Fragment.MessageFragment;
import com.example.tsinghuadaily.Fragment.UserInfoFragment;
import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.models.UserConfiguration;
import com.example.tsinghuadaily.utils.OkHttpUtil;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppCompatActivity {

    CircleImageView avatar;
    TextView tvUsername;
    QMUITopBarLayout mTopBar;
    RecyclerView userInfoRecylerView;
    TextView tvStatus;

    private String avatar_getter;  //可能为空
    private String username;
    private String status;
    private String uid;
    private String dept_name;
    private String id;
    private String type;

    Handler handler;
    byte[] tempAvatar;

    private List<UserConfiguration> userInfoList;

    private UserInfoListAdapter adapter;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        avatar = (CircleImageView)findViewById(R.id.img_avatar);
        tvUsername = (TextView)findViewById(R.id.tv_username);
        mTopBar = (QMUITopBarLayout)findViewById(R.id.topbarUInfoPage);
        userInfoRecylerView = (RecyclerView)findViewById(R.id.info_recyler_view);
        tvStatus = (TextView)findViewById(R.id.tv_status);


        avatar_getter = getIntent().getStringExtra("avatar_getter");
        username = getIntent().getStringExtra("username");
        status = getIntent().getStringExtra("status");
        if (StringUtils.isBlank(status))
            status = "未设置个性签名";
        uid = getIntent().getStringExtra("uid");
        dept_name = getIntent().getStringExtra("dept_name");
        id = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");

        tvUsername.setText(username);
        tvStatus.setText(status);


        userInfoList = new ArrayList<>();

        initInfoList();
        initAvatar();
        initTopBar();
        initRecylerView();

        if (!StringUtils.isBlank(avatar_getter))
            new GetAvatarTask().execute();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                int code = data.getInt("code");
                switch(code) {
                    case 0:
                        avatar.setImageBitmap(BitmapFactory.decodeByteArray(tempAvatar, 0, tempAvatar.length));
                        break;
                    default:
                        break;
                }
            }
        };


    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTopBar.setTitle("用户信息");
    }

    private void initAvatar() {
        avatar.setImageResource(R.drawable.default_avata);
    }

    private void initRecylerView() {
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        userInfoRecylerView.setLayoutManager(llm);
        userInfoRecylerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new UserInfoListAdapter(this, userInfoList);
        userInfoRecylerView.setAdapter(adapter);

    }

    private void initInfoList() {
        userInfoList.clear();

        UserConfiguration auth = new UserConfiguration("认证情况", "用户已认证", R.drawable.ic_baseline_account_circle_24);
        userInfoList.add(auth);
        UserConfiguration idC = new UserConfiguration("证件号", id, R.drawable.ic_baseline_credit_card_24);
        userInfoList.add(idC);
        UserConfiguration dept = new UserConfiguration("单位/院系", dept_name, R.drawable.ic_baseline_apartment_24);
        userInfoList.add(dept);
        UserConfiguration identity = new UserConfiguration("身份", type, R.drawable.ic_baseline_assignment_ind_24);
        userInfoList.add(identity);
        UserConfiguration chat = new UserConfiguration("发起聊天", "", R.drawable.ic_baseline_chat_24);
        userInfoList.add(chat);
    }


    class UserInfoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private List<UserConfiguration> userInfoList;

        public UserInfoListAdapter(Context context, List<UserConfiguration> userInfoList) {
            this.context = context;
            this.userInfoList = userInfoList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_user_info, parent, false);
            return new UserInfoViewHolder(context, view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            UserConfiguration config = userInfoList.get(position);
            ((UserInfoViewHolder) holder).icon.setBackgroundResource(config.getIcon());
            ((UserInfoViewHolder) holder).label.setText(config.getLabel());
            ((UserInfoViewHolder) holder).value.setText(config.getValue());
            ((UserInfoViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (config.getLabel().equals("发起聊天")) {
                        Intent intent = new Intent(context, ChatActivity.class);
                        //intent.putExtra("CONTACT_NAME", ((MessageDigestViewHolder) holder).txtName.getText().toString().trim());
                        intent.putExtra("CONTACT_NAME", username);
                        intent.putExtra("CONTACT_UID", Integer.valueOf(uid));
                        intent.putExtra("CONTACT_AVATAR", tempAvatar);
                        context.startActivity(intent);
                        finish();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return userInfoList.size();
        }
    }

    class UserInfoViewHolder extends RecyclerView.ViewHolder {
        public TextView label, value;
        public ImageView icon;
        private Context context;
        UserInfoViewHolder(Context context, View itemView) {
            super(itemView);
            label =  (TextView) itemView.findViewById(R.id.tv_title);
            value = (TextView) itemView.findViewById(R.id.tv_detail);
            icon = (ImageView) itemView.findViewById(R.id.img_icon);
        }
    }


    private class GetAvatarTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            String bUrl = "http://175.24.61.249:8080/media/get?";
            tempAvatar = OkHttpUtil.downloadMedia(bUrl + avatar_getter);
            if(tempAvatar != null && tempAvatar.length > 0) {
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putInt("code", 0);
                msg.setData(data);
                handler.sendMessage(msg);
            }
            return null;
        }
    }



}