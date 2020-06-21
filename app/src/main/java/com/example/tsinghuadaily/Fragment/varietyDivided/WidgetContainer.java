package com.example.tsinghuadaily.Fragment.varietyDivided;

import com.example.tsinghuadaily.Fragment.Variety.CorporationArticleFragment;
import com.example.tsinghuadaily.Fragment.Variety.CorporationCardArticleFragment;
import com.example.tsinghuadaily.Fragment.Variety.DepartmentArticleFragment;
import com.example.tsinghuadaily.Fragment.Variety.DepartmentEconomicsFragment;
import com.example.tsinghuadaily.Fragment.Variety.DepartmentEngineerFragment;
import com.example.tsinghuadaily.Fragment.Variety.SchoolArticleListFragment;
import com.example.tsinghuadaily.Fragment.Variety.SchoolDepartmentFragment;
import com.example.tsinghuadaily.R;
import com.example.tsinghuadaily.base.BaseFragment;

import java.util.HashMap;
import java.util.Map;

class WidgetContainer {
    private static WidgetContainer _sInstance = new WidgetContainer();

    private Map<Class<? extends BaseFragment>, ItemDescription> mWidgets;

    private WidgetContainer() {
        mWidgets = new HashMap<>();
        mWidgets.put(SchoolArticleListFragment.class, new ItemDescription(SchoolArticleListFragment.class, "校长办公室", R.mipmap.ic_avatar, ""));
        mWidgets.put(SchoolDepartmentFragment.class, new ItemDescription(SchoolDepartmentFragment.class, "教务处", R.mipmap.ic_avatar, ""));

        mWidgets.put(DepartmentArticleFragment.class, new ItemDescription(DepartmentArticleFragment.class, "软件学院", R.mipmap.ic_avatar, ""));
        mWidgets.put(DepartmentEconomicsFragment.class, new ItemDescription(DepartmentEconomicsFragment.class, "经管学院", R.mipmap.ic_avatar, ""));
        mWidgets.put(DepartmentEngineerFragment.class, new ItemDescription(DepartmentEngineerFragment.class, "电子工程", R.mipmap.ic_avatar, ""));

        mWidgets.put(CorporationArticleFragment.class, new ItemDescription(CorporationArticleFragment.class, "围棋社", R.mipmap.ic_avatar, ""));
        mWidgets.put(CorporationCardArticleFragment.class, new ItemDescription(CorporationCardArticleFragment.class, "桥牌社", R.mipmap.ic_avatar, ""));

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
