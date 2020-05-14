package com.example.tsinghuadaily;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void onClickShowDefaultDialog(View view) {
        new AlertDialog.Builder(this)
                .setTitle("默认对话框标题")
                .setMessage("这是默认对话框的内容")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "点击了取消按钮", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "点击了确定按钮", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
    }


    public void onClickShowQMUIDialog(View view) {
        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle("QMUI对话框标题")
                .setMessage("这是QMUI框架对话框的内容")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "点击了取消按钮", Toast.LENGTH_SHORT).show();

                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "点击了确定按钮", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
}
