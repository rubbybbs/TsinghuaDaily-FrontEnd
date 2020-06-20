// Generated code from Butter Knife. Do not modify!
package com.example.tsinghuadaily.Fragment.varietyDivided;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.tsinghuadaily.R;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import java.lang.IllegalStateException;
import java.lang.Override;

public class HomeController_ViewBinding implements Unbinder {
  private HomeController target;

  @UiThread
  public HomeController_ViewBinding(HomeController target) {
    this(target, target);
  }

  @UiThread
  public HomeController_ViewBinding(HomeController target, View source) {
    this.target = target;

    target.mTopBar = Utils.findRequiredViewAsType(source, R.id.topbar, "field 'mTopBar'", QMUITopBarLayout.class);
    target.mRecyclerView = Utils.findRequiredViewAsType(source, R.id.recyclerView, "field 'mRecyclerView'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    HomeController target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTopBar = null;
    target.mRecyclerView = null;
  }
}
