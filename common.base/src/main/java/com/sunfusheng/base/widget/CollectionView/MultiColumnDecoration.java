package com.sunfusheng.base.widget.CollectionView;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sunfusheng.viewobject.RecyclerViewVOAdapter;
import com.sunfusheng.viewobject.viewobject.ViewObject;

class MultiColumnDecoration extends RecyclerView.ItemDecoration {

    private RecyclerViewVOAdapter recyclerViewAdapter;
    private int spanCount = 1;

    public MultiColumnDecoration(RecyclerViewVOAdapter adapter, int spanCount) {
        super();

        this.recyclerViewAdapter = adapter;
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (recyclerViewAdapter == null) {
            return;
        }

        int position = parent.getChildAdapterPosition(view);
        if (position == -1) {
            return;
        }

        ViewObject viewObject = recyclerViewAdapter.getViewObject(position);
        if (viewObject == null || viewObject.getSideMarginForMultiColumn() == 0) {
            return;
        }

        if (viewObject.getSpanSize() == spanCount) {
            return;
        }

        calcOutRect(position, viewObject.getSideMarginForMultiColumn(), outRect);
    }

    private void calcOutRect(int position, int marginSide, Rect outRect) {
        int startPosition = position - 1;

        while (startPosition >= 0) {
            ViewObject viewObject = recyclerViewAdapter.getViewObject(startPosition);
            if (viewObject == null || viewObject.getSpanSize() == spanCount) {
                break;
            }
            startPosition--;
        }

        int offset = position - startPosition - 1;
        if (offset % spanCount == 0) {
            outRect.set(marginSide, 0, 0, 0);
        } else if (offset % spanCount == spanCount - 1) {
            outRect.set(0, 0, marginSide, 0);
        } else {
            outRect.set(marginSide / 2, 0, marginSide / 2, 0);
        }
    }
}
