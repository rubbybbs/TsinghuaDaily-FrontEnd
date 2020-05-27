package com.example.tsinghuadaily.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.example.tsinghuadaily.R;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

public class LoginOrRegisterActivity extends AppCompatActivity {

    private QMUIRoundButton LoginBtn, RegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);

        // 设置按钮点击效果
        LoginBtn = (QMUIRoundButton)findViewById(R.id.LoginBtn);
        RegisterBtn = (QMUIRoundButton)findViewById(R.id.RegisterBtn);

        LoginBtn.setChangeAlphaWhenPress(true);
        RegisterBtn.setChangeAlphaWhenPress(true);
    }



}
