package com.sunfusheng.base.widget.CollectionView;


import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.sunfusheng.utils.actions.ActionDispatcher;
import com.sunfusheng.viewobject.viewobject.ViewObject;

import java.util.List;

class ListViewCollection extends ListView implements ICollectionView {

    private ListViewFooterAdapter mAdapter;
    private ActionDispatcher<CollectionView.OnScrollListener> scrollActionDispatcher = new ActionDispatcher<>();
    private ActionDispatcher<CollectionView.AdapterDataObserver> adapterDataObservableDispatcher = new ActionDispatcher<>();
    private int firstVisibleItemIdx = 0;
    private int lastVisibleItemIdx = 0;

    public ListViewCollection(Context context) {
        this(context, null);
    }

    public ListViewCollection(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListViewCollection(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setDividerHeight(0);
        mAdapter = new ListViewFooterAdapter(this);
        setAdapter(mAdapter);
        setVerticalScrollBarEnabled(false);
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                scrollActionDispatcher.dispatchAction(CollectionView.OnScrollListener::onScrollStateChanged, view, scrollState);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                firstVisibleItemIdx = firstVisibleItem;
                lastVisibleItemIdx = firstVisibleItem + visibleItemCount;

                scrollActionDispatcher.dispatchAction(CollectionView.OnScrollListener::onScrolled, view, 0, 0);
            }
        });

        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                adapterDataObservableDispatcher.dispatchAction(CollectionView.AdapterDataObserver::onChanged);
            }
        });
    }

    @Override
    public void scrollVerticallyToPosition(int position, boolean anim) {
        if (anim) {
            smoothScrollToPosition(position);
        } else {
            setSelection(position);
        }
    }

    @Override
    public List<ViewObject> getList() {
        return mAdapter.getList();
    }

    @Override
    public IFooterAdapter getCommonAdapter() {
        return mAdapter;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setSpanCount(int spanCount) {
        // ListViewCollection don't support multi column
    }

    @Override
    public int getFirstVisibleItemIdx() {
        return firstVisibleItemIdx;
    }

    @Override
    public int getLastVisibleItemIdx() {
        return lastVisibleItemIdx;
    }

    @Override
    public void setFooterView(LoadMoreFooterView footerView, boolean showFooterAtTop) {
        footerView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mAdapter.setFooterView(footerView, showFooterAtTop);
    }

    @Override
    public void addOnScrollListener(CollectionView.OnScrollListener onScrollListener) {
        scrollActionDispatcher.registerNotify(onScrollListener);
    }

    @Override
    public void removeOnScrollListener(CollectionView.OnScrollListener onScrollListener) {
        scrollActionDispatcher.unregisterNotify(onScrollListener);
    }

    @Override
    public void registerAdapterDataObserver(CollectionView.AdapterDataObserver adapterDataObserver) {
        adapterDataObservableDispatcher.registerNotify(adapterDataObserver);
    }

    @Override
    public void unregisterAdapterDataObserver(CollectionView.AdapterDataObserver adapterDataObserver) {
        adapterDataObservableDispatcher.unregisterNotify(adapterDataObserver);
    }
}
