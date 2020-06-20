// Generated code from Butter Knife. Do not modify!
package com.example.tsinghuadaily.Fragment;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.tsinghuadaily.R;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AuthFragment_ViewBinding implements Unbinder {
  private AuthFragment target;

  @UiThread
  public AuthFragment_ViewBinding(AuthFragment target, View source) {
    this.target = target;

    target.AuthRequestList = Utils.findRequiredViewAsType(source, R.id.authReqList, "field 'AuthRequestList'", RecyclerView.class);
    target.PullRefreshLayout = Utils.findRequiredViewAsType(source, R.id.pullRefresh, "field 'PullRefreshLayout'", QMUIPullRefreshLayout.class);
    target.mTopBar = Utils.findRequiredViewAsType(source, R.id.topbarAuthPage, "field 'mTopBar'", QMUITopBarLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AuthFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.AuthRequestList = null;
    target.PullRefreshLayout = null;
    target.mTopBar = null;
  }
}
