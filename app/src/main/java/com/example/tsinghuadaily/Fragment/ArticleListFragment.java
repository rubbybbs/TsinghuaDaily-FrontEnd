package com.example.tsinghuadaily.Fragment;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tsinghuadaily.R;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArticleListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArticleListFragment extends Fragment {

    @BindView(R.id.articleListView)
    QMUIGroupListView mGroupListView;

    public ArticleListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_article_list, container, false);
        ButterKnife.bind(this, root);

        initGroupListView();

        return root;
    }

    private void initGroupListView() {
        int height = QMUIDisplayHelper.dp2px(getContext(), 56);

        QMUICommonListItemView itemWithDetailBelowWithChevronWithIcon = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher_round),
                "Item 7",
                "在标题下方的详细信息",
                QMUICommonListItemView.VERTICAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON,
                height);

        QMUICommonListItemView itemRedPoint = mGroupListView.createItemView(
                ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher_round),
                "Item 7",
                "在标题下方的详细信息",
                QMUICommonListItemView.VERTICAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON,
                height);
        itemRedPoint.setTipPosition(QMUICommonListItemView.TIP_POSITION_RIGHT);
        itemRedPoint.showRedDot(true);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof QMUICommonListItemView) {
                    CharSequence text = ((QMUICommonListItemView) v).getText();
                    Toast.makeText(getActivity(), text + " is Clicked", Toast.LENGTH_SHORT).show();
                    if (((QMUICommonListItemView) v).getAccessoryType() == QMUICommonListItemView.ACCESSORY_TYPE_SWITCH) {
                        ((QMUICommonListItemView) v).getSwitch().toggle();
                    }
                }
            }
        };


        int size = QMUIDisplayHelper.dp2px(getContext(), 20);
        QMUIGroupListView.newSection(getContext())
                .setTitle("Section 1: 默认样式")
                .setDescription("Section 1 的描述")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                .addItemView(itemWithDetailBelowWithChevronWithIcon, onClickListener)
                .setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(getContext(), 16), 0)
                .addTo(mGroupListView);

        QMUIGroupListView.newSection(getContext())
                .setTitle("Section 2: 红点/new 提示")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                .addItemView(itemRedPoint, onClickListener)
                .setOnlyShowStartEndSeparator(true)
                .addTo(mGroupListView);
    }



}