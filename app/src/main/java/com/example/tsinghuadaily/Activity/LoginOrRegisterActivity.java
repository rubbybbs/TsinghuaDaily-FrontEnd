package com.example.tsinghuadaily.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.utils.OkHttpUtil;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class LoginOrRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private QMUIRoundButton LoginBtn, RegisterBtn;
    private MaterialEditText idEdit, pwdEdit;
    private String username, pwd;
    Handler handler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);

        // 设置按钮点击效果
        LoginBtn = (QMUIRoundButton)findViewById(R.id.LoginBtn);
        RegisterBtn = (QMUIRoundButton)findViewById(R.id.RegisterBtn);
        idEdit = (MaterialEditText)findViewById(R.id.idEdit);
        pwdEdit = (MaterialEditText)findViewById(R.id.passwordEdit);
        // TODO: 移除测试账号！！
        idEdit.setText("chatbot001");
        pwdEdit.setText("123456");
        LoginBtn.setChangeAlphaWhenPress(true);
        RegisterBtn.setChangeAlphaWhenPress(true);
        // 设置按钮监听函数
        LoginBtn.setOnClickListener(this);
        RegisterBtn.setOnClickListener(this);
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                String val = data.getString("requestRes");
                JSONObject obj = JSONObject.parseObject(val);
                if (obj.get("code").equals(200))
                {
                    int UID = Integer.valueOf(obj.getJSONObject("info").get("user_id").toString());
                    boolean authority = Boolean.valueOf(obj.getJSONObject("info").get("admin").toString());
                    SharedPreferences.Editor editor = getSharedPreferences("userdata",  MODE_PRIVATE).edit();
                    editor.putString("username", username);
                    editor.putInt("uid", UID);
                    editor.putBoolean("authority", authority);
                    editor.apply();
                    Intent intent = new Intent();
                    intent.setClass(LoginOrRegisterActivity.this, MainPageActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    String errorMsg = obj.get("msg").toString();
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        };


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == 1)
                    Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.LoginBtn) {
            String username = idEdit.getText().toString();
            String password = pwdEdit.getText().toString();
            int res = editValidator(username, password);
            if (res == 1)
                Toast.makeText(this, "用户名长度必须在3-20位之间", Toast.LENGTH_SHORT).show();
            else if (res == 2)
                Toast.makeText(this, "密码长度必须在6-20位之间", Toast.LENGTH_SHORT).show();
            else {
                this.username = username;
                this.pwd = password;
                new LoginTask().execute();
            }


        }
        else if (v.getId() == R.id.RegisterBtn) {
            Intent intent = new Intent();
            intent.setClass(LoginOrRegisterActivity.this, RegisterActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    private int editValidator(String username, String pwd) {
        // Return Value: 1:UserName Length, 2: Password Length, 3: Password not match, 0: normal
        if (username.length() < 2 || username.length() > 20)
        {
            return 1;
        }
        if (pwd.length() < 2 || pwd.length() > 20)
        {
            return 2;
        }
        return 0;
    }


    private class LoginTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Map<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("password", pwd);
            String res = OkHttpUtil.postForm("http://175.24.61.249:8080/user/login", params);
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("requestRes", res);
            msg.setData(data);
            handler.sendMessage(msg);
            return null;
        }
    }
}
