package com.sunfusheng.base.widget.CollectionView;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.sunfusheng.base.R;
import com.sunfusheng.base.widget.RefreshView.SwipeRefreshWrapper;
import com.sunfusheng.base.widget.TopToast.TopToastUtil;
import com.sunfusheng.infostream.anotations.FooterStatus;
import com.sunfusheng.infostream.anotations.LoadingStatus;
import com.sunfusheng.viewobject.helper.IViewObjectAdapter;
import com.sunfusheng.viewobject.viewobject.ViewObject;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CollectionView extends FrameLayout {

    private ICollectionView collectionView;
    private LoadingStateDelegate loadingStateDelegate;
    private View mErrorLayout;
    private View mEmptyLayout;
    private View mLoadingLayout;
    private OnClickListener errorLayoutClickListener;
    private OnClickListener emptyViewClickListener;

    public SwipeRefreshWrapper mSwipeRefreshLayout;
    private LoadMoreFooterView footerView;
    private boolean loadMoreFromTop = false;
    private int collectionType = CollectionViewFactory.Type.RECYCLER_VIEW;

    private boolean enableLoadMore = false;
    private boolean isLoadingMore = false;
    private ViewObject lastViewObject;
    private Pair<Integer, Integer> visibleViewObjectPosition;
    ViewStub errorViewStub;
    ViewStub emptyViewStub;
    private CollectionView.OnLoadMoreListener onLoadMoreListener;

    private List<OnScrollListener> scrollListenerList = new CopyOnWriteArrayList<>();
    private List<AdapterDataObserver> adapterDataObserverList = new CopyOnWriteArrayList<>();


    public CollectionView(Context context) {
        this(context, null);
    }

    public CollectionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CollectionViewAttr);
        collectionType = ta.getInt(R.styleable.CollectionViewAttr_collectionType, collectionType);
        ta.recycle();
        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.widget_collection_view, this);
        collectionView = CollectionViewFactory.getCollectionView(getContext(), collectionType);

        mSwipeRefreshLayout = (SwipeRefreshWrapper) view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.addView(collectionView.getView(), new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mSwipeRefreshLayout.setEnabled(false);

        footerView = new LoadMoreFooterView(getContext());
        footerView.setStatus(FooterStatus.IDLE);
        mLoadingLayout = findViewById(R.id.loading_layout);
        errorViewStub = (ViewStub) view.findViewById(R.id.error_view_stub);
        emptyViewStub = (ViewStub) view.findViewById(R.id.empty_view_stub);
        loadingStateDelegate = new LoadingStateDelegate(mSwipeRefreshLayout, null, mLoadingLayout, null, null, errorViewStub, null, emptyViewStub);

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(View view, int newState) {
                super.onScrollStateChanged(view, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && !isLoadingMore) {
                    boolean firstItemVisible = collectionView.getFirstVisibleItemIdx() == 0;
                    boolean lastItemVisible = (collectionView.getCommonAdapter().getContentItemCount() > 0) && collectionView.getLastVisibleItemIdx() >= collectionView.getCommonAdapter().getContentItemCount() - 1;

                    if (loadMoreFromTop && firstItemVisible && !lastItemVisible) {
                        isLoadingMore = true;
                        lastViewObject = collectionView.getCommonAdapter().getViewObject(0);
                        if (onLoadMoreListener != null && enableLoadMore) {
                            onLoadMoreListener.loadMoreFromTop(collectionView.getCommonAdapter().getContentItemCount());
                        }
                    } else if (!loadMoreFromTop && !firstItemVisible && lastItemVisible) {
                        isLoadingMore = true;
                        lastViewObject = collectionView.getCommonAdapter().getViewObject(collectionView.getCommonAdapter().getContentItemCount() - 1);
                        if (onLoadMoreListener != null && enableLoadMore) {
                            onLoadMoreListener.loadMore(collectionView.getCommonAdapter().getContentItemCount(), collectionView.getLastVisibleItemIdx());
                        }
                    }
                }
            }

            @Override
            public void onScrolled(View view, int dx, int dy) {
                super.onScrolled(view, dx, dy);
                updateCurrentVisibleViewObjectPosition();
                if (isLoadingMore && ((loadMoreFromTop && lastViewObject != collectionView.getCommonAdapter().getViewObject(0)) ||
                        (!loadMoreFromTop && lastViewObject != collectionView.getCommonAdapter().getViewObject(collectionView.getCommonAdapter().getContentItemCount() - 1)))) {
                    isLoadingMore = false;
                    lastViewObject = null;
                }
            }
        });
    }

    private void updateCurrentVisibleViewObjectPosition() {
        if (collectionView.getFirstVisibleItemIdx() == RecyclerView.NO_POSITION
                && collectionView.getLastVisibleItemIdx() == RecyclerView.NO_POSITION) {
            return;
        }
        if (visibleViewObjectPosition != null) {
            for (int i = visibleViewObjectPosition.first; i < collectionView.getFirstVisibleItemIdx(); i++) {
                ViewObject vo = collectionView.getCommonAdapter().getViewObject(i);
                if (vo instanceof ICollectionViewItem) {
                    ((ICollectionViewItem) vo).scrolledOutOfScreen();
                }
            }
            for (int i = visibleViewObjectPosition.second; i > collectionView.getLastVisibleItemIdx(); i--) {
                ViewObject vo = collectionView.getCommonAdapter().getViewObject(i);
                if (vo instanceof ICollectionViewItem) {
                    ((ICollectionViewItem) vo).scrolledOutOfScreen();
                }
            }
            for (int i = collectionView.getFirstVisibleItemIdx(); i < visibleViewObjectPosition.first; i++) {
                ViewObject vo = collectionView.getCommonAdapter().getViewObject(i);
                if (vo instanceof ICollectionViewItem) {
                    ((ICollectionViewItem) vo).scrolledInToScreen();
                }
            }
            for (int i = collectionView.getLastVisibleItemIdx(); i > visibleViewObjectPosition.second; i--) {
                ViewObject vo = collectionView.getCommonAdapter().getViewObject(i);
                if (vo instanceof ICollectionViewItem) {
                    ((ICollectionViewItem) vo).scrolledInToScreen();
                }
            }
        }

        visibleViewObjectPosition = new Pair<>(collectionView.getFirstVisibleItemIdx(), collectionView.getLastVisibleItemIdx());
    }

    public void solveSlidingConflictWithNestedScrollView() {
        if (collectionView instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) collectionView;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            layoutManager.setAutoMeasureEnabled(true);
            recyclerView.setNestedScrollingEnabled(false);
        }
    }

    public RecyclerView getRecyclerView() {
        if (collectionView instanceof RecyclerView)
            return (RecyclerView) collectionView;
        return null;
    }

    public ListView getListView() {
        if (collectionView instanceof ListView)
            return (ListView) collectionView;
        return null;
    }

    public void enableLoadMoreFromTop() {
        loadMoreFromTop = true;
        enableLoadMore = true;
        mSwipeRefreshLayout.setEnabled(false);
        collectionView.setFooterView(footerView, true);
    }

    public void enableLoadMore() {
        collectionView.setFooterView(footerView, false);
        enableLoadMore = true;
    }

    public void setDefaultOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        if (loadMoreFromTop)
            return;

        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setOnRefreshListener(listener);
    }

    public void setRefreshing(boolean refreshing) {
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    public void setEmptyViewClickListener(OnClickListener listener) {
        this.emptyViewClickListener = listener;
        if (mEmptyLayout != null) {
            mEmptyLayout.setOnClickListener(listener);
        }
    }

    public void setErrorViewClickListener(OnClickListener listener) {
        this.errorLayoutClickListener = listener;
        if (mErrorLayout != null) {
            mErrorLayout.setOnClickListener(listener);
        }
    }

    public void setEmptyView(@LayoutRes int layoutResource) {
        if (emptyViewStub != null && layoutResource != -1) {
            emptyViewStub.setLayoutResource(layoutResource);
        }
    }

    public void setErrorView(@LayoutRes int layoutResource) {
        if (errorViewStub != null && layoutResource != -1) {
            errorViewStub.setLayoutResource(layoutResource);
        }
    }

    public void setLoadingState(@LoadingStatus int state) {
        if (state == LoadingStatus.FAILED) {
            if (collectionView.getList().size() > 0) {
                TopToastUtil.showTopToast(this, getResources().getString(R.string.top_toast_net_error));
                loadingStateDelegate.setViewState(LoadingStatus.SUCCEED);
            } else {
                mErrorLayout = loadingStateDelegate.setViewState(LoadingStatus.FAILED);
                setErrorViewClickListener(errorLayoutClickListener);
            }
        } else if (state == LoadingStatus.EMPTY) {
            mEmptyLayout = loadingStateDelegate.setViewState(LoadingStatus.EMPTY);
            setEmptyViewClickListener(emptyViewClickListener);
        } else {
            loadingStateDelegate.setViewState(state);
        }
    }

    public LoadMoreFooterView getFooterView() {
        return footerView;
    }

    public void scrollVerticallyToPosition(int position, boolean anim) {
        collectionView.scrollVerticallyToPosition(position, anim);
    }

    public Pair<Integer, Integer> getVisibleDataPosition() {
        return visibleViewObjectPosition;
    }

    public IViewObjectAdapter getAdapter() {
        return collectionView.getCommonAdapter();
    }

    public void setList(List<ViewObject> viewObjectList, boolean animation) {
        if (getAdapter() != null) {
            getAdapter().setList(viewObjectList, animation);
            if (!animation) {
                getAdapter().notifyDataSetChanged();
            }
        }
    }

    public void setOnLoadMoreListener(CollectionView.OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setCollectionViewType(@CollectionViewFactory.Type int collectionType) {
        if (this.collectionType == collectionType) {
            return;
        }

        if (collectionView != null && collectionView.getView().getParent() != null) {
            mSwipeRefreshLayout.removeView(collectionView.getView());
        }

        collectionView = CollectionViewFactory.getCollectionView(getContext(), collectionType);
        mSwipeRefreshLayout.addView(collectionView.getView());

        for (OnScrollListener listener : scrollListenerList) {
            collectionView.addOnScrollListener(listener);
        }

        for (AdapterDataObserver observer : adapterDataObserverList) {
            collectionView.registerAdapterDataObserver(observer);
        }
    }

    public void setColumnNum(int columnNum) {
        collectionView.setSpanCount(columnNum);
    }

    public void addOnScrollListener(OnScrollListener onScrollListener) {
        collectionView.addOnScrollListener(onScrollListener);

        if (!scrollListenerList.contains(onScrollListener)) {
            scrollListenerList.add(onScrollListener);
        }
    }

    public void removeOnScrollListener(OnScrollListener onScrollListener) {
        collectionView.removeOnScrollListener(onScrollListener);

        int index = scrollListenerList.indexOf(onScrollListener);
        if (index != -1) {
            scrollListenerList.remove(index);
        }
    }

    public void registerAdapterDataObserver(AdapterDataObserver adapterDataObserver) {
        collectionView.registerAdapterDataObserver(adapterDataObserver);

        if (adapterDataObserverList.contains(adapterDataObserver)) {
            return;
        }

        adapterDataObserverList.add(adapterDataObserver);
    }

    public void unregisterAdapterDataObserver(AdapterDataObserver adapterDataObserver) {
        collectionView.unregisterAdapterDataObserver(adapterDataObserver);

        if (!adapterDataObserverList.contains(adapterDataObserver)) {
            return;
        }

        adapterDataObserverList.remove(adapterDataObserver);
    }

    public interface OnLoadMoreListener {
        void loadMore(int itemsCount, final int maxLastVisiblePosition);

        void loadMoreFromTop(int itemsCount);
    }

    public abstract static class OnScrollListener {
        public void onScrollStateChanged(View view, int newState) {
        }

        public void onScrolled(View view, int dx, int dy) {
        }
    }

    public abstract static class AdapterDataObserver {
        public AdapterDataObserver() {
        }

        public void onChanged() {
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        }
    }

    public void setEmptyLayout(int resId) {
        if (mEmptyLayout != null) {
            removeView(mEmptyLayout);
        }

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        mEmptyLayout = layoutInflater.inflate(resId, this, false);

        addView(mEmptyLayout);
        mEmptyLayout.setVisibility(View.GONE);

        loadingStateDelegate = new LoadingStateDelegate(mSwipeRefreshLayout, null, mLoadingLayout, null, null, errorViewStub, mEmptyLayout, emptyViewStub);
    }
}
