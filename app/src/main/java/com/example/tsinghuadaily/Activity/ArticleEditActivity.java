package com.example.tsinghuadaily.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.alibaba.fastjson.JSONObject;
import com.chinalwb.are.AREditText;
import com.chinalwb.are.spans.AreImageSpan;
import com.chinalwb.are.strategies.ImageStrategy;
import com.chinalwb.are.strategies.VideoStrategy;
import com.chinalwb.are.styles.toolbar.ARE_ToolbarDefault;
import com.chinalwb.are.styles.toolbar.IARE_Toolbar;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentCenter;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentLeft;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentRight;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_At;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_BackgroundColor;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Bold;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_FontColor;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_FontSize;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Hr;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Image;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Italic;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Link;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_ListBullet;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_ListNumber;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Quote;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Strikethrough;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Subscript;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Superscript;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Underline;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Video;
import com.chinalwb.are.styles.toolitems.IARE_ToolItem;
import com.chinalwb.are.styles.toolitems.styles.ARE_Style_Image;
import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.services.WebSocketService;
import com.example.tsinghuadaily.utils.ArticleUtils;
import com.example.tsinghuadaily.utils.JWebSocketClient;
import com.example.tsinghuadaily.utils.OkHttpUtil;
import com.example.tsinghuadaily.utils.UriUtils;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

class DImageStrategy implements ImageStrategy {

    @Override
    public void uploadAndInsertImage(Uri uri, ARE_Style_Image areStyleImage) {
        new UploadImageTask(areStyleImage).executeOnExecutor(THREAD_POOL_EXECUTOR, uri);
    }

    private static class UploadImageTask extends AsyncTask<Uri, Integer, String> {

        WeakReference<ARE_Style_Image> areStyleImage;
        private ProgressDialog dialog;
        Context mContext;
        public static final int EXTERNAL_STORAGE_REQ_CODE = 10 ;

        UploadImageTask(ARE_Style_Image styleImage) {
            this.areStyleImage = new WeakReference<>(styleImage);
        }

        @Nullable
        public static Activity findActivity(Context context) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            if (context instanceof ContextWrapper) {
                ContextWrapper wrapper = (ContextWrapper) context;
                return findActivity(wrapper.getBaseContext());
            } else {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mContext = areStyleImage.get().getEditText().getContext();

            if (dialog == null) {
                dialog = ProgressDialog.show(
                        mContext,
                        "",
                        "Uploading image. Please wait...",
                        true);
            } else {
                dialog.show();
            }
        }

        @SuppressLint("HandlerLeak")
        @Override
        protected String doInBackground(Uri... uris) {
            if (uris != null && uris.length > 0) {

//                ContentResolver resolver = mContext.getContentResolver();
//                Cursor cursor = resolver.query(uris[0], null, null, null, null);
//                String path = uris[0].getPath();
//                if (cursor == null) {
//                    // 未查询到，说明为普通文件，可直接通过URI获取文件路径
//                    return null;
//                }
//                if (cursor.moveToFirst()) {
//                    // 多媒体文件，从数据库中获取文件的真实路径
//                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
//                    cursor.close();
//                    File file = new File(path);
//                    Random random = new Random(System.currentTimeMillis());
//                    String msg = OkHttpUtil.uploadFile(file,  String.valueOf(random.nextInt(100000)) + ".png");
//                    String ImageUrl = msg.split("\"")[5];
//                    String baseUrl = "http://175.24.61.249:8080/media/get?";
//                    return baseUrl+ImageUrl;
//                }
                // Returns the image url on server here

                String path = UriUtils.getPath(mContext, uris[0]);
                File file = new File(path);
                Random random = new Random(System.currentTimeMillis());
                String msg = OkHttpUtil.uploadFile(file,  String.valueOf(random.nextInt(1000000)) + ".png");
                String videoUrl = msg.split("\"")[5];
                String baseUrl = "http://175.24.61.249:8080/media/get?";
                return baseUrl+videoUrl;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (dialog != null) {
                dialog.dismiss();
            }
            if (areStyleImage.get() != null) {
                areStyleImage.get().insertImage(s, AreImageSpan.ImageType.URL);
            }
        }
    }
}

class DVideoStrategy implements VideoStrategy {
    Context mContext;

    public DVideoStrategy(Context context){
        mContext = context;
    }

    @Override
    public String uploadVideo(Uri uri) {
        if (uri != null) {
            String path = UriUtils.getPath(mContext, uri);
            File file = new File(path);
            Random random = new Random(System.currentTimeMillis());
            String msg = OkHttpUtil.uploadFile(file,  String.valueOf(random.nextInt(1000000)) + ".mp4");
            String videoUrl = msg.split("\"")[5];
            String baseUrl = "http://175.24.61.249:8080/media/get?";
            return baseUrl+videoUrl;
        }
        return null;

    }

    @Override
    public String uploadVideo(String videoPath) {
        File file = new File(videoPath);
        Random random = new Random(System.currentTimeMillis());
        String msg = OkHttpUtil.uploadFile(file,  String.valueOf(random.nextInt(1000000)) + ".mp4");
        String videoUrl = msg.split("\"")[5];
        String baseUrl = "http://175.24.61.249:8080/media/get?";
        return baseUrl+videoUrl;
    }
}

public class ArticleEditActivity extends AppCompatActivity {

    IARE_Toolbar mToolbar;

    AREditText mEditText;

    EditText mTopicText;

    EditText mDescribeText;

    String readerText;

    QMUITopBar mTopBar;

    RadioGroup mRadioGroup;

    private int UID;

    Handler handler;

    public static final int EXTERNAL_STORAGE_REQ_CODE = 10 ;

    private ImageStrategy imageStrategy;

    private VideoStrategy mVideoStrategy;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_edit);

        initToolbar();
        initTopBar();

        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_REQ_CODE);
        }

        mTopicText = this.findViewById(R.id.edittext_topic);
        //mDescribeText = this.findViewById(R.id.edittext_describe);
        mRadioGroup = this.findViewById(R.id.rg_level);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = radioGroup.findViewById(i);
                readerText = radioButton.getText().toString();
                Toast.makeText(getApplicationContext(), readerText, Toast.LENGTH_SHORT).show();
            }
        });

        UID = getSharedPreferences("userdata", MODE_PRIVATE).getInt("uid", 0);

        //保存文章
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                String val = data.getString("requestRes");
                JSONObject obj = JSONObject.parseObject(val);
                if (obj.get("code").equals(200))
                {
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    String errorMsg = obj.get("msg").toString();
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void initTopBar() {
        if (mTopBar == null) {
            mTopBar = this.findViewById(R.id.topbar);
        }
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTopBar.setTitle("分类");
        mTopBar.addRightTextButton("C", QMUIViewHelper.generateViewId())
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String html = mEditText.getHtml();
                        Toast.makeText(getApplicationContext(), html, Toast.LENGTH_LONG).show();
                        Map<String, String> params = new HashMap<>();
                        params.put("title", mTopicText.getText().toString());
                        params.put("author", String.valueOf(UID));
                        params.put("content", html);
                        params.put("reader", readerText);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String res = OkHttpUtil.postForm("http://175.24.61.249:8080/article/publish", params);
                                Message msg = new Message();
                                Bundle data = new Bundle();
                                data.putString("requestRes", res);
                                msg.setData(data);
                                handler.sendMessage(msg);
                            }
                        }).start();

                    }
                });
        mTopBar.addRightTextButton("P", QMUIViewHelper.generateViewId())
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String html = mEditText.getHtml();
                        Intent intent = new Intent(getApplication(), ArticleDetailActivity.class);
                        intent.putExtra("html_text", html);
                        startActivity(intent);
                    }
                }

        );
    }

    private void initToolbar() {
        mToolbar = this.findViewById(R.id.areToolbar);
        IARE_ToolItem bold = new ARE_ToolItem_Bold();
        IARE_ToolItem italic = new ARE_ToolItem_Italic();
        IARE_ToolItem underline = new ARE_ToolItem_Underline();
        IARE_ToolItem strikethrough = new ARE_ToolItem_Strikethrough();
        IARE_ToolItem fontSize = new ARE_ToolItem_FontSize();
        IARE_ToolItem fontColor = new ARE_ToolItem_FontColor();
        IARE_ToolItem backgroundColor = new ARE_ToolItem_BackgroundColor();
        IARE_ToolItem quote = new ARE_ToolItem_Quote();
        IARE_ToolItem listNumber = new ARE_ToolItem_ListNumber();
        IARE_ToolItem listBullet = new ARE_ToolItem_ListBullet();
        IARE_ToolItem hr = new ARE_ToolItem_Hr();
        IARE_ToolItem link = new ARE_ToolItem_Link();
        IARE_ToolItem subscript = new ARE_ToolItem_Subscript();
        IARE_ToolItem superscript = new ARE_ToolItem_Superscript();
        IARE_ToolItem left = new ARE_ToolItem_AlignmentLeft();
        IARE_ToolItem center = new ARE_ToolItem_AlignmentCenter();
        IARE_ToolItem right = new ARE_ToolItem_AlignmentRight();
        IARE_ToolItem image = new ARE_ToolItem_Image();
        IARE_ToolItem video = new ARE_ToolItem_Video();
        IARE_ToolItem at = new ARE_ToolItem_At();

        mToolbar.addToolbarItem(bold);
        mToolbar.addToolbarItem(italic);
        mToolbar.addToolbarItem(underline);
        mToolbar.addToolbarItem(strikethrough);
        mToolbar.addToolbarItem(fontSize);
        mToolbar.addToolbarItem(fontColor);
        mToolbar.addToolbarItem(backgroundColor);
        mToolbar.addToolbarItem(quote);
        mToolbar.addToolbarItem(listNumber);
        mToolbar.addToolbarItem(listBullet);
        mToolbar.addToolbarItem(hr);
        mToolbar.addToolbarItem(link);
        mToolbar.addToolbarItem(subscript);
        mToolbar.addToolbarItem(superscript);
        mToolbar.addToolbarItem(left);
        mToolbar.addToolbarItem(center);
        mToolbar.addToolbarItem(right);
        mToolbar.addToolbarItem(image);
        mToolbar.addToolbarItem(video);
        mToolbar.addToolbarItem(at);

        mEditText = this.findViewById(R.id.arEditText);
        mEditText.setToolbar(mToolbar);

        imageStrategy = new DImageStrategy();
        mVideoStrategy = new DVideoStrategy(getApplicationContext());

        mEditText.setImageStrategy(imageStrategy);
        mEditText.setVideoStrategy(mVideoStrategy);

        setHtml();
    }

    private void setHtml() {
        String html = "<p style=\"text-align: center;\"><strong>输入内容</strong></p>\n";
        mEditText.fromHtml(html);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int menuId = item.getItemId();
//        if (menuId == com.chinalwb.are.R.id.action_save) {
//            String html = this.mEditText.getHtml();
//            ArticleUtils.saveHtml(this, html);
//            return true;
//        }
//        if (menuId == R.id.action_show_tv) {
//            String html = this.mEditText.getHtml();
//            Intent intent = new Intent(this, ArticleDetailActivity.class);
//            intent.putExtra("html_text", html);
//            startActivity(intent);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mToolbar.onActivityResult(requestCode, resultCode, data);
    }
}