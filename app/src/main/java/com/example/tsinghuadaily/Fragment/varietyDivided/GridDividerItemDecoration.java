package com.example.tsinghuadaily.Fragment.varietyDivided;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tsinghuadaily.R;

import com.qmuiteam.qmui.util.QMUIResHelper;

public class GridDividerItemDecoration extends RecyclerView.ItemDecoration  {

    private Paint mDividerPaint = new Paint();
    private int mSpanCount;
    private final int mDividerAttr;

    public GridDividerItemDecoration(Context context, int spanCount) {
        this(context, spanCount, R.attr.qmui_skin_support_color_separator, 1f);
    }

    public GridDividerItemDecoration(Context context, int spanCount, int dividerColorAttr, float dividerWidth) {
        mSpanCount = spanCount;
        mDividerAttr = dividerColorAttr;
        mDividerPaint.setStrokeWidth(dividerWidth);
        mDividerPaint.setStyle(Paint.Style.STROKE);
        mDividerPaint.setColor(QMUIResHelper.getAttrColor(context, dividerColorAttr));
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int position = parent.getChildLayoutPosition(child);
            int column = (position + 1) % mSpanCount;

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int childBottom = child.getBottom() + params.bottomMargin +
                    Math.round(ViewCompat.getTranslationY(child));
            final int childRight = child.getRight() + params.rightMargin +
                    Math.round(ViewCompat.getTranslationX(child));

            if (childBottom < parent.getHeight()) {
                c.drawLine(child.getLeft(), childBottom, childRight, childBottom, mDividerPaint);
            }

            if (column < mSpanCount) {
                c.drawLine(childRight, child.getTop(), childRight, childBottom, mDividerPaint);
            }

        }
    }
}
