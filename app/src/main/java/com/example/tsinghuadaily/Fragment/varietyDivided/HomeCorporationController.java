package com.example.tsinghuadaily.Fragment.varietyDivided;

import android.content.Context;

public class HomeCorporationController extends HomeController {

    public HomeCorporationController(Context context) {
        super(context);
    }

    @Override
    protected String getTitle() {
        return "Corporation";
    }

    @Override
    protected HomeController.ItemAdapter getItemAdapter() {
        return new HomeController.ItemAdapter(getContext(), VarietyDataManager.getInstance().getCorporationsDescriptions());
    }
}
