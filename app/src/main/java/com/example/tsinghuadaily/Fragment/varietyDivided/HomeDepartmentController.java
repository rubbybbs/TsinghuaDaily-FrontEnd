package com.example.tsinghuadaily.Fragment.varietyDivided;

import android.content.Context;

public class HomeDepartmentController extends HomeController {

    public HomeDepartmentController(Context context) {
        super(context);
    }

    @Override
    protected String getTitle() {
        return "Department";
    }

    @Override
    protected ItemAdapter getItemAdapter() {
        return new ItemAdapter(getContext(), VarietyDataManager.getInstance().getDepartmentsDescriptions());
    }
}