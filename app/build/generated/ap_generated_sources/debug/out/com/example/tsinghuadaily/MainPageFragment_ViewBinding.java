// Generated code from Butter Knife. Do not modify!
package com.example.tsinghuadaily;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.qmuiteam.qmui.widget.QMUIViewPager;
import com.qmuiteam.qmui.widget.tab.QMUITabSegment;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainPageFragment_ViewBinding implements Unbinder {
  private MainPageFragment target;

  @UiThread
  public MainPageFragment_ViewBinding(MainPageFragment target, View source) {
    this.target = target;

    target.mViewPager = Utils.findRequiredViewAsType(source, R.id.pager, "field 'mViewPager'", QMUIViewPager.class);
    target.mTabSegment = Utils.findRequiredViewAsType(source, R.id.tabs, "field 'mTabSegment'", QMUITabSegment.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MainPageFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mViewPager = null;
    target.mTabSegment = null;
  }
}
