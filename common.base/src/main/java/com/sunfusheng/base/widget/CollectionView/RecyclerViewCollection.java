package com.sunfusheng.base.widget.CollectionView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.View;

import com.sunfusheng.utils.actions.ActionDispatcher;
import com.sunfusheng.viewobject.viewobject.ViewObject;

import java.util.List;

class RecyclerViewCollection extends RecyclerView implements ICollectionView {

    private RecyclerViewFooterAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private static final int DEFAULT_SPAN_COUNT = 1;
    private ActionDispatcher<CollectionView.OnScrollListener> scrollActionDispatcher = new ActionDispatcher<>();
    private ActionDispatcher<CollectionView.AdapterDataObserver> adapterDataObservableDispatcher = new ActionDispatcher<>();
    private MultiColumnDecoration multiColumnDecoration;

    public RecyclerViewCollection(Context context) {
        this(context, null);
    }

    public RecyclerViewCollection(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewCollection(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mAdapter = new RecyclerViewFooterAdapter(this);
        setAdapter(mAdapter);
        setSpanCount(DEFAULT_SPAN_COUNT);

        multiColumnDecoration = new MultiColumnDecoration(mAdapter, DEFAULT_SPAN_COUNT);
        addItemDecoration(multiColumnDecoration);

        ItemAnimator animator = getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollActionDispatcher.dispatchAction(CollectionView.OnScrollListener::onScrolled, recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                scrollActionDispatcher.dispatchAction(CollectionView.OnScrollListener::onScrollStateChanged, recyclerView, newState);
            }
        });

        mAdapter.registerAdapterDataObserver(new AdapterDataObserver() {
            @Override
            public void onChanged() {
                adapterDataObservableDispatcher.dispatchAction(CollectionView.AdapterDataObserver::onChanged);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                adapterDataObservableDispatcher.dispatchAction(CollectionView.AdapterDataObserver::onItemRangeChanged, positionStart, itemCount);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                adapterDataObservableDispatcher.dispatchAction(CollectionView.AdapterDataObserver::onItemRangeChanged, positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                adapterDataObservableDispatcher.dispatchAction(CollectionView.AdapterDataObserver::onItemRangeInserted, positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                adapterDataObservableDispatcher.dispatchAction(CollectionView.AdapterDataObserver::onItemRangeRemoved, positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                adapterDataObservableDispatcher.dispatchAction(CollectionView.AdapterDataObserver::onItemRangeMoved, fromPosition, toPosition, itemCount);
            }
        });
    }

    @Override
    public void scrollVerticallyToPosition(int position, boolean anim) {
        if (anim) {
            smoothScrollToPosition(position);
        } else {
            layoutManager.scrollToPositionWithOffset(position, 0);
        }
    }

    @Override
    public void setSpanCount(int spanCount) {
        if (multiColumnDecoration != null) {
            removeItemDecoration(multiColumnDecoration);
        }

        if (spanCount > 1) {
            layoutManager = new MultiColumnGridLayoutManager(getContext(), spanCount, mAdapter);
            multiColumnDecoration = new MultiColumnDecoration(mAdapter, spanCount);
            addItemDecoration(multiColumnDecoration);
        } else {
            layoutManager = new LinearLayoutManager(getContext());
        }

        layoutManager.setSmoothScrollbarEnabled(true);
        setLayoutManager(layoutManager);
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
    public int getFirstVisibleItemIdx() {
        return getCommonAdapter().getFirstVisibleItemIndex();
    }

    @Override
    public int getLastVisibleItemIdx() {
        return getCommonAdapter().getLastVisibleItemIndex();
    }

    @Override
    public void setFooterView(LoadMoreFooterView footerView, boolean showFooterAtTop) {
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
