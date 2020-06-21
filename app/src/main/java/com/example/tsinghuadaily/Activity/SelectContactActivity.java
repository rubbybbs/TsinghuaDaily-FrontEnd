package com.example.tsinghuadaily.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.ColumnInfo;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.tsinghuadaily.Database.AppDatabase;
import com.example.tsinghuadaily.Fragment.AuthFragment;
import com.example.tsinghuadaily.Fragment.MessageFragment;
import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.utils.OkHttpUtil;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class SelectContactActivity extends AppCompatActivity {

    RecyclerView ContactRecylerList;
    QMUITopBarLayout mTopBar;

    ContactSelectAdapter adapter;

    private List<ContactInfo> contactList;

    Handler handler;
    AppDatabase db;
    private int UID;
    private String article_title;
    private String article_id;
    private static String SHARE_HEADER = "ShareArticle0226:::";

    Context mContext;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        article_id = getIntent().getStringExtra("article_id");
        article_title = getIntent().getStringExtra("article_title");
        ContactRecylerList = findViewById(R.id.recycleListContact);
        mTopBar = findViewById(R.id.topbarContactPage);
        mTopBar.setTitle("选择联系人");
        mContext = this;
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        UID = getSharedPreferences("userdata", MODE_PRIVATE).getInt("uid", 0);
        db = AppDatabase.getInstance(getApplicationContext(), UID);
        contactList = new ArrayList<>();
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ContactRecylerList.setLayoutManager(llm);
        adapter = new ContactSelectAdapter(this);
        ContactRecylerList.setAdapter(adapter);
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                int code = data.getInt("code");
                switch(code) {
                    case 0:
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        };
        new GetContactInfoTask().execute();
    }



    private class ContactInfo {
        int uid;
        String username;
        byte[] avatar;

        public ContactInfo(byte[] avatar, int uid, String username) {
            this.avatar = avatar;
            this.uid = uid;
            this.username = username;
        }
    }

    class ContactSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;

        public ContactSelectAdapter(Context context) {
            this.context = context;

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
            return new ContactViewHolder(context, view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ContactInfo contact = contactList.get(position);
            ((ContactViewHolder)holder).avatar.setImageBitmap(BitmapFactory.decodeByteArray(contact.avatar, 0, contact.avatar.length));
            ((ContactViewHolder)holder).txtUsername.setText(contact.username);
            ((ContactViewHolder) holder).llBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new QMUIDialog.MessageDialogBuilder(mContext)
                            .setTitle("分享文章")
                            .setMessage(article_title)
                            .addAction("取消", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    dialog.dismiss();
                                }
                            })
                            .addAction("确认", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(mContext, ChatActivity.class);
                                    //intent.putExtra("CONTACT_NAME", ((MessageDigestViewHolder) holder).txtName.getText().toString().trim());
                                    intent.putExtra("MODE", 1);
                                    intent.putExtra("CONTACT_NAME", contact.username);
                                    intent.putExtra("CONTACT_UID", contact.uid);
                                    intent.putExtra("CONTACT_AVATAR", contact.avatar);
                                    JSONObject obj = new JSONObject();
                                    obj.put("a_id", article_id);
                                    obj.put("a_title", article_title);
                                    String content = obj.toJSONString();
                                    intent.putExtra("SHARE_MSG", SHARE_HEADER + content);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }
    }


    class ContactViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView avatar;
        public TextView txtUsername;
        public LinearLayout llBtn;
        private Context context;

        ContactViewHolder(Context context, View itemView) {
            super(itemView);
            avatar = (CircleImageView) itemView.findViewById(R.id.icon_avata);
            txtUsername = (TextView)itemView.findViewById(R.id.txtName);
            llBtn = (LinearLayout)itemView.findViewById(R.id.llBtn);
            this.context = context;
        }
    }

    private class GetContactInfoTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            List<Integer> uids = db.chatMsgDao().getContact();
            for (int i = 0; i < uids.size(); i++) {
                String uid = String.valueOf(uids.get(i));
                String baseUrl = "http://175.24.61.249:8080/user/get-info?user_id=";
                String res = OkHttpUtil.get(baseUrl + uid);
                JSONObject obj = JSONObject.parseObject(res);
                String username = obj.getJSONObject("info").getString("username");
                byte[] bytes;
                if (obj.getJSONObject("info").containsKey("avatar")) {
                    String avatar_getter = obj.getJSONObject("info").get("avatar").toString();
                    String bUrl = "http://175.24.61.249:8080/media/get?";
                    bytes = OkHttpUtil.downloadMedia(bUrl + avatar_getter);
                    if (bytes == null || bytes.length == 0)
                    {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_avata);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
                        bytes = baos.toByteArray();
                    }
                }
                else
                {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_avata);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
                    bytes = baos.toByteArray();
                }
                contactList.add(new ContactInfo(bytes, Integer.parseInt(uid), username));
            }
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putInt("code", 0);
            msg.setData(data);
            handler.sendMessage(msg);
            return null;
        }
    }


}