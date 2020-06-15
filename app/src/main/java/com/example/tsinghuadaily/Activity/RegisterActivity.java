package com.example.tsinghuadaily.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.utils.OkHttpUtil;
import com.example.tsinghuadaily.utils.OkHttpUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    QMUITopBar topBar;
    // QMUIRoundButton getVerifyBtn;
    QMUIRoundButton registerBtn;
    EditText usernameEdit;
    EditText pwdEdit;
    EditText repeatPwdEdit;
    Handler handler;


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        topBar = (QMUITopBar) findViewById(R.id.topbar);
        registerBtn = (QMUIRoundButton) findViewById(R.id.button_register);
        usernameEdit = (EditText) findViewById(R.id.edittext_name);
        pwdEdit = (EditText)findViewById(R.id.edittext_password);
        repeatPwdEdit = (EditText)findViewById(R.id.edittext_password_ensure);
        registerBtn.setChangeAlphaWhenPress(true);
        registerBtn.setOnClickListener(this);
        initTopBar();
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                String val = data.getString("requestRes");
                JSONObject obj = JSONObject.parseObject(val);
                if (obj.get("code").equals(200))
                {
                    Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    String errorMsg = obj.get("msg").toString();
                    dialogShower(4, errorMsg);

                }
            }
        };


    }

    private void initTopBar() {
        topBar.addLeftBackImageButton().setOnClickListener(v -> finish());
        topBar.setTitle("注册账号");
    }

    private int editValidator() {
        // Return Value: 1:UserName Length, 2: Password Length, 3: Password not match, 0: normal
        String username = usernameEdit.getText().toString();
        String pwd = pwdEdit.getText().toString();
        String repeatPwd = repeatPwdEdit.getText().toString();
        if (username.length() < 3 || username.length() > 20)
        {
            return 1;
        }
        if (pwd.length() < 6 || pwd.length() > 20)
        {
            return 2;
        }
        if (!pwd.equals(repeatPwd))
        {
            return 3;
        }
        return 0;
    }

    private void dialogShower(int code, String msg) {
        switch (code) {
            case 1:
                new QMUIDialog.MessageDialogBuilder(this).setTitle("错误").setMessage("用户名长度需在3-20个字符之间！").addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                }).show();
                break;
            case 2:
                new QMUIDialog.MessageDialogBuilder(this).setTitle("错误").setMessage("密码长度需在6-20个字符之间！").addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                }).show();
                break;
            case 3:
                new QMUIDialog.MessageDialogBuilder(this).setTitle("错误").setMessage("两次输入的密码不一致！").addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                }).show();
                break;
            case 4:
                new QMUIDialog.MessageDialogBuilder(this).setTitle("错误").setMessage(msg).addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                }).show();
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_register) {
            //TODO: 发送注册请求，获取状态并用对话框提示。
            int code = editValidator();
            if(code != 0)
            {
                dialogShower(code, "");
                return;
            }
            Map<String, String> params = new HashMap<>();
            params.put("username", usernameEdit.getText().toString());
            params.put("password", pwdEdit.getText().toString());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String res = OkHttpUtil.postForm("http://175.24.61.249:8080/user/register", params);
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("requestRes", res);
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            }).start();
        }
    }
}
