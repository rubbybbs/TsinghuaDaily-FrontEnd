package com.example.tsinghuadaily.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.models.UserConfiguration;
import com.example.tsinghuadaily.models.UserInfo;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfoFragment extends QMUIFragment {

    @BindView(R.id.img_avatar)
    ImageView avatar;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.topbarInfoPage)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.info_recyler_view)
    RecyclerView userInfoRecylerView;

    private UserInfoListAdapter adapter;

    private int UID;
    private String username;
    private boolean isVerified;

    private List<UserConfiguration> userInfoList;

    public UserInfoFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getContext(), "started by scheme", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected View onCreateView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user_info, null);
        ButterKnife.bind(this, rootView);

        SharedPreferences preferences = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
        UID = preferences.getInt("uid", 0);
        username = preferences.getString("username", "");
        isVerified = false;
        userInfoList = new ArrayList<>();
        initInfoList();

        initTopBar();
        tvUsername.setText(username);
        initRecylerView();

        return rootView;
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

    private void initRecylerView() {
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        userInfoRecylerView.setLayoutManager(llm);
        userInfoRecylerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new UserInfoListAdapter(getContext(), userInfoList, this);
        userInfoRecylerView.setAdapter(adapter);

    }

    private void initInfoList() {
        userInfoList.clear();
        if (!isVerified)
        {

            UserConfiguration auth = new UserConfiguration("账号认证", "", R.drawable.ic_baseline_account_circle_24);
            userInfoList.add(auth);
            UserConfiguration resetPwd = new UserConfiguration("修改个人信息", "", R.drawable.ic_baseline_settings_24);
            userInfoList.add(resetPwd);
            UserConfiguration logout = new UserConfiguration("退出登录", "", R.drawable.ic_baseline_settings_power_24);
            userInfoList.add(logout);

        }
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