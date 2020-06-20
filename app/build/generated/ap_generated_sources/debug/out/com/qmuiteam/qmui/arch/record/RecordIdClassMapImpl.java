package com.qmuiteam.qmui.arch.record;

import com.example.tsinghuadaily.Activity.MainPageActivity;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Override;
import java.util.HashMap;
import java.util.Map;

public class RecordIdClassMapImpl implements RecordIdClassMap {
  private Map<Class, Integer> mClassToIdMap;

  private Map<Integer, Class> mIdToClassMap;

  public RecordIdClassMapImpl() {
    mClassToIdMap = new HashMap<>();
    mIdToClassMap = new HashMap<>();
    mClassToIdMap.put(MainPageActivity.class, 1092979511);
    mIdToClassMap.put(1092979511, MainPageActivity.class);
  }

  @Override
  public Class<?> getRecordClassById(int arg0) {
    return mIdToClassMap.get(arg0);
  }

  @Override
  public int getIdByRecordClass(Class<?> arg0) {
    return mClassToIdMap.get(arg0);
  }
}
