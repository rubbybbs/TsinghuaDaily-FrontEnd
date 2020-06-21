package com.example.tsinghuadaily.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.tsinghuadaily.Activity.ArticleDetailActivity;
import com.example.tsinghuadaily.Fragment.Variety.CorporationArticleFragment;
import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.base.BaseFragment;
import com.example.tsinghuadaily.utils.OkHttpUtil;
import com.qmuiteam.qmui.arch.annotation.DefaultFirstFragment;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleResearchFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.btnSearch)
    Button sendBtn;
    @BindView(R.id.searchKey)
    EditText messageEdit;
    @BindView(R.id.searchArticleListView)
    QMUIGroupListView mGroupListView;
    @BindView(R.id.searchScrollView)
    ScrollView scrollView;

    private int mCurrentPageNum = 1;
    Handler handler;

    //标识是否滑动到顶部
    private boolean isScrollToStart = false;
    //标识是否滑动到底部
    private boolean isScrollToEnd = false;
    private static final int CODE_TO_START = 0x001;
    private static final int CODE_TO_END = 0x002;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CODE_TO_START:
                    //重置标志“滑动到顶部”时的标志位
                    isScrollToStart = false;
                    break;
                case CODE_TO_END:
                    //重置标志“滑动到底部”时的标志位
                    isScrollToEnd = false;
                    break;
                default:
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView() {
        // Inflate the layout for this fragment
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_article_research, null);
        ButterKnife.bind(this, root);

        mTopBar.setTitle("文章搜索");
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageEdit.getText().toString();
                new GetArticleListTask().execute(msg);
            }
        });

        handler = new Handler() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();

                String val = data.getString("requestRes");
                JSONObject obj = JSONObject.parseObject(val);
                if (obj == null){
                    Toast.makeText(getContext(), "获取文章列表失败，请检查认证或登录情况", Toast.LENGTH_SHORT).show();
                    return;
                }

                int code = Integer.parseInt(obj.get("code").toString());

                if (code == 200) {
                    JSONArray articlesRaw = JSONArray.parseArray(obj.get("articles").toString());

                    View.OnClickListener onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v instanceof QMUICommonListItemView) {
                                CharSequence text = ((QMUICommonListItemView) v).getText();
                                int articleID = Integer.parseInt(((QMUICommonListItemView) v).getDetailText().toString().split(" ")[0]);
                                //Toast.makeText(getActivity(), text + " is Clicked", Toast.LENGTH_SHORT).show();
                                if (((QMUICommonListItemView) v).getAccessoryType() == QMUICommonListItemView.ACCESSORY_TYPE_SWITCH) {
                                    ((QMUICommonListItemView) v).getSwitch().toggle();
                                }

                                Handler articleHandler= new Handler(){
                                    @Override
                                    public void handleMessage(@NonNull Message msg) {
                                        super.handleMessage(msg);
                                        Bundle data = msg.getData();
                                        String val = data.getString("requestRes");
                                        JSONObject obj = JSONObject.parseObject(val);
                                        if (obj == null){
                                            Toast.makeText(getContext(), "获取文章信息失败，请重试", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        int code = Integer.parseInt(obj.get("code").toString());

                                        if (code == 200) {
                                            JSONObject info = JSONObject.parseObject(obj.get("info").toString());
                                            String html = info.get("content").toString();
                                            String id = info.get("article_id").toString();
                                            String like = info.get("liked").toString();
                                            String favour = info.get("favoured").toString();
                                            Intent intent = new Intent();
                                            intent.putExtra("id", id);
                                            intent.putExtra("html_text", html);
                                            intent.putExtra("title", text);
                                            intent.putExtra("like", like);
                                            intent.putExtra("favour", favour);
                                            intent.setClass(getContext(), ArticleDetailActivity.class);
                                            getContext().startActivity(intent);
                                        }


                                    }
                                };

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String res = OkHttpUtil.get("http://175.24.61.249:8080/article/article?article_id="+articleID);
                                        Message msg = new Message();
                                        Bundle data = new Bundle();
                                        data.putString("requestRes", res);
                                        msg.setData(data);
                                        articleHandler.sendMessage(msg);
                                    }
                                }).start();

                            }
                        }
                    };

                    int size = QMUIDisplayHelper.dp2px(getContext(), 20);
                    int height = QMUIDisplayHelper.dp2px(getContext(), 56);
                    QMUIGroupListView.Section section = QMUIGroupListView.newSection(getContext());

                    for (int i = 0; i<articlesRaw.size(); i++){
                        JSONObject article = JSONObject.parseObject(articlesRaw.get(i).toString());

                        QMUICommonListItemView item = mGroupListView.createItemView(article.get("title").toString());
                        item.setOrientation(QMUICommonListItemView.VERTICAL);
                        item.setDetailText(article.get("article_id").toString() + " " +article.get("publish_time"));

                        section.addItemView(item, onClickListener);
                    }

                    section.addTo(mGroupListView);

                    if (articlesRaw.size()!=0) {
                        mCurrentPageNum++;
                    } else {
                        Toast.makeText(getContext(), "没有更多了", Toast.LENGTH_SHORT).show();
                    }

                } else if (code == 404){
                    Toast.makeText(getContext(), "获取文章列表失败，" + obj.get("msg").toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "获取文章列表失败", Toast.LENGTH_SHORT).show();
                }

            }

        };

        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener(){
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY == 0){
                    //过滤操作，优化为一次调用
                    if (!isScrollToStart) {
                        isScrollToStart = true;
                        mHandler.sendEmptyMessageDelayed(CODE_TO_START, 200);
                        Toast.makeText(getContext(), "到顶了", Toast.LENGTH_SHORT).show();
                    }

                }
                // 判断scrollview 滑动到底部
                // scrollY 的值和子view的高度一样，这人物滑动到了底部
                else if (scrollView.getChildAt(0)!=null && scrollView.getChildAt(0).getHeight()
                        - scrollView.getHeight() == scrollView.getScrollY()){
                    //优化，只过滤第一次
                    if (!isScrollToEnd) {
                        isScrollToEnd = true;
                        mHandler.sendEmptyMessageDelayed(CODE_TO_END, 200);

                        Toast.makeText(getContext(), "已滑动至底部，正在加载", Toast.LENGTH_SHORT).show();
                        new GetArticleListTask().execute();
                    }


                }
            }
        });

        return root;
    }

    private class GetArticleListTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            Map<String, String> params = new HashMap<>();
            String baseurl = "http://175.24.61.249:8080/article/search?query="+ strings[0] +"&page_num="+ mCurrentPageNum +"&page_size=10";
            String res = OkHttpUtil.postForm(baseurl, params);
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("requestRes", res);
            msg.setData(data);

            handler.sendMessage(msg);
            return null;
        }
    }
}