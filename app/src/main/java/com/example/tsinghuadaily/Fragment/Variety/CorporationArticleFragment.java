package com.example.tsinghuadaily.Fragment.Variety;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.tsinghuadaily.Activity.ArticleDetailActivity;
import com.example.tsinghuadaily.Fragment.ArticleDetailFragment;
import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.base.BaseFragment;
import com.example.tsinghuadaily.utils.OkHttpUtil;
import com.example.tsinghuadaily.utils.Widget;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */

@Widget(widgetClass = CorporationArticleFragment.class, iconRes = R.mipmap.ic_launcher)
public class CorporationArticleFragment extends BaseFragment {

    @BindView(R.id.articleListView)
    QMUIGroupListView mGroupListView;
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;


    @BindView(R.id.scrollView)
    ScrollView scrollView;

    int section_id = 8;
    boolean isFollow;
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

    private int mCurrentPageNum = 1;
    Handler handler;

    @SuppressLint("HandlerLeak")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView() {
        // Inflate the layout for this fragment
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_school_article_list, null);
        ButterKnife.bind(this, root);

        initTopBar();

        initGroupListView();

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
                                int articleID = Integer.parseInt(((QMUICommonListItemView) v).getDetailText().toString().split(" ")[1]);
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

//                        QMUICommonListItemView item = mGroupListView.createItemView(article.get("title").toString());
//                        item.setOrientation(QMUICommonListItemView.VERTICAL);
//                        item.setDetailText(article.get("article_id").toString() + " " +article.get("publish_time"));
                        QMUICommonListItemView item = mGroupListView.createItemView(null,
                                article.get("title").toString(),
                                article.get("author_name").toString() + " " + article.get("article_id").toString() + " " + article.get("publish_time") + "\n" +
                                        article.getString("view_num") + "浏览，" + article.getString("like_num") + "点赞，" + article.getString("fav_num") + "收藏",
                                QMUICommonListItemView.VERTICAL,
                                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        int paddingVer = QMUIDisplayHelper.dp2px(getContext(), 12);
                        item.setPadding(item.getPaddingLeft(), paddingVer,
                                item.getPaddingRight(), paddingVer);
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

        return root;
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle("围棋社文章列表");
        isFollow = false;
        Button followButton = mTopBar.addRightTextButton("关注", QMUIViewHelper.generateViewId());

        @SuppressLint("HandlerLeak") Handler secHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();

                String val = data.getString("requestRes");
                JSONObject obj = JSONObject.parseObject(val);
                if (obj == null){
                    Toast.makeText(getContext(), "获取关注情况失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    return;
                }

                int code = Integer.parseInt(obj.get("code").toString());

                if (code == 200) {
                    String follow = obj.get("followed").toString();
                    if (follow == "false") {
                        isFollow = false;
                        followButton.setText("关注");
                        followButton.setTextColor(getResources().getColor(R.color.black_overlay));
                    } else {
                        isFollow = true;
                        followButton.setText("已关注");
                        followButton.setTextColor(getResources().getColor(R.color.qmui_config_color_white));
                    }
                } else {
                    Toast.makeText(getContext(), "获取关注情况失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                }

            }

        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<>();
                String res;
                res = OkHttpUtil.get("http://175.24.61.249:8080/section/section-info?section_id=" + section_id);
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("requestRes", res);
                msg.setData(data);
                secHandler.sendMessage(msg);
            }
        }).start();

        @SuppressLint("HandlerLeak") Handler xHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();

                String val = data.getString("requestRes");
                JSONObject obj = JSONObject.parseObject(val);
                if (obj == null){
                    Toast.makeText(getContext(), "更改关注状态失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    return;
                }

                int code = Integer.parseInt(obj.get("code").toString());

                if (code == 200) {
                    String message = obj.get("msg").toString();
                    if (isFollow) {
                        isFollow = false;
                        followButton.setText("关注");
                        followButton.setTextColor(getResources().getColor(R.color.black_overlay));
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        isFollow = true;
                        followButton.setText("已关注");
                        followButton.setTextColor(getResources().getColor(R.color.qmui_config_color_white));
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), "获取关注情况失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                }

            }

        };

        followButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Map<String, String> params = new HashMap<>();
                                String res;
                                if (isFollow) {
                                    res = OkHttpUtil.postForm("http://175.24.61.249:8080/section/unfollow?section_id=" + section_id, params);
                                } else {
                                    res = OkHttpUtil.postForm("http://175.24.61.249:8080/section/follow?section_id=" + section_id, params);
                                }
                                Message msg = new Message();
                                Bundle data = new Bundle();
                                data.putString("requestRes", res);
                                msg.setData(data);
                                xHandler.sendMessage(msg);
                            }
                        }).start();


                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initGroupListView() {
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

        new GetArticleListTask().execute();
    }

    private class GetArticleListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String baseurl = "http://175.24.61.249:8080/article/section-articles?section_id=8&page_num="+ mCurrentPageNum +"&page_size=10";
            String res = OkHttpUtil.get(baseurl);
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("requestRes", res);
            msg.setData(data);

            handler.sendMessage(msg);


            return null;
        }
    }

}