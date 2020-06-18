package com.example.tsinghuadaily.Fragment.varietyDivided;

import com.example.tsinghuadaily.Fragment.SchoolArticleListFragment;
import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.base.BaseFragment;

import java.util.HashMap;
import java.util.Map;

class WidgetContainer {
    private static WidgetContainer _sInstance = new WidgetContainer();

    private Map<Class<? extends BaseFragment>, ItemDescription> mWidgets;

    private WidgetContainer() {
        mWidgets = new HashMap<>();
        mWidgets.put(SchoolArticleListFragment.class, new ItemDescription(SchoolArticleListFragment.class, "清华大学", R.mipmap.ic_launcher, ""));
    }

    public static WidgetContainer getInstance() {
        if (_sInstance == null) {
            _sInstance = new WidgetContainer();
        }
        return _sInstance;
    }

    public ItemDescription get(Class<? extends BaseFragment> fragment) {
        return mWidgets.get(fragment);
    }
}
