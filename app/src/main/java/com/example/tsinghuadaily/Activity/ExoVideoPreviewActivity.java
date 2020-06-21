package com.example.tsinghuadaily.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.utils.OkHttpUtil;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class ExoVideoPreviewActivity extends AppCompatActivity {

    private Button btn1x;
    private Button btn2x;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private DataSource source;
    private Uri mUri;

    byte[] tempVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_video_preview);
        btn1x = findViewById(R.id.btn_cut);
        btn2x = findViewById(R.id.btn_cut2);
        playerView = findViewById(R.id.play_view);

        mUri = Uri.parse(getIntent().getStringExtra("url"));
        btn1x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.setPlaybackParameters(new PlaybackParameters(1f));
            }
        });
        btn2x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.setPlaybackParameters(new PlaybackParameters(2f));
            }
        });
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        player.setPlayWhenReady(true);
        MediaSource mp4 = new ProgressiveMediaSource.Factory(new DefaultHttpDataSourceFactory("exoplayer-codelab")).createMediaSource(mUri);
        player.prepare(mp4, false, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }



}