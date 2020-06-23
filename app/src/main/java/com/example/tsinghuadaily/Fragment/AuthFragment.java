package com.example.tsinghuadaily.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.tsinghuadaily.Activity.ChatActivity;
import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.models.ChatMessage;
import com.example.tsinghuadaily.utils.OkHttpUtil;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
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


public class AuthFragment extends QMUIFragment {

    @BindView(R.id.authReqList)
    RecyclerView AuthRequestList;
    @BindView(R.id.pullRefresh)
    QMUIPullRefreshLayout PullRefreshLayout;
    @BindView(R.id.topbarAuthPage)
    QMUITopBarLayout mTopBar;

    List<AuthType> authList;

    AuthListAdapter adapter;

    Handler handler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(getContext(), "started by scheme", Toast.LENGTH_SHORT).show();
    }


    @SuppressLint("HandlerLeak")
    @Override
    protected View onCreateView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_auth, null);
        ButterKnife.bind(this, rootView);
        initTopBar();
        PullRefreshLayout.setEnabled(false);

        authList = new ArrayList<>();
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        AuthRequestList.setLayoutManager(llm);
        adapter = new AuthListAdapter(getContext());
        AuthRequestList.setAdapter(adapter);

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                int code = data.getInt("code");
                switch (code) {
                    case 0:
                        PullRefreshLayout.setToRefreshDirectly();
                        authList.clear();
                        break;
                    case 1:
                        PullRefreshLayout.finishRefresh();
                        adapter.notifyDataSetChanged();
                        PullRefreshLayout.finishRefresh();
                        break;
                    case 2:
                        String res = data.getString("requestRes", "");
                        JSONObject obj = JSONObject.parseObject(res);
                        if (obj == null || !obj.containsKey("code")) {
                            Toast.makeText(getContext(), "请求失败，请重试", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!obj.get("code").equals(200)) {
                            Toast.makeText(getContext(), obj.get("msg").toString(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        authList.remove(data.getInt("pos", 0));
                        Toast.makeText(getContext(), "接受请求成功", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        break;
                    case 3:
                        String r = data.getString("requestRes", "");
                        JSONObject o = JSONObject.parseObject(r);
                        if (o == null || !o.containsKey("code")) {
                            Toast.makeText(getContext(), "请求失败，请重试", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!o.get("code").equals(200)) {
                            Toast.makeText(getContext(), o.get("msg").toString(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        authList.remove(data.getInt("pos", 0));
                        Toast.makeText(getContext(), "拒绝请求成功", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
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
        new InitTask().execute();
    }

    private void initTopBar() {
//        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popBackStack();
//            }
//        });
        mTopBar.setTitle("认证列表");
    }

    private class InitTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putInt("code", 0);
            msg.setData(data);
            handler.sendMessage(msg);
            String url = "http://175.24.61.249:8080/auth-request/get-requests";
            String res = OkHttpUtil.get(url);
            JSONObject obj = JSONObject.parseObject(res);
            if (obj == null || !obj.containsKey("code")) {
                Looper.prepare();
                Toast.makeText(getContext(), "请求失败，请重试", Toast.LENGTH_SHORT).show();
                Looper.loop();
                return null;
            }
            if (!obj.get("code").equals(200)) {
                Looper.prepare();
                Toast.makeText(getContext(), obj.get("msg").toString(), Toast.LENGTH_SHORT).show();
                Looper.loop();
                return null;
            }
            JSONArray reqList = obj.getJSONArray("requests");
            for (int i = 0; i < reqList.size(); i++) {
                JSONObject o = reqList.getJSONObject(i);
                String username = o.get("username").toString();
                String id = o.get("id_num").toString();
                String deptname = o.get("dept_name").toString();
                String idcard_getter = o.get("id_card").toString();
                String identity;
                Bitmap bitmap;
                if (o.get("type").toString().equals("Staff")) {
                    identity = "教职工";
                }
                else
                    identity = "学生";
                if (StringUtils.isBlank(idcard_getter)) {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_avata);
                }
                String burl = "http://175.24.61.249:8080/media/get?";
                byte[] bytes = OkHttpUtil.downloadMedia(burl + idcard_getter);
                if (bytes == null || bytes.length == 0) {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_avata);
                }
                else
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                String req_id = o.get("request_id").toString();
                AuthType req = new AuthType(bitmap, username, id, deptname, identity, req_id);
                authList.add(req);
            }
            Message msg1 = new Message();
            Bundle data1 = new Bundle();
            data1.putInt("code", 1);
            msg1.setData(data1);
            handler.sendMessage(msg1);
            return null;
        }
    }



    private class AuthType {
        Bitmap idCard;
        String username;
        String id;
        String dept_name;
        String identity;
        String req_id;

        public AuthType(Bitmap idCard, String username, String id, String dept_name, String identity, String req_id) {
            this.idCard = idCard;
            this.username = username;
            this.id = id;
            this.dept_name = dept_name;
            this.identity = identity;
            this.req_id = req_id;
        }
    }


    class AuthListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;

        public AuthListAdapter(Context context) {
            this.context = context;

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_auth, parent, false);
            return new AuthViewHolder(context, view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            AuthType authReq = authList.get(position);
            ((AuthViewHolder)holder).idCard.setImageBitmap(authReq.idCard);
            ((AuthViewHolder)holder).txtID.setText(authReq.id);
            ((AuthViewHolder)holder).txtUsername.setText(authReq.username);
            ((AuthViewHolder)holder).txtDepartment.setText(authReq.dept_name);
            ((AuthViewHolder)holder).txtIdentity.setText(authReq.identity);
            ((AuthViewHolder)holder).acceptBtn.setChangeAlphaWhenPress(true);
            ((AuthViewHolder)holder).refuseBtn.setChangeAlphaWhenPress(true);
            ((AuthViewHolder)holder).idCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(getContext(), R.style.FullscreenTheme);
                    WindowManager.LayoutParams attributes = getActivity().getWindow().getAttributes();
                    attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
                    attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
                    dialog.getWindow().setAttributes(attributes);
                    ImageView imageView = new ImageView(getContext());
                    imageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    imageView.setImageBitmap(authReq.idCard);
                    dialog.setContentView(imageView);
                    dialog.show();
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });
            ((AuthViewHolder) holder).acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new QMUIDialog.MessageDialogBuilder(getContext())
                            .setTitle("操作确认")
                            .setMessage("确认通过该认证请求？")
                            .addAction("取消", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    dialog.dismiss();
                                }
                            })
                            .addAction("确认", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Map<String, String> params = new HashMap<>();
                                            params.put("request_id", authReq.req_id);
                                            String res = OkHttpUtil.postForm("http://175.24.61.249:8080/auth-request/approve", params);
                                            Message msg = new Message();
                                            Bundle data = new Bundle();
                                            data.putInt("code", 2);
                                            data.putString("requestRes", res);
                                            data.putInt("pos", position);
                                            msg.setData(data);
                                            handler.sendMessage(msg);
                                        }
                                    }).start();
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });
            ((AuthViewHolder) holder).refuseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new QMUIDialog.MessageDialogBuilder(getContext())
                            .setTitle("操作确认")
                            .setMessage("确认拒绝该认证请求？")
                            .addAction("取消", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    dialog.dismiss();
                                }
                            })
                            .addAction("确认", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Map<String, String> params = new HashMap<>();
                                            params.put("request_id", authReq.req_id);
                                            String res = OkHttpUtil.postForm("http://175.24.61.249:8080/auth-request/refuse", params);
                                            Message msg = new Message();
                                            Bundle data = new Bundle();
                                            data.putInt("code", 3);
                                            data.putString("requestRes", res);
                                            data.putInt("pos", position);
                                            msg.setData(data);
                                            handler.sendMessage(msg);
                                        }
                                    }).start();
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });


        }

        @Override
        public int getItemCount() {
            return authList.size();
        }
    }


    class AuthViewHolder extends RecyclerView.ViewHolder{
        public ImageView idCard;
        public TextView txtUsername, txtID, txtDepartment, txtIdentity;
        public QMUIRoundButton acceptBtn, refuseBtn;
        private Context context;

        AuthViewHolder(Context context, View itemView) {
            super(itemView);
            idCard = (ImageView)itemView.findViewById(R.id.icon_idCard);
            txtUsername = (TextView)itemView.findViewById(R.id.tv_username);
            txtID = (TextView)itemView.findViewById(R.id.tv_id);
            txtDepartment = (TextView)itemView.findViewById(R.id.tv_department);
            txtIdentity = (TextView)itemView.findViewById(R.id.tv_identity);
            acceptBtn = (QMUIRoundButton)itemView.findViewById(R.id.button_authAccept);
            refuseBtn = (QMUIRoundButton)itemView.findViewById(R.id.button_authRefuse);
            this.context = context;
        }
    }



}




