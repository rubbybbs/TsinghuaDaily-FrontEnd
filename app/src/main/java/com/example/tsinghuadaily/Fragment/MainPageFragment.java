package com.example.tsinghuadaily.Fragment;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.base.BaseFragment;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.arch.QMUIFragmentPagerAdapter;
import com.qmuiteam.qmui.widget.QMUIViewPager;
import com.qmuiteam.qmui.widget.tab.QMUITabSegment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainPageFragment extends BaseFragment {
    @BindView(R.id.pager)
    QMUIViewPager mViewPager;
    @BindView(R.id.tabs)
    QMUITabSegment mTabSegment;

    private boolean isAdmin;

    @Override
    protected View onCreateView() {
        FrameLayout layout = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main_page, null);
        ButterKnife.bind(this, layout);
        isAdmin = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE).getBoolean("authority", false);
        initPagers();
        return layout;
    }

    private void initPagers() {
        QMUIFragmentPagerAdapter pagerAdapter = new QMUIFragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public QMUIFragment createFragment(int position) {
                if (!isAdmin)
                {
                    switch (position) {
                        case 0:
                            return new TabScrollableFragment();
                        case 1:
                            return new GridVarietyFragment();
                        case 2:
                            return new MessageFragment();
                        case 3:
                            return new UserInfoFragment();
                        default:
                            return new TabScrollableFragment();
                    }
                }
                else
                {
                    switch (position) {
                        case 0:
                            return new TabScrollableFragment();
                        case 1:
                            return new GridVarietyFragment();
                        case 2:
                            return new MessageFragment();
                        case 3:
                            return new MessageFragment();
                        case 4:
                            return new UserInfoFragment();
                        default:
                            return new TabScrollableFragment();
                    }
                }
            }

            @Override
            public int getCount() {
                if (!isAdmin)
                    return 4;
                else
                    return 5;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if (!isAdmin)
                {
                    switch (position) {
                        case 0:
                            return "主页";
                        case 1:
                            return "分类";
                        case 2:
                            return "消息";
                        case 3:
                        default:
                            return "我的";
                    }
                }
                else
                {
                    switch (position) {
                        case 0:
                            return "主页";
                        case 1:
                            return "分类";
                        case 2:
                            return "认证";
                        case 3:
                            return "消息";
                        default:
                            return "我的";
                    }
                }
            }
        };
        mViewPager.setAdapter(pagerAdapter);
        mTabSegment.setupWithViewPager(mViewPager);
    }
}
