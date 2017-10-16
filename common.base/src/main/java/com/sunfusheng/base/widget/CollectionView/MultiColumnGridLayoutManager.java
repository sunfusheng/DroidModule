package com.sunfusheng.base.widget.CollectionView;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

import com.sunfusheng.viewobject.RecyclerViewVOAdapter;

class MultiColumnGridLayoutManager extends GridLayoutManager {

    private boolean isScrollEnabled = true;

    public MultiColumnGridLayoutManager(Context context, int spanCount, RecyclerViewVOAdapter adapter) {
        super(context, spanCount);

        setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter == null || position < 0 || position >= adapter.getItemCount() || adapter.getViewObject(position) == null) {
                    return getSpanCount();
                }

                return adapter.getViewObject(position).getSpanSize();
            }
        });
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }
}
