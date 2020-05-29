package com.example.tsinghuadaily.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tsinghuadaily.Activity.ChatActivity;
import com.example.tsinghuadaily.Activity.MainPageActivity;
import com.example.tsinghuadaily.R;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;

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
    QMUITopBar mTopBar;

    private ArrayList<String> testData;

    private MessageListAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getContext(), "started by scheme", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected View onCreateView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_message, null);
        ButterKnife.bind(this, rootView);

        initTopBar();
        initTestData();
        initRecyleView();


        return rootView;
    }

    private void initTestData() {
        testData = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            testData.add("this is test message" + i);
    }

    private void initTopBar() {
        mTopBar.setTitle("消息列表");
    }


    private void initRecyleView() {


        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        MessageRecycleList.setLayoutManager(llm);
        adapter = new MessageListAdapter(getContext(), testData, this);
        MessageRecycleList.setAdapter(adapter);
    }
}


class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<String> testlist;
    private MessageFragment fragment;

    public MessageListAdapter(Context context, ArrayList<String> list, MessageFragment fragment) {
        this.context = context;
        this.fragment = fragment;
        this.testlist = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message_digest, parent, false);
        return new MessageDigestViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MessageDigestViewHolder) holder).avata.setImageResource(R.drawable.default_avata);
        ((MessageDigestViewHolder) holder).txtMessage.setText(testlist.get(position));
        ((MessageDigestViewHolder) holder).txtName.setText("Admin" + position);
        ((MessageDigestViewHolder) holder).txtTime.setText("00:00");
        ((MessageDigestViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("CONTACT_NAME", ((MessageDigestViewHolder) holder).txtName.getText().toString().trim());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return testlist.size();
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
