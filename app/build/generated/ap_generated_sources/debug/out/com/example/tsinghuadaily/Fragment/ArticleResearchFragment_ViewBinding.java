// Generated code from Butter Knife. Do not modify!
package com.example.tsinghuadaily.Fragment;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ArticleResearchFragment_ViewBinding implements Unbinder {
  private ArticleResearchFragment target;

  @UiThread
  public ArticleResearchFragment_ViewBinding(ArticleResearchFragment target, View source) {
    this.target = target;

    target.mTopBar = Utils.findRequiredViewAsType(source, R.id.topbar, "field 'mTopBar'", QMUITopBarLayout.class);
    target.sendBtn = Utils.findRequiredViewAsType(source, R.id.btnSearch, "field 'sendBtn'", Button.class);
    target.messageEdit = Utils.findRequiredViewAsType(source, R.id.searchKey, "field 'messageEdit'", EditText.class);
    target.mGroupListView = Utils.findRequiredViewAsType(source, R.id.searchArticleListView, "field 'mGroupListView'", QMUIGroupListView.class);
    target.scrollView = Utils.findRequiredViewAsType(source, R.id.searchScrollView, "field 'scrollView'", ScrollView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ArticleResearchFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTopBar = null;
    target.sendBtn = null;
    target.messageEdit = null;
    target.mGroupListView = null;
    target.scrollView = null;
  }
}
