package com.example.tsinghuadaily.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import android.content.Intent;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tsinghuadaily.Activity.ArticleEditActivity;
import com.example.tsinghuadaily.R;
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
    @SuppressWarnings("FieldCanBeLocal") private final int TAB_COUNT = 4;

    @BindView(R.id.topbarMainPage) QMUITopBarLayout mTopBar;
    @BindView(R.id.tabSegment) QMUITabSegment mTabSegment;
    @BindView(R.id.contentViewPager) ViewPager mContentViewPager;

    private Map<ContentPage, View> mPageMap = new HashMap<>();
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(getContext(), "started by scheme", Toast.LENGTH_SHORT).show();

        Bundle args = getArguments();
        if(args != null){
            int mode = args.getInt("mode");
            Toast.makeText(getContext(), "mode = " + mode, Toast.LENGTH_SHORT).show();
        }

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
                        research();
                    }
                });
    }

    private void initTabAndPager() {
        mContentViewPager.setAdapter(mPagerAdapter);
        mContentViewPager.setCurrentItem(mDestPage.getPosition(), false);
        QMUITabBuilder tabBuilder = mTabSegment.tabBuilder();
        String title[]={"      关注      ","      学校      ","      院系      ","      社团      "};
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
                Toast.makeText(getContext(), "select index " + index, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(int index) {
                Toast.makeText(getContext(), "unSelect index " + index, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabReselected(int index) {
                Toast.makeText(getContext(), "reSelect index " + index, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDoubleTap(int index) {
                Toast.makeText(getContext(), "double tap index " + index, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void research() {
        Intent intent = new Intent(getActivity(), ArticleEditActivity.class);
        startActivity(intent);
    }

    private View getPageView(ContentPage page) {
        View view = mPageMap.get(page);
        if (view == null) {
            QMUIGroupListView listView = new QMUIGroupListView(getContext());
            initGroupListView(listView);
            view = listView;
            mPageMap.put(page, view);
        }
        return view;
    }

    private void initGroupListView(QMUIGroupListView mGroupListView) {
        int height = QMUIDisplayHelper.dp2px(getContext(), 56);

        QMUICommonListItemView itemWithDetailBelowWithChevronWithIcon = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher_round),
                "标题 1",
                "在标题下方的详细信息",
                QMUICommonListItemView.VERTICAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON,
                height);

        QMUICommonListItemView item2 = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher_round),
                "标题 2",
                "在标题下方的详细信息",
                QMUICommonListItemView.VERTICAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON,
                height);

        QMUICommonListItemView item3 = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher_round),
                "标题 3",
                "在标题下方的详细信息",
                QMUICommonListItemView.VERTICAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON,
                height);

        QMUICommonListItemView itemRedPoint = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher_round),
                "标题 4",
                "在标题下方的详细信息",
                QMUICommonListItemView.VERTICAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON,
                height);
        itemRedPoint.setTipPosition(QMUICommonListItemView.TIP_POSITION_RIGHT);
        itemRedPoint.showRedDot(true);

        QMUICommonListItemView item5 = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher_round),
                "标题 5",
                "在标题下方的详细信息",
                QMUICommonListItemView.VERTICAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON,
                height);

        QMUICommonListItemView item6 = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher_round),
                "标题 6",
                "在标题下方的很长很长很长很长很长很长很长很长很长很长很长很长很长的详细信息",
                QMUICommonListItemView.VERTICAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int paddingVer = QMUIDisplayHelper.dp2px(getContext(), 12);
        item6.setPadding(item6.getPaddingLeft(), paddingVer,
                item6.getPaddingRight(), paddingVer);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof QMUICommonListItemView) {
                    CharSequence text = ((QMUICommonListItemView) v).getText();
                    Toast.makeText(getActivity(), text + " is Clicked", Toast.LENGTH_SHORT).show();
                    if (((QMUICommonListItemView) v).getAccessoryType() == QMUICommonListItemView.ACCESSORY_TYPE_SWITCH) {
                        ((QMUICommonListItemView) v).getSwitch().toggle();
                    }

                    ArticleDetailFragment fragment = new ArticleDetailFragment();
                    startFragment(fragment);
                }
            }
        };


        int size = QMUIDisplayHelper.dp2px(getContext(), 20);
        QMUIGroupListView.newSection(getContext())
                .setTitle("Section 1: 默认样式")
                .setDescription("Section 1 的描述")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                .addItemView(itemWithDetailBelowWithChevronWithIcon, onClickListener)
                .addItemView(item2, onClickListener)
                .addItemView(item3, onClickListener)
                .setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(getContext(), 16), 0)
                .addTo(mGroupListView);

        QMUIGroupListView.newSection(getContext())
                .setTitle("Section 2: 红点/new 提示")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                .addItemView(itemRedPoint, onClickListener)
                .addItemView(item5, onClickListener)
                .addItemView(item6, onClickListener)
                .setOnlyShowStartEndSeparator(true)
                .addTo(mGroupListView);
    }

    public enum ContentPage {
        Item1(0),
        Item2(1),
        Item3(2),
        Item4(3);
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
                default:
                    return Item4;
            }
        }

        public int getPosition() {
            return position;
        }
    }
}
