package com.sunfusheng.infostream;

import com.sunfusheng.infostream.DataSource.InfoStreamPushDataSource;
import com.sunfusheng.infostream.HeaderProvider.HeaderProvider;
import com.sunfusheng.infostream.RefreshStrategy.AbsPushRefreshStrategy;
import com.sunfusheng.viewobject.helper.ViewObjectComparator;
import com.sunfusheng.viewobject.viewobject.ViewObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class InfoStreamPushPresenter extends InfoStreamPresenter {
    public void setNewArrivedWithAnim(boolean newArrivedWithAnim) {
        this.newArrivedWithAnim = newArrivedWithAnim;
    }

    //插入新item时是否显示动画
    private boolean newArrivedWithAnim = true;

    public InfoStreamPushPresenter(InfoStreamContract.View view, InfoStreamPushDataSource repository, HeaderProvider headerProvider, AbsPushRefreshStrategy refreshStrategy) {
        super(view, repository, headerProvider, refreshStrategy);

        repository.onNewItemObservable()
                .takeUntil(lifeCycle)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onNewItemReceived, Throwable::printStackTrace);

        repository.onRemoveItemObservable()
                .takeUntil(lifeCycle)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onItemRemoved, Throwable::printStackTrace);
    }

    protected void onNewItemReceived(List<Object> data) {
        if (data == null || data.size() == 0) {
            return;
        }

        Observable.interval(0, 50, TimeUnit.MILLISECONDS, Schedulers.computation())
                .filter(it -> !isRefreshing)
                .take(1)
                .map(it -> data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::convertToViewObject)
                .takeUntil(lifeCycle)
                .subscribe(it -> {
                    AbsPushRefreshStrategy pushRefreshStrategy = (AbsPushRefreshStrategy) refreshStrategy;
                    switch (pushRefreshStrategy.positionOfNewItem()) {
                        case AbsPushRefreshStrategy.NewItemInsertPosition.InsertAtTop:
                            view.addData(0, it, newArrivedWithAnim);
                            break;
                        case AbsPushRefreshStrategy.NewItemInsertPosition.InsertAtBottom:
                            view.addData(view.getList().size(), it, newArrivedWithAnim);
                            break;
                        case AbsPushRefreshStrategy.NewItemInsertPosition.CustomPosition:
                            List<ViewObject> currentList = view.getList();
                            List<ViewObject> result = new ArrayList<>();
                            if (!headerProvider.showInBottom()) {
                                result.addAll(pushRefreshStrategy.onNewItemArrived(currentList, it));
                            } else {
                                result.addAll(0, pushRefreshStrategy.onNewItemArrived(currentList, it));
                            }
                            view.setList(result, newArrivedWithAnim);
                            break;
                    }
                    if (view.getList().size() == it.size()) {
                        updateViewStatus();
                    }
                }, Throwable::printStackTrace);
    }

    protected void onItemRemoved(List<ViewObjectComparator> data) {
        if (data == null || data.size() == 0) {
            return;
        }

        for (ViewObjectComparator viewObjectComparator : data) {
            remove(viewObjectComparator);
        }
    }

}
