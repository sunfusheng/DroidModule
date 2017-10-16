package com.sunfusheng.infostream;

import android.content.Context;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.sunfusheng.utils.actions.Action4;
import com.sunfusheng.infostream.DataSource.InfoStreamDataSource;
import com.sunfusheng.infostream.HeaderProvider.HeaderProvider;
import com.sunfusheng.infostream.HeaderProvider.NilHeaderProvider;
import com.sunfusheng.infostream.RefreshStrategy.AbsRefreshStrategy;
import com.sunfusheng.infostream.anotations.FooterStatus;
import com.sunfusheng.infostream.anotations.LoadingStatus;
import com.sunfusheng.viewobject.delegate.action.ActionDelegateProvider;
import com.sunfusheng.viewobject.delegate.action.factory.ActionDelegateFactory;
import com.sunfusheng.viewobject.helper.ViewObjectComparator;
import com.sunfusheng.viewobject.viewobject.ViewObject;
import com.sunfusheng.viewobject.viewobject.ViewObjectProvider;
import com.sunfusheng.viewobject.viewobject.factory.ViewObjectFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function4;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;


public class InfoStreamPresenter implements InfoStreamContract.Presenter {

    private final static long EXTRA_CHANNEL_LAST_REFRESH_TIME = 15 * 60 * 1000;
    private final static int READ_TIMEOUT_MILLIS = 10000;

    protected InfoStreamContract.View view;
    protected InfoStreamDataSource repository;
    protected AbsRefreshStrategy refreshStrategy;
    protected HeaderProvider headerProvider = new NilHeaderProvider();
    protected ActionDelegateProvider actionDelegateProvider = new ActionDelegateProvider();
    protected ViewObjectProvider viewObjectProvider = new ViewObjectProvider();
    protected boolean isRefreshing = false;
    protected Subject<Object> lifeCycle = PublishSubject.create().toSerialized();
    private List<ViewObject> newHeaders = new ArrayList<>();
    private List<ViewObject> currentHeaders = new ArrayList<>();
    private long channelRefreshInterval = EXTRA_CHANNEL_LAST_REFRESH_TIME;
    private boolean gotMoreData = false;

    public InfoStreamPresenter(InfoStreamContract.View view,
                               InfoStreamDataSource repository,
                               HeaderProvider headerProvider,
                               AbsRefreshStrategy refreshStrategy) {
        this.view = view;
        this.repository = repository;
        this.refreshStrategy = refreshStrategy;

        if (view != null) {
            view.setPresenter(this);
        }

        if (headerProvider != null) {
            this.headerProvider = headerProvider;
        }
    }

    @Override
    public void init() {
        view.init();
        view.setLoadingStatus(LoadingStatus.LOADING);

        setRecyclerWithRefreshStrategy(refreshStrategy);
        headerProvider.init();

        long now = System.currentTimeMillis();
        if (now - repository.lastUpdateTime() > channelRefreshInterval) {
            load(InfoStreamContract.LoadType.TYPE_BOTH);
        } else {
            load(InfoStreamContract.LoadType.TYPE_LOCAL);
        }
    }

    @Override
    public void unInit() {
        view.unInit();
        headerProvider.unInit();
        lifeCycle.onComplete();
    }

    @Override
    public void onPause() {
        if (view != null) {
            view.onPause();
        }
    }

    @Override
    public void onResume() {
        if (view != null) {
            view.onResume();
        }
    }

    private void setRecyclerWithRefreshStrategy(AbsRefreshStrategy refreshStrategy) {
        if (refreshStrategy == null) {
            return;
        }

        if (refreshStrategy.isPullRefreshEnabled()) {
            view.enablePullRefresh();
        }

        if (refreshStrategy.isLoadMore()) {
            view.enableLoadMore();
        }

        if (refreshStrategy.isLoadMoreFromTop()) {
            view.enableLoadMoreFromTop();
        }
    }

    protected List<ViewObject> convertToViewObject(List data) {
        List<ViewObject> voList = new ArrayList<>();
        for (Object item : data) {
            if (item instanceof ViewObject) {
                voList.add((ViewObject) item);
            } else {
                ViewObject vo = viewObjectProvider.model2ViewObject(item, view.getContext(), actionDelegateProvider);
                if (vo != null) {
                    voList.add(vo);
                } else {
                    Logger.w("Create view object failed, item = " + item.toString());
                }
            }
        }
        return voList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void load(InfoStreamContract.LoadType loadType) {
        if (isRefreshing) {
            return;
        }

        Subject<Object> headerCompleteSubject = ReplaySubject.create().toSerialized();
        headerProvider.getHeader(view.getContext(), actionDelegateProvider, viewObjectProvider)
                .subscribeOn(Schedulers.io())
                .timeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS, Observable.error(new TimeoutException()))
                .filter(it -> it != null)
                .takeUntil(lifeCycle)
                .doOnTerminate(() -> headerCompleteSubject.onNext(0))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onLoadHeader, Throwable::printStackTrace);

        if (!headerProvider.syncWithDataSource()) {
            headerCompleteSubject.onNext(0);
        }

        isRefreshing = true;
        view.showLoadIndicator(refreshStrategy.isPullRefreshEnabled());
        repository.load(loadType)
                .subscribeOn(Schedulers.io())
                .timeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS, Observable.error(new TimeoutException()))
                .map(it -> new Pair<>(it.first, new Pair<>(it.second.getTips(), convertToViewObject(it.second.getData()))))
                .filter(it -> it != null)
                .delay(it -> headerCompleteSubject)
                .doOnTerminate(headerCompleteSubject::onComplete)
                .doOnTerminate(() -> isRefreshing = false)
                .takeUntil(lifeCycle)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onLoadData, this::onLoadError, this::onLoadComplete);
    }

    protected void onLoadHeader(List<ViewObject> headerList) {
        this.newHeaders = headerList;

        if (!isRefreshing) {
            List<ViewObject> currentList = view.getList();

            if (currentHeaders != null && currentHeaders.size() > 0) {
                currentList.removeAll(currentHeaders);
            }

            currentHeaders = new ArrayList<>(newHeaders);
            List<ViewObject> result = new ArrayList<>(headerList);
            if (!headerProvider.showInBottom()) {
                result.addAll(currentList);
            } else {
                result.addAll(0, currentList);
            }

            view.setList(result, false);
        }
    }

    protected void onLoadData(Pair<Integer, Pair<String, List<ViewObject>>> data) {
        List<ViewObject> currentList = view.getList();

        if (data.first == InfoStreamDataSource.SourceType.RemoteSource && refreshStrategy.isShowNewDataToast()) {
            if (!TextUtils.isEmpty(data.second.first)) {
                view.showLoadingTopToast(data.second.first);
            }
        }

        if (currentHeaders != null && currentHeaders.size() > 0) {
            currentList.removeAll(currentHeaders);
        }

        List<ViewObject> result = new ArrayList<>(newHeaders);
        currentHeaders = new ArrayList<>(newHeaders);

        if (!headerProvider.showInBottom()) {
            result.addAll(refreshStrategy.onLoad(data.first, currentList, data.second.second));
        } else {
            result.addAll(0, refreshStrategy.onLoad(data.first, currentList, data.second.second));
        }

        view.setList(result, false);
        updateViewStatus();
    }

    protected void onLoadError(Throwable e) {
        Logger.e(e.getMessage());
        view.showLoadIndicator(false);
        view.setFooterStatus(FooterStatus.ERROR);
        view.setLoadingStatus(LoadingStatus.FAILED);
    }

    protected void onLoadComplete() {
        view.showLoadIndicator(false);
        view.setFooterStatus(FooterStatus.IDLE);
        updateViewStatus();

        if (refreshStrategy != null && !refreshStrategy.isLoadMoreFromTop() && refreshStrategy.isPullRefreshEnabled()) {
            scrollToTop();
        }
    }

    protected void updateViewStatus() {
        List<ViewObject> list = view.getList();
        if (currentHeaders != null) {
            list.removeAll(currentHeaders);
        }

        if (list.size() == 0 && isRefreshing) {
            return;
        }
        view.setLoadingStatus(list.size() == 0 ? LoadingStatus.EMPTY : LoadingStatus.SUCCEED);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadMore() {
        if (isRefreshing) {
            return;
        }
        isRefreshing = true;
        gotMoreData = false;

        view.setFooterStatus(FooterStatus.LOADING);
        repository.loadMore()
                .subscribeOn(Schedulers.io())
                .timeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS, Observable.error(new TimeoutException()))
                .observeOn(AndroidSchedulers.mainThread())
                .map(it -> new Pair<>(it.first, convertToViewObject(it.second.getData())))
                .doOnError(e -> view.setFooterStatus(FooterStatus.ERROR))
                .takeUntil(lifeCycle)
                .doOnTerminate(() -> isRefreshing = false)
                .subscribe(it -> {
                    gotMoreData = true;

                    if (it.second.size() > 0) {
                        List<ViewObject> currentList = view.getList();
                        currentList.removeAll(newHeaders);
                        List<ViewObject> result = new ArrayList<>(newHeaders);
                        if (!headerProvider.showInBottom()) {
                            result.addAll(refreshStrategy.onLoadMore(it.first, currentList, it.second));
                        } else {
                            result.addAll(0, refreshStrategy.onLoadMore(it.first, currentList, it.second));
                        }
                        view.setList(result, true);
                        view.setFooterStatus(FooterStatus.IDLE);
                    } else {
                        view.setFooterStatus(FooterStatus.FULL);
                    }
                }, Throwable::printStackTrace, () -> {
                    if (!gotMoreData) {
                        view.setFooterStatus(FooterStatus.FULL);
                    }
                });
    }

    @Override
    public void refresh(boolean force) {
        long now = System.currentTimeMillis();
        if (force || now - repository.lastUpdateTime() > channelRefreshInterval) {
            load(InfoStreamContract.LoadType.TYPE_REMOTE);
        } else {
            load(InfoStreamContract.LoadType.TYPE_LOCAL);
        }
    }

    @Override
    public void remove(ViewObjectComparator viewObjectComparator) {
        if (view == null) {
            return;
        }

        ViewObject vo = view.getViewObject(viewObjectComparator);
        if (vo == null) {
            return;
        }

        view.remove(vo);
        if (vo.getData() != null && repository != null) {
            repository.remove(vo.getData());
        }
    }

    @Override
    public void removeAll(ViewObjectComparator viewObjectComparator) {
        List<ViewObject> viewObjectList = new ArrayList<>(view.getList());
        for (ViewObject viewObject : viewObjectList) {
            if (viewObjectComparator.isEquals(viewObject)) {
                view.remove(viewObject);
            }
        }
    }

    private void scrollToTop() {
        int scrollTo = 0;
        if (headerProvider != null && !headerProvider.showInBottom()) {
            scrollTo = headerProvider.getFirstVisibleHeader();
        }
        view.scrollList(scrollTo);
    }

    public <T> void registerActionDelegate(Class<? extends ViewObject> viewObjectClass, Class<T> modelClass, Action4<Context, Integer, T, ViewObject<?>> actionDelegate) {
        actionDelegateProvider.registerActionDelegate(viewObjectClass, modelClass, actionDelegate);
    }

    public <T> void registerActionDelegate(int actionId, Class<T> modelClass, Action4<Context, Integer, T, ViewObject<?>> actionDelegate) {
        actionDelegateProvider.registerActionDelegate(actionId, modelClass, actionDelegate);
    }

    public <T, K> void registerViewObjectCreator(Class<T> modelClass, Function<T, K> keyGenerator, K key, Function4<T, Context, ActionDelegateFactory, ViewObjectFactory, ViewObject> viewObjectCreator) {
        viewObjectProvider.registerViewObjectCreator(modelClass, keyGenerator, key, viewObjectCreator);
    }

    public <T> void registerViewObjectCreator(Class<T> modelClass, Function4<T, Context, ActionDelegateFactory, ViewObjectFactory, ViewObject> viewObjectCreator) {
        viewObjectProvider.registerViewObjectCreator(modelClass, viewObjectCreator);
    }

    public void setRefreshInterval(int refreshInterval) {
        channelRefreshInterval = refreshInterval;
    }
}
