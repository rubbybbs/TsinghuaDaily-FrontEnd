package com.example.tsinghuadaily.Fragment.varietyDivided;

import com.example.tsinghuadaily.Fragment.SchoolArticleListFragment;
import com.example.tsinghuadaily.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class VarietyDataManager {
    private static VarietyDataManager _sInstance;

    private List<Class<? extends BaseFragment>> mSchoolsNames;

    private WidgetContainer mWidgetContainer;

    public VarietyDataManager() {
        mWidgetContainer = WidgetContainer.getInstance();
        initSchoolsDesc();
    }

    public static VarietyDataManager getInstance() {
        if (_sInstance == null) {
            _sInstance = new VarietyDataManager();
        }
        return _sInstance;
    }


    /**
     * School
     */
    private void initSchoolsDesc() {
        mSchoolsNames = new ArrayList<>();
        mSchoolsNames.add(SchoolArticleListFragment.class);

    }


    public ItemDescription getDescription(Class<? extends BaseFragment> cls) {
        return mWidgetContainer.get(cls);
    }

    public String getName(Class<? extends BaseFragment> cls) {
        ItemDescription itemDescription = getDescription(cls);
        if (itemDescription == null) {
            return null;
        }
        return itemDescription.getName();
    }

    public String getDocUrl(Class<? extends BaseFragment> cls) {
        ItemDescription itemDescription = getDescription(cls);
        if (itemDescription == null) {
            return null;
        }
        return itemDescription.getDocUrl();
    }

    public List<ItemDescription> getSchoolsDescriptions() {
        List<ItemDescription> list = new ArrayList<>();
        for (int i = 0; i < mSchoolsNames.size(); i++) {
            list.add(mWidgetContainer.get(mSchoolsNames.get(i)));
        }
        return list;
    }

}
