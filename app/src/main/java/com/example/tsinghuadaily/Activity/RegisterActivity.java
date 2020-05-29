package com.example.tsinghuadaily.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.tsinghuadaily.R;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    QMUITopBar topBar;
    QMUIRoundButton getVerifyBtn;
    QMUIRoundButton registerBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        topBar = (QMUITopBar) findViewById(R.id.topbar);
        getVerifyBtn = (QMUIRoundButton) findViewById(R.id.button_getverifycode);
        registerBtn = (QMUIRoundButton) findViewById(R.id.button_register);
        getVerifyBtn.setChangeAlphaWhenPress(true);
        registerBtn.setChangeAlphaWhenPress(true);
        registerBtn.setOnClickListener(this);
        initTopBar();

    }

    private void initTopBar() {
        topBar.addLeftBackImageButton().setOnClickListener(v -> finish());
        topBar.setTitle("注册账号");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_getverifycode) {

        }
        else if (v.getId() == R.id.button_register) {
            //TODO: 发送注册请求，获取状态并用对话框提示。
            finish();
        }
    }
}
