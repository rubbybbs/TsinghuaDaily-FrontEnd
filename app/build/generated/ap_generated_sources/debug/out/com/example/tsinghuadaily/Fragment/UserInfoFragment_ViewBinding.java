// Generated code from Butter Knife. Do not modify!
package com.example.tsinghuadaily.Fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.tsinghuadaily.R;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import java.lang.IllegalStateException;
import java.lang.Override;

public class UserInfoFragment_ViewBinding implements Unbinder {
  private UserInfoFragment target;

  @UiThread
  public UserInfoFragment_ViewBinding(UserInfoFragment target, View source) {
    this.target = target;

    target.avatar = Utils.findRequiredViewAsType(source, R.id.img_avatar, "field 'avatar'", ImageView.class);
    target.tvUsername = Utils.findRequiredViewAsType(source, R.id.tv_username, "field 'tvUsername'", TextView.class);
    target.mTopBar = Utils.findRequiredViewAsType(source, R.id.topbarInfoPage, "field 'mTopBar'", QMUITopBarLayout.class);
    target.userInfoRecylerView = Utils.findRequiredViewAsType(source, R.id.info_recyler_view, "field 'userInfoRecylerView'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    UserInfoFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.avatar = null;
    target.tvUsername = null;
    target.mTopBar = null;
    target.userInfoRecylerView = null;
  }
}
