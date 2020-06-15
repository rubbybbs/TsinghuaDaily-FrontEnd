package com.example.tsinghuadaily.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.tsinghuadaily.R;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

public class LoginOrRegisterActivity extends AppCompatActivity implements View.OnClickListener {

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
        // 设置按钮监听函数
        LoginBtn.setOnClickListener(this);
        RegisterBtn.setOnClickListener(this);


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
            Intent intent = new Intent();
            intent.setClass(LoginOrRegisterActivity.this, MainPageActivity.class);
            startActivity(intent);
            finish();
        }
        else if (v.getId() == R.id.RegisterBtn) {
            Intent intent = new Intent();
            intent.setClass(LoginOrRegisterActivity.this, RegisterActivity.class);
            startActivityForResult(intent, 1);
        }
    }
}
