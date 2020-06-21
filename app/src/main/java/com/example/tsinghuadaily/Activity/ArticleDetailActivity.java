package com.example.tsinghuadaily.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinalwb.are.Util;
import com.chinalwb.are.render.AreTextView;
import com.chinalwb.are.spans.AreAtSpan;
import com.chinalwb.are.spans.AreImageSpan;
import com.chinalwb.are.spans.AreVideoSpan;
import com.chinalwb.are.strategies.AreClickStrategy;
import com.chinalwb.are.strategies.defaults.DefaultImagePreviewActivity;
import com.chinalwb.are.strategies.defaults.DefaultProfileActivity;
import com.example.tsinghuadaily.Fragment.MessageFragment;
import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.base.BaseRecyclerAdapter;
import com.example.tsinghuadaily.base.RecyclerViewHolder;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            intent.setClass(context, ExoVideoPreviewActivity.class);
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

    private QMUITopBar mTopBar;

    private AreTextView areTextView;

    private AreClickStrategy mClickStrategy;

    private RecyclerView mRecyclerView;

    private BaseRecyclerAdapter<String> mAdapter;

    private Button mButtonLike;

    private Button mButtonComment;

    private Button mButtonCollection;

    private Context mContext;

    String title;
    String article_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        areTextView = findViewById(R.id.areTextView);
        mTopBar = findViewById(R.id.topbar);
        mButtonLike = findViewById(R.id.button_like);
        mContext = this;
        mButtonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mButtonComment = findViewById(R.id.button_comment);
        mButtonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mButtonCollection = findViewById(R.id.button_collection);
        mButtonCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mClickStrategy = new DClickStrategy();
        areTextView.setClickStrategy(mClickStrategy);

        Intent intent = getIntent();
        String s = intent.getStringExtra(HTML_TEXT);
        title = intent.getStringExtra("title");
        article_id = intent.getStringExtra("id");

        initTopBar(title);
        if (s == null) {
            s = "<p style=\"text-align: center;\"><strong>无内容</strong></p>";
        }
        areTextView.fromHtml(s);

        mRecyclerView = findViewById(R.id.CommentRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });

        mAdapter = new BaseRecyclerAdapter<String>(getApplicationContext(), null) {
            @Override
            public int getItemLayoutId(int viewType) {
                return android.R.layout.simple_list_item_1;
            }

            @Override
            public void bindData(RecyclerViewHolder holder, int position, String item) {
                holder.setText(android.R.id.text1, item);
            }
        };
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int pos) {
                Toast.makeText(getApplicationContext(), "click position=" + pos, Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        onDataLoaded();

    }

    private void onDataLoaded() {
        List<String> data = new ArrayList<>(Arrays.asList("评论1", "评论2", "评论3", "评论4", "评论5"));
        Collections.shuffle(data);  //置换，非必需
        mAdapter.setData(data);
    }

    private void initTopBar(String title) {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTopBar.setTitle(title);
        mTopBar.addRightTextButton("分享", QMUIViewHelper.generateViewId())
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, SelectContactActivity.class);
                        intent.putExtra("article_title", title);
                        intent.putExtra("article_id", article_id);
                        startActivity(intent);
                    }
                });


    }

}