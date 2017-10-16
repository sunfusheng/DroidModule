package com.sunfusheng.base.widget.CollectionView;

import android.view.View;

import com.sunfusheng.viewobject.viewobject.ViewObject;

import java.util.List;

interface ICollectionView {

    void scrollVerticallyToPosition(int position, boolean anim);

    List<ViewObject> getList();

    IFooterAdapter getCommonAdapter();

    View getView();

    void setSpanCount(int spanCount);

    int getFirstVisibleItemIdx();

    int getLastVisibleItemIdx();

    void setFooterView(LoadMoreFooterView footerView, boolean showFooterAtTop);

    void addOnScrollListener(CollectionView.OnScrollListener onScrollListener);

    void removeOnScrollListener(CollectionView.OnScrollListener onScrollListener);

    void registerAdapterDataObserver(CollectionView.AdapterDataObserver adapterDataObserver);

    void unregisterAdapterDataObserver(CollectionView.AdapterDataObserver adapterDataObserver);
}
