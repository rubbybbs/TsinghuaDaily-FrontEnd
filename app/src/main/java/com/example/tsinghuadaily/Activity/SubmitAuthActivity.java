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
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.models.ChatMessage;
import com.example.tsinghuadaily.utils.OkHttpUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubmitAuthActivity extends AppCompatActivity implements View.OnClickListener {

    QMUITopBar topBar;
    QMUIRoundButton uploadImageBtn;
    QMUIRoundButton submitBtn;
    ImageView idCardPreview;
    EditText et_ID;
    RadioButton studentBtn, teacherBtn;
    Spinner spinner_department;

    ArrayAdapter<String> adpter;

    private Handler handler;

    private static final int PICK_IDCARD = 227;
    public static final int EXTERNAL_STORAGE_REQ_CODE = 10 ;

    private int UID;

    private byte[] tempIDCard;
    private String idCardGetter;
    private List<String> secNameList;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_auth);
        UID = getSharedPreferences("userdata", MODE_PRIVATE).getInt("uid", 0);
        topBar = (QMUITopBar)findViewById(R.id.topbar);
        uploadImageBtn = (QMUIRoundButton)findViewById(R.id.button_uploadIDCard);
        submitBtn = (QMUIRoundButton)findViewById(R.id.button_submit_auth);
        idCardPreview = (ImageView) findViewById(R.id.idCard);
        spinner_department = (Spinner)findViewById(R.id.spinner_department);
        et_ID = (EditText)findViewById(R.id.edittext_ID);
        studentBtn = (RadioButton)findViewById(R.id.studentRB);
        teacherBtn = (RadioButton)findViewById(R.id.teacherRB);
        uploadImageBtn.setChangeAlphaWhenPress(true);
        uploadImageBtn.setOnClickListener(this);
        submitBtn.setChangeAlphaWhenPress(true);
        submitBtn.setOnClickListener(this);
        initTopBar();

        secNameList = new ArrayList<>();
        idCardGetter = "";

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
                        idCardGetter = obj.get("msg").toString();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String baseUrl = "http://175.24.61.249:8080/media/get?";
                                byte[] bytes = OkHttpUtil.downloadMedia(baseUrl + idCardGetter);
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
                        tempIDCard = data.getByteArray("requestRes");
                        idCardPreview.setImageBitmap(BitmapFactory.decodeByteArray(tempIDCard, 0, tempIDCard.length));
                        break;
                    case 2:
                        String requestRes = data.getString("requestRes");
                        JSONObject o = JSONObject.parseObject(requestRes);
                        if (o == null || !o.containsKey("code") || !o.get("code").toString().equals("200")) {
                            Toast.makeText(getApplicationContext(), "提交身份认证失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //跳转回个人信息页面。
                            finish();
                        }
                        break;
                    case 3:
                        String sectionInfo = data.getString("requestRes");
                        JSONObject si = JSONObject.parseObject(sectionInfo);
                        JSONArray secs = si.getJSONArray("sections");
                        for (int i = 0; i < secs.size(); i++) {
                            secNameList.add(secs.getJSONObject(i).get("section_name").toString());
                        }
                        adpter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, secNameList);
                        spinner_department.setAdapter(adpter);
                        spinner_department.setSelection(0);

                    default:
                        break;
                }
                return;
            }

        };

        new GetSecListTask().execute();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_uploadIDCard) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Avatar"), PICK_IDCARD);

        }
        else if (v.getId() == R.id.button_submit_auth) {

            if (StringUtils.isBlank(et_ID.getText().toString())) {
                Toast.makeText(this, "请填写证件号", Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String, String> params = new HashMap<>();
            params.put("id_num", et_ID.getText().toString());
            params.put("dept_name", spinner_department.getSelectedItem().toString());
            if (studentBtn.isChecked())
                params.put("user_type", "Undergraduate");
            else
                params.put("user_type", "Staff");
            if (StringUtils.isBlank(idCardGetter)){
                Toast.makeText(this, "请上传证件图片", Toast.LENGTH_SHORT).show();
                return;
            }
            params.put("id_card", idCardGetter);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String res = OkHttpUtil.postForm("http://175.24.61.249:8080/auth-request/request", params);
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
        topBar.setTitle("提交身份认证");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IDCARD && resultCode == Activity.RESULT_OK)
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
                        String res = OkHttpUtil.uploadFile(avatarFile, "idCard.png");
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

    private class GetSecListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String baseurl = "http://175.24.61.249:8080/section/get-sections";
            String res = OkHttpUtil.get(baseurl);
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putInt("eventType", 3);
            data.putString("requestRes", res);
            msg.setData(data);
            handler.sendMessage(msg);
            return null;
        }
    }


}