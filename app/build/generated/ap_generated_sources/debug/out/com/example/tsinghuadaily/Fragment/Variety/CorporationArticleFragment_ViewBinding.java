// Generated code from Butter Knife. Do not modify!
package com.example.tsinghuadaily.Fragment.Variety;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.tsinghuadaily.R;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CorporationArticleFragment_ViewBinding implements Unbinder {
  private CorporationArticleFragment target;

  @UiThread
  public CorporationArticleFragment_ViewBinding(CorporationArticleFragment target, View source) {
    this.target = target;

    target.mGroupListView = Utils.findRequiredViewAsType(source, R.id.articleListView, "field 'mGroupListView'", QMUIGroupListView.class);
    target.mTopBar = Utils.findRequiredViewAsType(source, R.id.topbar, "field 'mTopBar'", QMUITopBarLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    CorporationArticleFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mGroupListView = null;
    target.mTopBar = null;
  }
}
