// Generated code from Butter Knife. Do not modify!
package com.example.tsinghuadaily.Fragment;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.tsinghuadaily.R;
import com.qmuiteam.qmui.nestedScroll.QMUIContinuousNestedScrollLayout;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ArticleDetailFragment_ViewBinding implements Unbinder {
  private ArticleDetailFragment target;

  @UiThread
  public ArticleDetailFragment_ViewBinding(ArticleDetailFragment target, View source) {
    this.target = target;

    target.mTopBarLayout = Utils.findRequiredViewAsType(source, R.id.topbar, "field 'mTopBarLayout'", QMUITopBarLayout.class);
    target.mPullRefreshLayout = Utils.findRequiredViewAsType(source, R.id.pull_to_refresh, "field 'mPullRefreshLayout'", QMUIPullRefreshLayout.class);
    target.mCoordinatorLayout = Utils.findRequiredViewAsType(source, R.id.coordinator, "field 'mCoordinatorLayout'", QMUIContinuousNestedScrollLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ArticleDetailFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTopBarLayout = null;
    target.mPullRefreshLayout = null;
    target.mCoordinatorLayout = null;
  }
}
