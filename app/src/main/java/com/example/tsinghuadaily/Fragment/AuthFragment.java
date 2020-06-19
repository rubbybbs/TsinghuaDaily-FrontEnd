package com.example.tsinghuadaily.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tsinghuadaily.R;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AuthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AuthFragment extends QMUIFragment {

    @BindView(R.id.recycleListAuth)
    RecyclerView AuthRequestList;
    @BindView(R.id.pullRefresh)
    QMUIPullRefreshLayout PullRefreshLayout;
    @BindView(R.id.topbarAuthPage)
    QMUITopBarLayout mTopBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(getContext(), "started by scheme", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected View onCreateView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_message, null);
        ButterKnife.bind(this, rootView);
        initTopBar();
        PullRefreshLayout.setEnabled(false);

        return rootView;
    }


    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle("认证列表");
    }


    private class AuthType {
        Bitmap idCard;
        String username;
        String id;
        String dept_name;
        String identity;
    }

}