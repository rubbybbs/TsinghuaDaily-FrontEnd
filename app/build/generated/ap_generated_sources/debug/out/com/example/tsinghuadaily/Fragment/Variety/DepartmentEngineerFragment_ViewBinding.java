// Generated code from Butter Knife. Do not modify!
package com.example.tsinghuadaily.Fragment.Variety;

import android.view.View;
import android.widget.ScrollView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.tsinghuadaily.R;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class DepartmentEngineerFragment_ViewBinding implements Unbinder {
  private DepartmentEngineerFragment target;

  @UiThread
  public DepartmentEngineerFragment_ViewBinding(DepartmentEngineerFragment target, View source) {
    this.target = target;

    target.mGroupListView = Utils.findRequiredViewAsType(source, R.id.articleListView, "field 'mGroupListView'", QMUIGroupListView.class);
    target.mTopBar = Utils.findRequiredViewAsType(source, R.id.topbar, "field 'mTopBar'", QMUITopBarLayout.class);
    target.scrollView = Utils.findRequiredViewAsType(source, R.id.scrollView, "field 'scrollView'", ScrollView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    DepartmentEngineerFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mGroupListView = null;
    target.mTopBar = null;
    target.scrollView = null;
  }
}
