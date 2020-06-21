package com.example.tsinghuadaily.Fragment.varietyDivided;

import com.example.tsinghuadaily.Fragment.Variety.CorporationArticleFragment;
import com.example.tsinghuadaily.Fragment.Variety.CorporationCardArticleFragment;
import com.example.tsinghuadaily.Fragment.Variety.DepartmentArticleFragment;
import com.example.tsinghuadaily.Fragment.Variety.DepartmentEconomicsFragment;
import com.example.tsinghuadaily.Fragment.Variety.DepartmentEngineerFragment;
import com.example.tsinghuadaily.Fragment.Variety.SchoolArticleListFragment;
import com.example.tsinghuadaily.Fragment.Variety.SchoolDepartmentFragment;
import com.example.tsinghuadaily.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class VarietyDataManager {
    private static VarietyDataManager _sInstance;

    private List<Class<? extends BaseFragment>> mSchoolsNames;
    private List<Class<? extends BaseFragment>> mDepartmentsNames;
    private List<Class<? extends BaseFragment>> mCorporationsNames;

    private WidgetContainer mWidgetContainer;

    public VarietyDataManager() {
        mWidgetContainer = WidgetContainer.getInstance();
        initSchoolsDesc();
        initDepartmentsDesc();
        initCorporationsDesc();
    }

    public static VarietyDataManager getInstance() {
        if (_sInstance == null) {
            _sInstance = new VarietyDataManager();
        }
        return _sInstance;
    }


    private void initSchoolsDesc() {
        mSchoolsNames = new ArrayList<>();
        mSchoolsNames.add(SchoolArticleListFragment.class);
        mSchoolsNames.add(SchoolDepartmentFragment.class);
    }

    private void initDepartmentsDesc() {
        mDepartmentsNames = new ArrayList<>();
        mDepartmentsNames.add(DepartmentArticleFragment.class);
        mDepartmentsNames.add(DepartmentEconomicsFragment.class);
        mDepartmentsNames.add(DepartmentEngineerFragment.class);
    }

    private void initCorporationsDesc() {
        mCorporationsNames = new ArrayList<>();
        mCorporationsNames.add(CorporationArticleFragment.class);
        mCorporationsNames.add(CorporationCardArticleFragment.class);
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

    public List<ItemDescription> getDepartmentsDescriptions() {
        List<ItemDescription> list = new ArrayList<>();
        for (int i = 0; i < mDepartmentsNames.size(); i++) {
            list.add(mWidgetContainer.get(mDepartmentsNames.get(i)));
        }
        return list;
    }

    public List<ItemDescription> getCorporationsDescriptions() {
        List<ItemDescription> list = new ArrayList<>();
        for (int i = 0; i < mCorporationsNames.size(); i++) {
            list.add(mWidgetContainer.get(mCorporationsNames.get(i)));
        }
        return list;
    }

}
