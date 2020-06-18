package com.example.tsinghuadaily.Fragment.varietyDivided;

import com.example.tsinghuadaily.base.BaseFragment;

public class ItemDescription {
    private Class<? extends BaseFragment> mClass;
    private String mName;
    private int mIconRes;
    private String mDocUrl;

    public ItemDescription(Class<? extends BaseFragment> kitClass, String kitName){
        this(kitClass, kitName, 0, "");
    }


    public ItemDescription(Class<? extends BaseFragment> kitClass, String kitName, int iconRes, String docUrl) {
        mClass = kitClass;
        mName = kitName;
        mIconRes = iconRes;
        mDocUrl = docUrl;
    }

    public Class<? extends BaseFragment> getMClass() {
        return mClass;
    }

    public String getName() {
        return mName;
    }

    public int getIconRes() {
        return mIconRes;
    }

    public String getDocUrl() {
        return mDocUrl;
    }
}
