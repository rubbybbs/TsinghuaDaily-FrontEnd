package com.example.tsinghuadaily.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tsinghuadaily.Activity.ArticleEditActivity;
import com.example.tsinghuadaily.Activity.ArticleSearchActivity;
import com.example.tsinghuadaily.Fragment.varietyDivided.HomeController;
import com.example.tsinghuadaily.Fragment.varietyDivided.HomeCorporationController;
import com.example.tsinghuadaily.Fragment.varietyDivided.HomeDepartmentController;
import com.example.tsinghuadaily.Fragment.varietyDivided.HomeSchoolController;
import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.base.BaseFragment;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
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
public class GridVarietyFragment extends QMUIFragment {
    @SuppressWarnings("FieldCanBeLocal") private final int TAB_COUNT = 3;

    @BindView(R.id.topbarMainPage)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.tabSegment)
    QMUITabSegment mTabSegment;
    @BindView(R.id.contentViewPager)
    ViewPager mContentViewPager;

    private Map<GridVarietyFragment.ContentPage, HomeController> mPages = new HashMap<>();
    private GridVarietyFragment.ContentPage mDestPage = GridVarietyFragment.ContentPage.Item1;
    private int mCurrentItemCount = TAB_COUNT;


    private PagerAdapter mPagerAdapter = new PagerAdapter() {

        private int mChildCount = 0;

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return mPages.size();
        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            HomeController page = mPages.get(GridVarietyFragment.ContentPage.getPage(position));
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(page, params);
            return page;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            if (mChildCount == 0) {
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
        }

        @Override
        public void notifyDataSetChanged() {
            mChildCount = getCount();
            super.notifyDataSetChanged();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toast.makeText(getContext(), "started by scheme", Toast.LENGTH_SHORT).show();

//        Bundle args = getArguments();
//        if(args != null){
//            int mode = args.getInt("mode");
//            Toast.makeText(getContext(), "mode = " + mode, Toast.LENGTH_SHORT).show();
//        }

    }


    @Override
    protected View onCreateView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_grid_variety, null);
        ButterKnife.bind(this, rootView);

        initTopBar();
        initTabs();
        initPagers();

        return rootView;
    }

    private void initTopBar() {
        mTopBar.setTitle("分类");
        mTopBar.addRightTextButton("S", QMUIViewHelper.generateViewId())
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        search();
                    }
                });
    }

    private void search() {
        Intent intent = new Intent(getActivity(), ArticleSearchActivity.class);
        startActivity(intent);
    }

    private void initTabs() {
        mContentViewPager.setAdapter(mPagerAdapter);
        mContentViewPager.setCurrentItem(mDestPage.getPosition(), false);
        QMUITabBuilder tabBuilder = mTabSegment.tabBuilder();
        String title[]={"           学校            ","           院系           ","           社团           "};
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

    private void initPagers() {
        HomeController.HomeControlListener listener = new HomeController.HomeControlListener() {
            @Override
            public void startFragment(BaseFragment fragment) {
                GridVarietyFragment.this.startFragment(fragment);
            }
        };

        mPages = new HashMap<>();

        HomeController homeSchoolsController = new HomeSchoolController(getActivity());
        homeSchoolsController.setHomeControlListener(listener);
        mPages.put(ContentPage.Item1, homeSchoolsController);

        HomeController homeDepartmentsController = new HomeDepartmentController(getActivity());
        homeDepartmentsController.setHomeControlListener(listener);
        mPages.put(ContentPage.Item2, homeDepartmentsController);

        HomeController homeCorporationsController = new HomeCorporationController(getActivity());
        homeCorporationsController.setHomeControlListener(listener);
        mPages.put(ContentPage.Item3, homeCorporationsController);

        mContentViewPager.setAdapter(mPagerAdapter);
        mTabSegment.setupWithViewPager(mContentViewPager, false);
    }



    public enum ContentPage {
        Item1(0),
        Item2(1),
        Item3(2);
        private final int position;

        ContentPage(int pos) {
            position = pos;
        }

        public static GridVarietyFragment.ContentPage getPage(int position) {
            switch (position) {
                case 0:
                    return Item1;
                case 1:
                    return Item2;
                case 2:
                default:
                    return Item3;
            }
        }

        public int getPosition() {
            return position;
        }
    }
}