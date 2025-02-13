// Generated code from Butter Knife. Do not modify!
package com.example.tsinghuadaily.Fragment;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.viewpager.widget.ViewPager;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.tsinghuadaily.R;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.tab.QMUITabSegment;
import java.lang.IllegalStateException;
import java.lang.Override;

public class GridVarietyFragment_ViewBinding implements Unbinder {
  private GridVarietyFragment target;

  @UiThread
  public GridVarietyFragment_ViewBinding(GridVarietyFragment target, View source) {
    this.target = target;

    target.mTopBar = Utils.findRequiredViewAsType(source, R.id.topbarMainPage, "field 'mTopBar'", QMUITopBarLayout.class);
    target.mTabSegment = Utils.findRequiredViewAsType(source, R.id.tabSegment, "field 'mTabSegment'", QMUITabSegment.class);
    target.mContentViewPager = Utils.findRequiredViewAsType(source, R.id.contentViewPager, "field 'mContentViewPager'", ViewPager.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    GridVarietyFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTopBar = null;
    target.mTabSegment = null;
    target.mContentViewPager = null;
  }
}
