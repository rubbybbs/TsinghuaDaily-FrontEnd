package com.example.tsinghuadaily.Fragment.varietyDivided;

import android.content.Context;

public class HomeSchoolController extends HomeController {

    public HomeSchoolController(Context context) {
        super(context);
    }

    @Override
    protected String getTitle() {
        return "School";
    }

    @Override
    protected ItemAdapter getItemAdapter() {
        return new ItemAdapter(getContext(), VarietyDataManager.getInstance().getSchoolsDescriptions());
    }
}