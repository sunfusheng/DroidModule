package com.sunfusheng.base.DataStream;

import android.content.Context;
import android.support.annotation.LayoutRes;

import com.sunfusheng.base.widget.CollectionView.CollectionView;
import com.sunfusheng.base.widget.CollectionView.LoadMoreFooterView;
import com.sunfusheng.base.widget.TopToast.TopToast;
import com.sunfusheng.base.widget.TopToast.TopToastUtil;
import com.sunfusheng.utils.actions.Action1;
import com.sunfusheng.infostream.InfoStreamContract;
import com.sunfusheng.infostream.anotations.FooterStatus;
import com.sunfusheng.infostream.anotations.LoadingStatus;
import com.sunfusheng.viewobject.helper.IViewObjectAdapter;
import com.sunfusheng.viewobject.helper.ViewObjectComparator;
import com.sunfusheng.viewobject.viewobject.ViewObject;

import java.util.List;

public class DataStreamViewDefaultImpl implements InfoStreamContract.View {

    private CollectionView collectionView;
    private InfoStreamContract.Presenter presenter;
    private TopToast topToast;

    private boolean showEmptyView = true;
    private boolean isPagerVisible = false;

    public DataStreamViewDefaultImpl(CollectionView collectionView) {
        this.collectionView = collectionView;
    }

    @Override
    public void init() {
        collectionView.setErrorViewClickListener(v -> {
            presenter.load(InfoStreamContract.LoadType.TYPE_REMOTE);
            setLoadingStatus(LoadingStatus.LOADING);
        });

        collectionView.setEmptyViewClickListener(v -> {
            presenter.load(InfoStreamContract.LoadType.TYPE_REMOTE);
            setLoadingStatus(LoadingStatus.LOADING);
        });

        getFooterView().setFooterListener(new LoadMoreFooterView.FooterClickListener() {
            @Override
            public boolean onFullRefresh() {
                presenter.load(InfoStreamContract.LoadType.TYPE_REMOTE);
                return true;
            }

            @Override
            public boolean onLoadMore() {
                presenter.loadMore();
                return true;
            }

            @Override
            public boolean onErrorClick() {
                presenter.loadMore();
                return true;
            }
        });
    }

    @Override
    public void unInit() {
        if (topToast != null) {
            topToast.dismiss();
        }
    }

    @Override
    public void onResume() {
        if (getAdapter() != null) {
            getAdapter().onContextResume();
        }
    }

    @Override
    public void onPause() {
        if (getAdapter() != null) {
            getAdapter().onContextPause();
        }
    }

    @Override
    public Context getContext() {
        return collectionView.getContext();
    }

    protected IViewObjectAdapter getAdapter() {
        if (collectionView != null) {
            return collectionView.getAdapter();
        }
        return null;
    }

    @Override
    public void setPresenter(InfoStreamContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setList(List<ViewObject> viewObjectList, boolean animation) {
        if (collectionView != null) {
            collectionView.setList(viewObjectList, animation);
        }
    }

    @Override
    public List<ViewObject> getList() {
        return getAdapter().getList();
    }

    @Override
    public void showLoadIndicator(boolean show) {
        collectionView.setRefreshing(show);
    }

    @Override
    public void showLoadingTopToast(String text) {
        if (collectionView != null && collectionView.isShown()) {
            if (topToast != null) {
                topToast.dismiss();
            }
            if (isPagerVisible) {
                topToast = TopToastUtil.showTopToast(collectionView, text);
            }
        }
    }

    public void setPagerVisible(boolean pagerVisible) {
        isPagerVisible = pagerVisible;
    }

    @Override
    public void setLoadingStatus(@LoadingStatus int state) {
        if (!showEmptyView || collectionView == null) return;

        collectionView.setLoadingState(state);
    }

    @Override
    public void setFooterStatus(@FooterStatus int status) {
        if (getAdapter() != null) {
            getFooterView().setStatus(status);
        }

        if (onFooterStatusChangedAction != null) {
            onFooterStatusChangedAction.call(status);
        }
    }

    public LoadMoreFooterView getFooterView() {
        return collectionView.getFooterView();
    }

    private Action1<Integer> onFooterStatusChangedAction;

    public void setOnFooterStatusChangedAction(Action1<Integer> action1) {
        this.onFooterStatusChangedAction = action1;
    }

    @Override
    public void scrollList(int scrollTo) {
        if (collectionView != null) {
            collectionView.scrollVerticallyToPosition(scrollTo, false);
        }
    }

    @Override
    public void enablePullRefresh() {
        if (collectionView != null) {
            collectionView.setDefaultOnRefreshListener(() -> presenter.load(InfoStreamContract.LoadType.TYPE_REMOTE));
        }
    }

    @Override
    public void enableLoadMore() {
        collectionView.enableLoadMore();
        collectionView.setOnLoadMoreListener(new CollectionView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, int maxLastVisiblePosition) {
                presenter.loadMore();
            }

            @Override
            public void loadMoreFromTop(int itemsCount) {

            }
        });
    }

    @Override
    public void enableLoadMoreFromTop() {
        collectionView.enableLoadMoreFromTop();
        collectionView.setOnLoadMoreListener(new CollectionView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, int maxLastVisiblePosition) {
            }

            @Override
            public void loadMoreFromTop(int itemsCount) {
                presenter.loadMore();
            }
        });
    }

    @Override
    public void addData(int position, List<ViewObject> viewObjectList, boolean animation) {
        if (getAdapter() != null) {
            getAdapter().addAll(position, viewObjectList, animation);
        }
    }

    @Override
    public void remove(ViewObject viewObject) {
        if (getAdapter() != null) {
            if (viewObject != null) {
                getAdapter().remove(viewObject);
            }
        }
    }

    @Override
    public void showEmptyView(boolean show) {
        this.showEmptyView = show;
    }

    @Override
    public void setEmptyView(@LayoutRes int layoutResource) {
        collectionView.setEmptyView(layoutResource);
    }

    @Override
    public void setErrorView(@LayoutRes int layoutResource) {
        collectionView.setErrorView(layoutResource);
    }

    @Override
    public ViewObject getViewObject(ViewObjectComparator viewObjectComparator) {
        if (getAdapter() == null) {
            return null;
        }

        return getAdapter().getViewObject(viewObjectComparator);
    }

    @Override
    public void setColumnNum(int columnNumber) {
        collectionView.setColumnNum(columnNumber);
    }

}
