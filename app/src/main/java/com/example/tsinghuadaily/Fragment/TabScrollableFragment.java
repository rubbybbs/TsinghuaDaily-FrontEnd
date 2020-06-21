package com.example.tsinghuadaily.Fragment;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinalwb.are.strategies.defaults.DefaultProfileActivity;
import com.example.tsinghuadaily.Activity.ArticleDetailActivity;
import com.example.tsinghuadaily.Activity.ArticleEditActivity;
import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.base.CustomScrollView;
import com.example.tsinghuadaily.utils.OkHttpUtil;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.arch.QMUIFragmentPagerAdapter;
import com.qmuiteam.qmui.arch.SwipeBackLayout;
import com.qmuiteam.qmui.skin.QMUISkinHelper;
import com.qmuiteam.qmui.skin.QMUISkinValueBuilder;
import com.qmuiteam.qmui.skin.SkinWriter;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.QMUIViewPager;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.qmuiteam.qmui.widget.tab.QMUITabBuilder;
import com.qmuiteam.qmui.widget.tab.QMUITabIndicator;
import com.qmuiteam.qmui.widget.tab.QMUITabSegment;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabScrollableFragment extends QMUIFragment {
    @SuppressWarnings("FieldCanBeLocal") private final int TAB_COUNT = 5;

    @BindView(R.id.topbarMainPage) QMUITopBarLayout mTopBar;
    @BindView(R.id.tabSegment) QMUITabSegment mTabSegment;
    @BindView(R.id.contentViewPager) ViewPager mContentViewPager;

    private Handler handler;

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

    private Map<ContentPage, View> mPageMap = new HashMap<>();
    private int[] mCurrentPageNum = {1, 1, 1, 1, 1};
    private ContentPage mDestPage = ContentPage.Item1;
    private int mCurrentItemCount = TAB_COUNT;
    private PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return mCurrentItemCount;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            ContentPage page = ContentPage.getPage(position);
            View view = getPageView(page);
            view.setTag(page);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(view, params);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            View view = (View) object;
            Object page = view.getTag();
            if (page instanceof ContentPage) {
                int pos = ((ContentPage) page).getPosition();
                if (pos >= mCurrentItemCount) {
                    return POSITION_NONE;
                }
                return POSITION_UNCHANGED;
            }
            return POSITION_NONE;
        }
    };

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Toast.makeText(getContext(), "started by scheme", Toast.LENGTH_SHORT).show();
//
//        Bundle args = getArguments();
//        if(args != null){
//            int mode = args.getInt("mode");
//            Toast.makeText(getContext(), "mode = " + mode, Toast.LENGTH_SHORT).show();
//        }

        handler = new Handler() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                int pos = data.getInt("position", 0);

                String val = data.getString("requestRes");
                JSONObject obj = JSONObject.parseObject(val);
                if (obj == null){
                    Toast.makeText(getContext(), "获取文章列表失败，请检查认证或登录情况", Toast.LENGTH_SHORT).show();
                    return;
                }

                int code = Integer.parseInt(obj.get("code").toString());

                if (code == 200) {
                    JSONArray articlesRaw = JSONArray.parseArray(obj.get("articles").toString());
                    ScrollView mScrollView = (ScrollView)getPageView(ContentPage.getPage(pos));
                    QMUIGroupListView mGroupListView = (QMUIGroupListView)mScrollView.getChildAt(0);

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
//                    section.setTitle("Section 1: 关注")
//                            .setDescription("Section 1 的描述")
//                            .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
//                            .setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(getContext(), 16), 0);
//
                    for (int i = 0; i<articlesRaw.size(); i++){
                        JSONObject article = JSONObject.parseObject(articlesRaw.get(i).toString());

                        QMUICommonListItemView item = mGroupListView.createItemView(article.get("title").toString());
                        item.setOrientation(QMUICommonListItemView.VERTICAL);
                        item.setDetailText(article.get("author_name").toString() + " " + article.get("article_id").toString() + " " + article.get("publish_time"));

                        section.addItemView(item, onClickListener);
                    }

                    section.addTo(mGroupListView);

                    if (articlesRaw.size()!=0) {
                        mCurrentPageNum[pos]++;
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

    }


    @Override
    protected View onCreateView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_tab_scrollable, null);
        ButterKnife.bind(this, rootView);

        initTopBar();
        initTabAndPager();

        return rootView;
    }

    private void initTopBar() {
//        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popBackStack();
//            }
//        });

        mTopBar.setTitle("主页");
        mTopBar.addRightTextButton("+", QMUIViewHelper.generateViewId())
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editNewArticle();
                    }
                });
    }

    private void initTabAndPager() {
        mContentViewPager.setAdapter(mPagerAdapter);
        mContentViewPager.setCurrentItem(mDestPage.getPosition(), false);
        QMUITabBuilder tabBuilder = mTabSegment.tabBuilder();
        String title[]={"    关注    ","    学校    ","    院系    ","    社团    ", "    收藏    "};
        for (int i = 0; i < mCurrentItemCount; i++) {
            mTabSegment.addTab(tabBuilder.setText(title[i]).build(getContext()));
        }
        int space = QMUIDisplayHelper.dp2px(getContext(), 16);
        mTabSegment.setIndicator(new QMUITabIndicator(
                QMUIDisplayHelper.dp2px(getContext(), 2), false, true));
        mTabSegment.setMode(QMUITabSegment.MODE_SCROLLABLE);
        mTabSegment.setItemSpaceInScrollMode(space);
        mTabSegment.setupWithViewPager(mContentViewPager, false);
        mTabSegment.setPadding(space, 0, space, 0);
        mTabSegment.addOnTabSelectedListener(new QMUITabSegment.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int index) {
                //Toast.makeText(getContext(), "select index " + index, Toast.LENGTH_SHORT).show();
                //new GetArticleListTask().execute(ContentPage.getPage(index));
            }

            @Override
            public void onTabUnselected(int index) {
                //Toast.makeText(getContext(), "unSelect index " + index, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabReselected(int index) {
                //Toast.makeText(getContext(), "reSelect index " + index, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDoubleTap(int index) {
                //Toast.makeText(getContext(), "double tap index " + index, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editNewArticle() {
        Intent intent = new Intent(getActivity(), ArticleEditActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private View getPageView(ContentPage page) {
        View view = mPageMap.get(page);
        if (view == null) {
            ScrollView scrollView = new ScrollView(getContext());
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
                            new GetArticleListTask().execute(ContentPage.getPage(mTabSegment.getSelectedIndex()));
                        }


                    }
                }
            });
            QMUIGroupListView listView = new QMUIGroupListView(getContext());
            scrollView.addView(listView);
            initGroupListView(listView, page);
            view = scrollView;
            mPageMap.put(page, view);
        }
        return view;
    }

    private void initGroupListView(QMUIGroupListView mGroupListView, ContentPage page) {
        new GetArticleListTask().execute(page);
    }



    public enum ContentPage {
        Item1(0),
        Item2(1),
        Item3(2),
        Item4(3),
        Item5(4);
        private final int position;

        ContentPage(int pos) {
            position = pos;
        }

        public static ContentPage getPage(int position) {
            switch (position) {
                case 0:
                    return Item1;
                case 1:
                    return Item2;
                case 2:
                    return Item3;
                case 3:
                    return Item4;
                case 4:
                default:
                    return Item5;
            }
        }

        public int getPosition() {
            return position;
        }
    }

    private class GetArticleListTask extends AsyncTask<ContentPage, Void, Void> {

        @Override
        protected Void doInBackground(ContentPage... page) {

            switch (page[0]) {
                case Item1: {
                    String baseurl = "http://175.24.61.249:8080/article/cateory-articles?category=Follow&page_num="+ mCurrentPageNum[0] +"&page_size=10";
                    String res = OkHttpUtil.get(baseurl);
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putInt("position", 0);
                    data.putString("requestRes", res);
                    msg.setData(data);
                    handler.sendMessage(msg);
                    break;
                }
                case Item2: {
                    String baseurl = "http://175.24.61.249:8080/article/cateory-articles?category=Campus&page_num="+ mCurrentPageNum[1] +"&page_size=10";
                    String res = OkHttpUtil.get(baseurl);
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putInt("position", 1);
                    data.putString("requestRes", res);
                    msg.setData(data);
                    handler.sendMessage(msg);
                    break;
                }
                case Item3: {
                    String baseurl = "http://175.24.61.249:8080/article/cateory-articles?category=Faculty&page_num="+ mCurrentPageNum[2] +"&page_size=10";
                    String res = OkHttpUtil.get(baseurl);
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putInt("position", 2);
                    data.putString("requestRes", res);
                    msg.setData(data);
                    handler.sendMessage(msg);
                    break;
                }
                case Item4: {
                    String baseurl = "http://175.24.61.249:8080/article/cateory-articles?category=Club&page_num="+ mCurrentPageNum[3] +"&page_size=10";
                    String res = OkHttpUtil.get(baseurl);
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putInt("position", 3);
                    data.putString("requestRes", res);
                    msg.setData(data);
                    handler.sendMessage(msg);
                    break;
                }
                case Item5:
                default: {
                    String baseurl = "http://175.24.61.249:8080/article/cateory-articles?category=Favourite&page_num="+ mCurrentPageNum[4] +"&page_size=10";
                    String res = OkHttpUtil.get(baseurl);
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putInt("position", 4);
                    data.putString("requestRes", res);
                    msg.setData(data);
                    handler.sendMessage(msg);
                    break;
                }
            }

            return null;
        }
    }
}
