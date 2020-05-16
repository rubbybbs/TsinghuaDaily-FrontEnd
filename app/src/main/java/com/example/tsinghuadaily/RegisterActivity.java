package com.example.tsinghuadaily;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;

public class RegisterActivity extends AppCompatActivity {
    QMUITopBar topBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        topBar = (QMUITopBar) findViewById(R.id.topbar);

        //QMUIStatusBarHelper.translucent(this);

        initTopBar();

    }

    private void initTopBar() {
        topBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        topBar.setTitle("注册账号");
    }
}
