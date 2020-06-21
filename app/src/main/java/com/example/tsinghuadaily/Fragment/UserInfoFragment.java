package com.example.tsinghuadaily.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.tsinghuadaily.Activity.LoginOrRegisterActivity;
import com.example.tsinghuadaily.Activity.MainPageActivity;
import com.example.tsinghuadaily.Activity.ModifyUserInfoActivity;
import com.example.tsinghuadaily.Activity.RegisterActivity;
import com.example.tsinghuadaily.Activity.SubmitAuthActivity;
import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.models.UserConfiguration;
import com.example.tsinghuadaily.models.UserInfo;
import com.example.tsinghuadaily.utils.OkHttpUtil;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
public class UserInfoFragment extends QMUIFragment {

    @BindView(R.id.img_avatar)
    CircleImageView avatar;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.topbarInfoPage)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.info_recyler_view)
    RecyclerView userInfoRecylerView;
    @BindView(R.id.tv_status)
    TextView tvStatus;

    private UserInfoListAdapter adapter;

    private int UID;
    private String username;
    private int isVerified;
    private String status;
    private String id;
    private String dept_name;
    private String type;

    Handler handler;

    private List<UserConfiguration> userInfoList;

    public UserInfoFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(getContext(), "started by scheme", Toast.LENGTH_SHORT).show();
    }


    @SuppressLint("HandlerLeak")
    @Override
    protected View onCreateView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user_info, null);
        ButterKnife.bind(this, rootView);

        SharedPreferences preferences = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
        UID = preferences.getInt("uid", 0);
        username = preferences.getString("username", "");
        isVerified = -1;
        status = "";
        id = "";
        dept_name = "";
        type = "";
        userInfoList = new ArrayList<>();
        initInfoList();
        initAvatar();
        initTopBar();
        tvUsername.setText(username);
        initRecylerView();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                int eventType = data.getInt("eventType", -1);
                switch (eventType) {
                    case 0:
                        String res = data.getString("requestRes");
                        JSONObject obj = JSONObject.parseObject(res);
                        if (obj.containsKey("code") && obj.get("code").toString().equals("200"))
                        {
                            JSONObject info = obj.getJSONObject("info");
                            if (info.containsKey("avatar")) {
                                String avatar_getter = info.get("avatar").toString();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String baseUrl = "http://175.24.61.249:8080/media/get?";
                                        byte[] bytes = OkHttpUtil.downloadMedia(baseUrl + avatar_getter);
                                        Message msg1 = new Message();
                                        Bundle data1 = new Bundle();
                                        data1.putByteArray("requestRes", bytes);
                                        data1.putInt("eventType", 1);
                                        msg1.setData(data1);
                                        handler.sendMessage(msg1);
                                    }
                                }).start();
                            }
                            if (info.containsKey("status")) {
                                status = info.get("status").toString();
                                tvStatus.setText(status);
                            }
                            else
                                tvStatus.setText("未设置个性签名");
                            isVerified = (int)info.get("verified");
                            if (info.containsKey("id_num"))
                                id = info.getString("id_num");
                            if (info.containsKey("dept_name"))
                                dept_name = info.getString("dept_name");
                            if (info.containsKey("type")) {
                                if (info.getString("type").equals("Staff"))
                                    type = "教职工";
                                else
                                    type = "学生";
                            }
                            initInfoList();
                            adapter.notifyDataSetChanged();
                        }
                        else
                            Toast.makeText(getContext(), "请求失败，请重启APP", Toast.LENGTH_SHORT);
                        break;
                    case 1:
                        byte[] tempAvatar = data.getByteArray("requestRes");
                        Bitmap bitmap = BitmapFactory.decodeByteArray(tempAvatar, 0, tempAvatar.length);
                        avatar.setImageBitmap(bitmap);
                        // 持久化头像至内部存储
                        File f = new File(getActivity().getFilesDir().getAbsolutePath() + "/Avatar/" + UID, "avatar.png");
                        while (f.exists())
                            f.delete();
                        try {
                            FileOutputStream out = new FileOutputStream(f);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                            out.flush();
                            out.close();
                         } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }

            }
        };

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        new GetInfoTask().execute();
    }

    private class GetInfoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String baseurl = "http://175.24.61.249:8080/user/get-info?user_id=";
            String res = OkHttpUtil.get(baseurl + UID);
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putInt("eventType", 0);
            data.putString("requestRes", res);
            msg.setData(data);
            handler.sendMessage(msg);
            return null;
        }
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });

        mTopBar.setTitle("个人信息");
    }

    private void initAvatar() {
        try {
            File f = new File(getActivity().getFilesDir().getAbsolutePath() + "/Avatar/" + UID, "avatar.png");
            if (f.exists()) {
                FileInputStream fis = new FileInputStream(f);
                avatar.setImageBitmap(BitmapFactory.decodeStream(fis));
            }
            else
                avatar.setImageResource(R.drawable.default_avata);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initRecylerView() {
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        userInfoRecylerView.setLayoutManager(llm);
        userInfoRecylerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new UserInfoListAdapter(getContext(), userInfoList, this);
        userInfoRecylerView.setAdapter(adapter);

    }

    private void initInfoList() {
        userInfoList.clear();
        if (isVerified == 0)
        {

            UserConfiguration auth = new UserConfiguration("账号认证", "点击进行账号认证", R.drawable.ic_baseline_account_circle_24);
            userInfoList.add(auth);
            UserConfiguration resetPwd = new UserConfiguration("修改个人信息", "", R.drawable.ic_baseline_settings_24);
            userInfoList.add(resetPwd);
            UserConfiguration logout = new UserConfiguration("退出登录", "", R.drawable.ic_baseline_settings_power_24);
            userInfoList.add(logout);

        }
        else if (isVerified == 1) {
            UserConfiguration auth = new UserConfiguration("账号认证", "请等待管理员审核认证", R.drawable.ic_baseline_account_circle_24);
            userInfoList.add(auth);
            UserConfiguration resetPwd = new UserConfiguration("修改个人信息", "", R.drawable.ic_baseline_settings_24);
            userInfoList.add(resetPwd);
            UserConfiguration logout = new UserConfiguration("退出登录", "", R.drawable.ic_baseline_settings_power_24);
            userInfoList.add(logout);
        }
        else if (isVerified == 2) {
            UserConfiguration auth = new UserConfiguration("账号认证", "用户已认证", R.drawable.ic_baseline_account_circle_24);
            userInfoList.add(auth);
            UserConfiguration idC = new UserConfiguration("证件号", id, R.drawable.ic_baseline_credit_card_24);
            userInfoList.add(idC);
            UserConfiguration dept = new UserConfiguration("单位/院系", dept_name, R.drawable.ic_baseline_apartment_24);
            userInfoList.add(dept);
            UserConfiguration identity = new UserConfiguration("身份", type, R.drawable.ic_baseline_assignment_ind_24);
            userInfoList.add(identity);
            UserConfiguration resetPwd = new UserConfiguration("修改个人信息", "", R.drawable.ic_baseline_settings_24);
            userInfoList.add(resetPwd);
            UserConfiguration logout = new UserConfiguration("退出登录", "", R.drawable.ic_baseline_settings_power_24);
            userInfoList.add(logout);
        }
        else if (isVerified == 3) {
            UserConfiguration auth = new UserConfiguration("账号认证", "认证被拒绝！请重新申请认证", R.drawable.ic_baseline_account_circle_24);
            userInfoList.add(auth);
            UserConfiguration resetPwd = new UserConfiguration("修改个人信息", "", R.drawable.ic_baseline_settings_24);
            userInfoList.add(resetPwd);
            UserConfiguration logout = new UserConfiguration("退出登录", "", R.drawable.ic_baseline_settings_power_24);
            userInfoList.add(logout);
        }
    }


    class UserInfoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private List<UserConfiguration> userInfoList;
        private UserInfoFragment fragment;

        public UserInfoListAdapter(Context context, List<UserConfiguration> userInfoList, UserInfoFragment fragment) {
            this.context = context;
            this.fragment = fragment;
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
                    if (config.getLabel().equals("修改个人信息")) {
                        Intent intent = new Intent();
                        intent.setClass(context, ModifyUserInfoActivity.class);
                        context.startActivity(intent);
                    }
                    else if (config.getLabel().equals("账号认证")) {
                        if (isVerified == 0 || isVerified == 3)
                        {
                            Intent intent = new Intent();
                            intent.setClass(context, SubmitAuthActivity.class);
                            context.startActivity(intent);
                        }
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

}

