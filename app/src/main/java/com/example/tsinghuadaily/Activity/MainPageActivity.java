package com.example.tsinghuadaily.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.tsinghuadaily.Fragment.MainPageFragment;
import com.example.tsinghuadaily.R;


public class MainPageActivity extends AppCompatActivity
    implements View.OnClickListener
{
    ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        MainPageFragment fragment = new MainPageFragment();
        transaction.add(R.id.showArea, fragment);
        transaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {

    }
}
