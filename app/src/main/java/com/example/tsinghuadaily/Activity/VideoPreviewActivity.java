package com.example.tsinghuadaily.Activity;

import androidx.appcompat.app.AppCompatActivity;

import com.dou361.ijkplayer.widget.IjkVideoView;
import com.dou361.ijkplayer.widget.PlayStateParams;
import com.dou361.ijkplayer.widget.PlayerView;
import com.example.tsinghuadaily.R;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import org.jetbrains.annotations.NotNull;

public class VideoPreviewActivity extends AppCompatActivity {

    IjkVideoView mPlayerView;
    String mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = getLayoutInflater().from(this).inflate(R.layout.simple_player_view_player, null);
        setContentView(rootView);

        //mPlayerView =(IjkVideoView) findViewById(R.id.ijk_iv_rotation);

        mUri = getIntent().getStringExtra("url");
        new PlayerView(this)
                .setTitle("Video")
                .setScaleType(PlayStateParams.fitparent)
                .hideMenu(true)
                .forbidTouch(false)
                .setPlaySource(mUri) //里面写你播放视频的地址
                .startPlay();

    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mPlayerView.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mPlayerView.onPause();
//    }



}