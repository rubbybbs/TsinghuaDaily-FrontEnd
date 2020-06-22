package com.example.tsinghuadaily.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinalwb.are.Util;
import com.chinalwb.are.render.AreTextView;
import com.chinalwb.are.spans.AreAtSpan;
import com.chinalwb.are.spans.AreImageSpan;
import com.chinalwb.are.spans.AreVideoSpan;
import com.chinalwb.are.strategies.AreClickStrategy;
import com.chinalwb.are.strategies.defaults.DefaultImagePreviewActivity;
import com.chinalwb.are.strategies.defaults.DefaultProfileActivity;
import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.base.BaseRecyclerAdapter;
import com.example.tsinghuadaily.base.RecyclerViewHolder;
import com.example.tsinghuadaily.utils.OkHttpUtil;
import com.qmuiteam.qmui.layout.QMUIFrameLayout;
import com.qmuiteam.qmui.skin.QMUISkinHelper;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.skin.QMUISkinValueBuilder;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.popup.QMUIFullScreenPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.style.URLSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private QMUIPopup mNormalPopup;

    private Button mButtonCollection;


    private Context mContext;

    String title;

    String articleID;

    boolean isFavour = false;

    boolean isLike = false;

    Handler handler;

    List<String> mData;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        Intent intent = getIntent();
        String s = intent.getStringExtra(HTML_TEXT);
        title = intent.getStringExtra("title");
        articleID = intent.getStringExtra("id");
        String like = intent.getStringExtra("like");
        String favour = intent.getStringExtra("favour");
        isLike = like != null && like.compareTo("true") == 0;
        isFavour = favour != null && favour.compareTo("true") == 0;

        mContext = this;
        areTextView = findViewById(R.id.areTextView);
        mTopBar = findViewById(R.id.topbar);
        mButtonLike = findViewById(R.id.button_like);
        mButtonComment = findViewById(R.id.button_comment);
        mButtonCollection = findViewById(R.id.button_collection);

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                String val = data.getString("requestRes");
                JSONObject obj = JSONObject.parseObject(val);
                if (obj == null){
                    Toast.makeText(getApplicationContext(), "失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (obj.get("code").equals(200))
                {
                    int type = data.getInt("type");
                    switch (type) {
                        case 0:{
                            if (isLike) {
                                isLike = false;
                                Toast.makeText(getApplicationContext(), "取消点赞", Toast.LENGTH_SHORT).show();
                                mButtonLike.setText("点赞");
                                mButtonLike.setTextColor(getResources().getColor(R.color.qmui_config_color_black));
                            } else {
                                isLike = true;
                                Toast.makeText(getApplicationContext(), "点赞成功", Toast.LENGTH_SHORT).show();
                                mButtonLike.setText("已点赞");
                                mButtonLike.setTextColor(getResources().getColor(R.color.qmui_config_color_red));
                            }
                            break;
                        }
                        case 1:{
                            Toast.makeText(getApplicationContext(), "评论成功", Toast.LENGTH_SHORT).show();
                            mData.add(data.getString("comment_content"));
                            mAdapter.setData(mData);
                            break;
                        }
                        case 2:{
                            if (isFavour) {
                                isFavour = false;
                                Toast.makeText(getApplicationContext(), "取消收藏", Toast.LENGTH_SHORT).show();
                                mButtonCollection.setText("收藏");
                                mButtonCollection.setTextColor(getResources().getColor(R.color.qmui_config_color_black));
                            } else {
                                isFavour = true;
                                Toast.makeText(getApplicationContext(), "收藏成功", Toast.LENGTH_SHORT).show();
                                mButtonCollection.setText("已收藏");
                                mButtonCollection.setTextColor(getResources().getColor(R.color.qmui_config_color_red));
                            }
                            break;
                        }
                        case 3: {
                            JSONArray comments = JSONArray.parseArray(obj.get("comments").toString());
                            for (int i = 0; i<comments.size(); i++){
                                JSONObject comment = JSONObject.parseObject(comments.get(i).toString());
                                mData.add(comment.getString("username") + ": \n         " + comment.getString("content"));
                            }
                            mAdapter.setData(mData);
                            break;
                        }
                        default:
                            break;
                    }
                }
                else
                {
                    String errorMsg = obj.get("msg").toString();
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }

            }
        };

        if (articleID.compareTo("-1")!=0) {
            if (isLike) {
                mButtonLike.setText("已点赞");
                mButtonLike.setTextColor(getResources().getColor(R.color.qmui_config_color_red));
            } else {
                mButtonLike.setText("点赞");

            }

            if (isFavour) {
                mButtonCollection.setText("已收藏");
                mButtonCollection.setTextColor(getResources().getColor(R.color.qmui_config_color_red));
            } else {
                mButtonCollection.setText("收藏");
                mButtonCollection.setTextColor(getResources().getColor(R.color.qmui_config_color_black));
            }

            Map<String, String> params = new HashMap<>();
            mButtonLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String res;
                            if (isLike) {
                                res = OkHttpUtil.postForm("http://175.24.61.249:8080/article/dislike?article_id=" + articleID, params);
                            } else {
                                res = OkHttpUtil.postForm("http://175.24.61.249:8080/article/like?article_id=" + articleID, params);
                            }
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putString("requestRes", res);
                            data.putInt("type", 0);
                            msg.setData(data);
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            });

            mButtonComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    QMUISkinValueBuilder builder = QMUISkinValueBuilder.acquire();
                    QMUIFrameLayout frameLayout = new QMUIFrameLayout(getApplicationContext());
                    frameLayout.setBackground(
                            QMUIResHelper.getAttrDrawable(getApplicationContext(), R.color.qmui_config_color_50_white));
                    builder.background(R.color.qmui_config_color_50_white);
                    QMUISkinHelper.setSkinValue(frameLayout, builder);
                    frameLayout.setRadius(QMUIDisplayHelper.dp2px(getApplicationContext(), 12));
                    int padding = QMUIDisplayHelper.dp2px(getApplicationContext(), 20);
                    frameLayout.setPadding(padding, padding, padding, padding);

                    EditText textView = new EditText(getApplicationContext());
                    textView.setLineSpacing(QMUIDisplayHelper.dp2px(getApplicationContext(), 4), 1.0f);
                    textView.setPadding(padding, padding, padding, padding);
                    textView.setHint("输入评论...点击旁边区域发送");
                    textView.setBackgroundColor(getResources().getColor(R.color.qmui_config_color_white));
                    int paddingHor = QMUIDisplayHelper.dp2px(getApplicationContext(), 20);
                    int paddingVer = QMUIDisplayHelper.dp2px(getApplicationContext(), 10);
                    textView.setPadding(paddingHor, paddingVer, paddingHor, paddingVer);
                    textView.setMaxHeight(QMUIDisplayHelper.dp2px(getApplicationContext(), 200));
                    builder.clear();
                    builder.textColor(R.color.black_overlay);
                    QMUISkinHelper.setSkinValue(textView, builder);
                    textView.setGravity(Gravity.CENTER);
                    int size = QMUIDisplayHelper.dp2px(getApplicationContext(), 200);
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(size, size);
                    frameLayout.addView(textView, lp);

                    QMUIPopups.fullScreenPopup(getApplicationContext())
                            .addView(frameLayout, QMUIFullScreenPopup.getOffsetHalfKeyboardHeightListener())
                            .skinManager(QMUISkinManager.defaultInstance(getApplicationContext()))
                            .onBlankClick(new QMUIFullScreenPopup.OnBlankClickListener() {
                                @Override
                                public void onBlankClick(QMUIFullScreenPopup popup) {
                                    //popup.dismiss();
                                    Toast.makeText(getApplicationContext(), "正在发送评论", Toast.LENGTH_SHORT).show();
                                    Map<String, String> mparams = new HashMap<>();
                                    String commmentContent = textView.getText().toString();
                                    mparams.put("content", commmentContent);
                                    mparams.put("article_id", articleID);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String res = OkHttpUtil.postForm("http://175.24.61.249:8080/comment/add", mparams);
                                            Message msg = new Message();
                                            Bundle data = new Bundle();
                                            data.putString("requestRes", res);
                                            data.putInt("type", 1);
                                            data.putString("comment_content", commmentContent);
                                            msg.setData(data);
                                            handler.sendMessage(msg);
                                        }
                                    }).start();
                                    popup.dismiss();
                                }
                            })
                            .onDismiss(new PopupWindow.OnDismissListener() {
                                @Override
                                public void onDismiss() {
                                    //Toast.makeText(getApplicationContext(), "取消发送", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show(v);
                }
            });

            mButtonCollection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String res;
                            if (isFavour) {
                                res = OkHttpUtil.postForm("http://175.24.61.249:8080/article/disfavour?article_id=" + articleID, params);
                            } else {
                                res = OkHttpUtil.postForm("http://175.24.61.249:8080/article/favour?article_id=" + articleID, params);
                            }
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putString("requestRes", res);
                            data.putInt("type", 2);
                            msg.setData(data);
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            });
        }


        mClickStrategy = new DClickStrategy();
        areTextView.setClickStrategy(mClickStrategy);



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
                //Toast.makeText(getApplicationContext(), "click position=" + pos, Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        onDataLoaded();

    }

    private void onDataLoaded() {
        if (articleID.compareTo("-1")!=0) {
            mData = new ArrayList<>();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String res = OkHttpUtil.get("http://175.24.61.249:8080/comment/get-comments?article_id=" + articleID);
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("requestRes", res);
                    data.putInt("type", 3);
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            }).start();
        } else {
            mData = new ArrayList<>(Arrays.asList("预览评论1", "预览评论2", "预览评论3", "预览评论4", "预览评论5"));
            //Collections.shuffle(mData);  //置换，非必需
            mAdapter.setData(mData);
        }
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
                        intent.putExtra("article_id", articleID);
                        startActivity(intent);
                    }
                });


    }

}