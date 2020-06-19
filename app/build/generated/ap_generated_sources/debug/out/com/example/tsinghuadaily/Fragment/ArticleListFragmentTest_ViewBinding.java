// Generated code from Butter Knife. Do not modify!
package com.example.tsinghuadaily.Fragment;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.tsinghuadaily.R;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ArticleListFragmentTest_ViewBinding implements Unbinder {
  private ArticleListFragmentTest target;

  @UiThread
  public ArticleListFragmentTest_ViewBinding(ArticleListFragmentTest target, View source) {
    this.target = target;

    target.mGroupListView = Utils.findRequiredViewAsType(source, R.id.articleListView, "field 'mGroupListView'", QMUIGroupListView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ArticleListFragmentTest target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mGroupListView = null;
  }
}
