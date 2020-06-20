package com.example.tsinghuadaily.Activity;

import androidx.appcompat.app.AppCompatActivity;

import com.chinalwb.are.Util;
import com.chinalwb.are.render.AreTextView;
import com.chinalwb.are.spans.AreAtSpan;
import com.chinalwb.are.spans.AreImageSpan;
import com.chinalwb.are.spans.AreVideoSpan;
import com.chinalwb.are.strategies.AreClickStrategy;
import com.chinalwb.are.strategies.defaults.DefaultImagePreviewActivity;
import com.chinalwb.are.strategies.defaults.DefaultProfileActivity;
import com.example.tsinghuadaily.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.URLSpan;
import android.widget.Toast;

class DClickStrategy implements AreClickStrategy {
    @Override
    public boolean onClickAt(Context context, AreAtSpan atSpan) {
        Intent intent = new Intent();
        intent.setClass(context, DefaultProfileActivity.class);
        intent.putExtra("userKey", atSpan.getUserKey());
        intent.putExtra("userName", atSpan.getUserName());
        context.startActivity(intent);
        return true;
    }

    @Override
    public boolean onClickImage(Context context, AreImageSpan imageSpan) {
        Intent intent = new Intent();
        AreImageSpan.ImageType imageType = imageSpan.getImageType();
        intent.putExtra("imageType", imageType);
        if (imageType == AreImageSpan.ImageType.URI) {
            intent.putExtra("uri", imageSpan.getUri());
        } else if (imageType == AreImageSpan.ImageType.URL) {
            intent.putExtra("url", imageSpan.getURL());
        } else {
            intent.putExtra("resId", imageSpan.getResId());
        }
        intent.setClass(context, DefaultImagePreviewActivity.class);
        context.startActivity(intent);
        return true;
    }

    @Override
    public boolean onClickVideo(Context context, AreVideoSpan videoSpan) {
        Intent intent = new Intent();
        AreVideoSpan.VideoType videoType = videoSpan.getVideoType();
        intent.putExtra("imageType", videoType);
        if (videoType == AreVideoSpan.VideoType.LOCAL) {
            intent.putExtra("path", videoSpan.getVideoPath());
            Toast.makeText(context, "暂不支持本地视频", Toast.LENGTH_SHORT);
        } else if (videoType == AreVideoSpan.VideoType.SERVER) {
            intent.putExtra("url", videoSpan.getVideoUrl());
            intent.setClass(context, VideoPreviewActivity.class);
            context.startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean onClickUrl(Context context, URLSpan urlSpan) {
        // Use default behavior
        return false;
    }
}

public class ArticleDetailActivity extends AppCompatActivity {
    public static final String HTML_TEXT = "html_text";

    private AreClickStrategy mClickStrategy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        AreTextView areTextView = findViewById(R.id.areTextView);

        mClickStrategy = new DClickStrategy();
        areTextView.setClickStrategy(mClickStrategy);

        String s = getIntent().getStringExtra(HTML_TEXT);
        areTextView.fromHtml(s);

    }
}