package com.example.tsinghuadaily.Activity;

import com.example.tsinghuadaily.Fragment.MainPageFragment;
import com.example.tsinghuadaily.Fragment.MessageFragment;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.arch.first.FirstFragmentFinder;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Override;
import java.util.HashMap;
import java.util.Map;

public class MainPageActivity_FragmentFinder implements FirstFragmentFinder {
  private Map<Class<? extends QMUIFragment>, Integer> mClassToIdMap;

  private Map<Integer, Class<? extends QMUIFragment>> mIdToClassMap;

  public MainPageActivity_FragmentFinder() {
    mClassToIdMap = new HashMap<>();
    mIdToClassMap = new HashMap<>();
    mClassToIdMap.put(MainPageFragment.class, 100);
    mIdToClassMap.put(100, MainPageFragment.class);
    mClassToIdMap.put(MessageFragment.class, 101);
    mIdToClassMap.put(101, MessageFragment.class);
  }

  @Override
  public Class<? extends QMUIFragment> getFragmentClassById(int arg0) {
    return mIdToClassMap.get(arg0);
  }

  @Override
  public int getIdByFragmentClass(Class<? extends QMUIFragment> arg0) {
    Integer id = mClassToIdMap.get(arg0);
    return id != null ? id :FirstFragmentFinder.NO_ID;
  }
}
