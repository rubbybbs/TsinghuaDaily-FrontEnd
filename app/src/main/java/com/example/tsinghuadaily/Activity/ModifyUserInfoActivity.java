package com.example.tsinghuadaily.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.utils.ImageUtils;
import com.example.tsinghuadaily.utils.OkHttpUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ModifyUserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    QMUITopBar topBar;
    QMUIRoundButton uploadImageBtn;
    QMUIRoundButton submitBtn;
    CircleImageView avatarPreview;
    EditText status;

    private Handler handler;

    private static final int PICK_AVATAR = 226;
    public static final int EXTERNAL_STORAGE_REQ_CODE = 10 ;

    private byte[] tempAvatar;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user_info);
        topBar = (QMUITopBar)findViewById(R.id.topbar);
        uploadImageBtn = (QMUIRoundButton)findViewById(R.id.button_uploadAvatar);
        submitBtn = (QMUIRoundButton)findViewById(R.id.button_submit_modification);
        avatarPreview = (CircleImageView)findViewById(R.id.avatarPreview);
        status = (EditText)findViewById(R.id.edittext_status);
        uploadImageBtn.setChangeAlphaWhenPress(true);
        uploadImageBtn.setOnClickListener(this);
        submitBtn.setChangeAlphaWhenPress(true);
        submitBtn.setOnClickListener(this);
        initTopBar();
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_REQ_CODE);
        }
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                int eventType = data.getInt("eventType", 0);
                switch (eventType) {
                    case 0:
                        String val = data.getString("requestRes");
                        JSONObject obj = JSONObject.parseObject(val);
                        String avatar_getter = obj.get("msg").toString();
                        SharedPreferences.Editor editor = getSharedPreferences("userdata", MODE_PRIVATE).edit();
                        editor.putString("avatar_getter", avatar_getter);
                        editor.apply();
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
                        break;
                    case 1:
                        tempAvatar = data.getByteArray("requestRes");
                        avatarPreview.setImageBitmap(BitmapFactory.decodeByteArray(tempAvatar, 0, tempAvatar.length));
                        break;
                    case 2:
                        String requestRes = data.getString("requestRes");
                        JSONObject o = JSONObject.parseObject(requestRes);
                        if (o == null || !o.containsKey("code") || !o.get("code").toString().equals("200")) {
                            Toast.makeText(getApplicationContext(), "修改信息失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //TODO: 持久化修改后的头像，跳转回个人信息页面。
                            Log.e("ModifyUserInfoActivity", requestRes);
                        }
                        break;
                    default:
                        break;
                }
                return;
            }
        };

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_uploadAvatar) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Avatar"), PICK_AVATAR);

        }
        else if (v.getId() == R.id.button_submit_modification) {
            Map<String, String> params = new HashMap<>();
            if (!StringUtils.isBlank(status.getText().toString())) {
                params.put("status", status.getText().toString());
            }
            SharedPreferences preferences = getSharedPreferences("userdata", MODE_PRIVATE);
            String avatar_getter = preferences.getString("avatar_getter", "");
            params.put("avatar", avatar_getter);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String res = OkHttpUtil.postForm("http://175.24.61.249:8080/user/modify-info", params);
                    Message msg1 = new Message();
                    Bundle data1 = new Bundle();
                    data1.putString("requestRes", res);
                    data1.putInt("eventType", 2);
                    msg1.setData(data1);
                    handler.sendMessage(msg1);
                }
            }).start();
        }
    }

    private void initTopBar() {
        topBar.addLeftBackImageButton().setOnClickListener(v -> finish());
        topBar.setTitle("修改个人信息");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AVATAR && resultCode == Activity.RESULT_OK)
        {
            if (data == null) {
                Toast.makeText(this, "Failed to get picture", Toast.LENGTH_SHORT).show();
                return;
            }
            Uri uri = data.getData();
            ContentResolver resolver = this.getContentResolver();
            Cursor cursor = resolver.query(uri, null, null, null, null);

            if (cursor == null) {
                // 未查询到，说明为普通文件，可直接通过URI获取文件路径
                String path = uri.getPath();
                Toast.makeText(this, "Failed to get picture", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cursor.moveToFirst()) {
                // 多媒体文件，从数据库中获取文件的真实路径
                String path = cursor.getString(cursor.getColumnIndex("_data"));
                File avatarFile = new File(path);
                boolean i = avatarFile.isFile();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String res = OkHttpUtil.uploadFile(avatarFile);
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString("requestRes", res);
                        data.putInt("eventType", 0);
                        msg.setData(data);
                        handler.sendMessage(msg);
                    }
                }).start();

            }
            cursor.close();

        }
    }


}


