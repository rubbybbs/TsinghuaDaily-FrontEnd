package com.example.tsinghuadaily.Activity;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tsinghuadaily.R;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import org.jetbrains.annotations.NotNull;

public class VideoPreviewActivity extends AppCompatActivity implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{


    public static final String TAG = "MyVideoPlay";
    private VideoView mVideoView;
    private Uri mUri;
    private int mPositionWhenPaused = -1;

    private MediaController mMediaController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);

        mUri = Uri.parse(getIntent().getStringExtra("url"));

        //Create media controller
        mVideoView = findViewById(R.id.videoView);
        mMediaController = new MediaController(this);
        mVideoView.setMediaController(mMediaController);

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                VideoPreviewActivity.this.finish();
            }
        });
    }

    public void onStart() {
        // Play Video
        if (mVideoView != null && mUri != null) {
            mVideoView.setVideoURI(mUri);
            mVideoView.start();
        } else {
            Toast.makeText(VideoPreviewActivity.this, "发生错误", Toast.LENGTH_SHORT).show();
        }
        super.onStart();
    }

    public void onPause() {
        mPositionWhenPaused = mVideoView.getCurrentPosition();
        mVideoView.stopPlayback();
        super.onPause();
    }

    public void onResume() {
        // Resume video player
        if(mPositionWhenPaused >= 0) {
            mVideoView.seekTo(mPositionWhenPaused);
            mPositionWhenPaused = -1;
        }

        super.onResume();
    }

    public boolean onError(MediaPlayer player, int arg1, int arg2) {
        return false;
    }

    public void onCompletion(MediaPlayer mp) {
        VideoPreviewActivity.this.finish();
    }


}