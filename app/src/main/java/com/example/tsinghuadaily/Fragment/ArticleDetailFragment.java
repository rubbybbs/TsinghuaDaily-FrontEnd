package com.example.tsinghuadaily.Fragment;

import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.base.BaseRecyclerAdapter;
import com.example.tsinghuadaily.base.RecyclerViewHolder;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.nestedScroll.QMUIContinuousNestedBottomAreaBehavior;
import com.qmuiteam.qmui.nestedScroll.QMUIContinuousNestedBottomRecyclerView;
import com.qmuiteam.qmui.nestedScroll.QMUIContinuousNestedScrollLayout;
import com.qmuiteam.qmui.nestedScroll.QMUIContinuousNestedTopAreaBehavior;
import com.qmuiteam.qmui.nestedScroll.QMUIContinuousNestedTopWebView;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;
import com.qmuiteam.qmui.widget.tab.QMUITabSegment;
import com.qmuiteam.qmui.widget.webview.QMUIWebView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArticleDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArticleDetailFragment extends QMUIFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBarLayout;
    @BindView(R.id.pull_to_refresh)
    QMUIPullRefreshLayout mPullRefreshLayout;
    @BindView(R.id.coordinator)
    QMUIContinuousNestedScrollLayout mCoordinatorLayout;

    private QMUIWebView mNestedWebView;
    private RecyclerView mRecyclerView;
    private BaseRecyclerAdapter<String> mAdapter;

    @Override
    protected View onCreateView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_article_detail, null);
        ButterKnife.bind(this, view);
        initTopBar();
        initPullRefreshLayout();
        initCoordinatorLayout();
        mCoordinatorLayout.setDraggableScrollBarEnabled(true);
        return view;
    }

    private void initPullRefreshLayout(){
        mPullRefreshLayout.setOnPullListener(new QMUIPullRefreshLayout.OnPullListener() {
            @Override
            public void onMoveTarget(int offset) {

            }

            @Override
            public void onMoveRefreshView(int offset) {

            }

            @Override
            public void onRefresh() {
                mPullRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullRefreshLayout.finishRefresh();
                    }
                }, 3000);
            }
        });
    }


    private void initCoordinatorLayout() {
        mNestedWebView = new QMUIContinuousNestedTopWebView(getContext());
        int matchParent = ViewGroup.LayoutParams.MATCH_PARENT;
        CoordinatorLayout.LayoutParams webViewLp = new CoordinatorLayout.LayoutParams(
                matchParent, matchParent);
        webViewLp.setBehavior(new QMUIContinuousNestedTopAreaBehavior(getContext()));
        mCoordinatorLayout.setTopAreaView(mNestedWebView, webViewLp);

        mRecyclerView = new QMUIContinuousNestedBottomRecyclerView(getContext());
        CoordinatorLayout.LayoutParams recyclerViewLp = new CoordinatorLayout.LayoutParams(
                matchParent, matchParent);
        recyclerViewLp.setBehavior(new QMUIContinuousNestedBottomAreaBehavior());
        mCoordinatorLayout.setBottomAreaView(mRecyclerView, recyclerViewLp);

        mNestedWebView.loadUrl("https://mp.weixin.qq.com/s/zgfLOMD2JfZJKfHx-5BsBg");

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });

        mAdapter = new BaseRecyclerAdapter<String>(getContext(), null) {
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
                Toast.makeText(getContext(), "click position=" + pos, Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        onDataLoaded();
    }

    private void onDataLoaded() {
        List<String> data = new ArrayList<>(Arrays.asList("评论1", "评论2", "评论3", "评论4", "评论5"));
        Collections.shuffle(data);
        mAdapter.setData(data);
    }

    private void initTopBar() {
        mTopBarLayout.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });

        mTopBarLayout.setTitle("详情");
        mTopBarLayout.addRightTextButton("scroll", QMUIViewHelper.generateViewId())
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBottomSheet();
                    }
                });
    }

    private void showBottomSheet() {
        new QMUIBottomSheet.BottomListSheetBuilder(getContext())
                .addItem("scrollToBottom")
                .addItem("scrollToTop")
                .addItem("scrollBottomViewToTop")
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        switch (position) {
                            case 0:
                                mCoordinatorLayout.scrollToBottom();
                                break;
                            case 1:
                                mCoordinatorLayout.scrollToTop();
                                break;
                            case 2:
                                mCoordinatorLayout.scrollBottomViewToTop();
                        }
                        dialog.dismiss();
                    }
                })
                .build().show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mNestedWebView != null) {
            mCoordinatorLayout.removeView(mNestedWebView);
            mNestedWebView.destroy();
            mNestedWebView = null;
        }
    }
}