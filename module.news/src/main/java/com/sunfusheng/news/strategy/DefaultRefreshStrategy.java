package com.sunfusheng.news.strategy;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.sunfusheng.infostream.DataSource.InfoStreamDataSource;
import com.sunfusheng.infostream.InfoStreamContract;
import com.sunfusheng.infostream.RefreshStrategy.AbsRefreshStrategy;
import com.sunfusheng.base.model.NewsModel;
import com.sunfusheng.viewobject.viewobject.ViewObject;
import com.sunfusheng.viewobject.viewobject.ViewObjectGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class DefaultRefreshStrategy extends AbsRefreshStrategy {

    ViewObject lastRefreshTagViewObject;
    InfoStreamContract.Presenter presenter;
    InfoStreamContract.View view;

    public DefaultRefreshStrategy(InfoStreamContract.Presenter presenter, InfoStreamContract.View view) {
        super();

        this.presenter = presenter;
        this.view = view;
    }

    @Override
    public boolean isPullRefreshEnabled() {
        return true;
    }

    @Override
    public boolean isLoadMore() {
        return true;
    }

    @Override
    public boolean isShowNewDataToast() {
        return true;
    }

    @Override
    public List<ViewObject> onLoad(int sourceType, List<ViewObject> currentItems, List<ViewObject> newItems) {
        ViewObject lastRefreshViewObject = null;
        if (lastRefreshTagViewObject != null && newItems.size() > 0) {
            currentItems.remove(lastRefreshTagViewObject);
            lastRefreshViewObject = newItems.get(newItems.size() - 1);
        }

        Logger.d("before change: current items size:" + currentItems.size() + ";new items size:" + newItems.size());
        changeStickNewsStatus(currentItems, newItems, true);
        Logger.d("after change:current items size:" + currentItems.size() + ";new items size:" + newItems.size());
        newItems.addAll(currentItems);
        sortAndReduce(newItems);

        if (lastRefreshViewObject != null && lastRefreshTagViewObject != null) {
            int tagPosition = newItems.indexOf(lastRefreshViewObject);
            if (tagPosition != -1 && (!isAllStickyNews(newItems, tagPosition))) {
                newItems.add(tagPosition + 1, lastRefreshTagViewObject);
                Logger.d("add overImageTag position:" + tagPosition);
            }
        }

        if (sourceType == InfoStreamDataSource.SourceType.RemoteSource && lastRefreshTagViewObject == null) {
            initRefreshTagView();
        }

        return newItems;
    }

    private boolean isAllStickyNews(List<ViewObject> newItems, int tagPosition) {
        int count = 0;
        for (int i = 0; i < tagPosition + 1; i++) {
            if (newItems.get(i).getData() instanceof NewsModel) {
                NewsModel model = (NewsModel) newItems.get(i).getData();
                if (model.getData() != null && model.getData().getIs_stick() == 1) {
                    count++;
                }
            }
        }
        return count == (tagPosition + 1);
    }

    @Override
    public List<ViewObject> onLoadMore(int sourceType, List<ViewObject> currentItems, List<ViewObject> newItems) {
        if (newItems == null || newItems.size() == 0) {
            return currentItems;
        }
        changeStickNewsStatus(currentItems, newItems, false);
        currentItems.addAll(newItems);
        return currentItems;
    }

    private void initRefreshTagView() {
        if (view.getContext() == null) {
            return;
        }
//        ActionDelegateProvider actionDelegateProvider = new ActionDelegateProvider();
//        actionDelegateProvider.registerActionDelegate(LastRefreshTagViewObject.class, Void.class, (context, actionId, data, vo) -> {
//            QEventBus.getEventBus().post(new Events.OnClickLastRefreshVOEvent());
//            presenter.load(InfoStreamContract.LoadType.TYPE_REMOTE);
//        });
//        lastRefreshTagViewObject = new LastRefreshTagViewObject(view.getContext(), null, actionDelegateProvider, null);
    }

    private void sortAndReduce(List<ViewObject> list) {
        sort(list, true);
        reduce(list);
        sort(list, false);
    }

    private void sort(List<ViewObject> list, boolean asc) {
        Collections.sort(list, (lhs, rhs) -> {
            try {
                ViewObject lowVo = getTopLevelViewObject(lhs);
                ViewObject highVo = getTopLevelViewObject(rhs);
                if (lowVo.getData() instanceof NewsModel && highVo.getData() instanceof NewsModel) {
                    NewsModel low = (NewsModel) lowVo.getData();
                    NewsModel high = (NewsModel) highVo.getData();
                    return low.getTime().compareTo(high.getTime()) * (asc ? 1 : -1);
                } else {
                    return 0;
                }
            } catch (Throwable e) {
                e.printStackTrace();
                return 0;
            }
        });
    }

    public static ViewObject getTopLevelViewObject(ViewObject viewObject) {
        if (viewObject.getParent() == null) {
            return viewObject;
        } else {
            return getTopLevelViewObject(viewObject.getParent());
        }
    }

    private void reduce(List<ViewObject> list) {
        removeChildVoList(list);
        reduceToSize(list);
        addChildVo(list);
    }

    @SuppressWarnings("unchecked")
    private void addChildVo(List<ViewObject> list) {
        List<ViewObject> result = new ArrayList<>();
        for (ViewObject viewObject : list) {
            if (viewObject instanceof ViewObjectGroup) {
                ViewObjectGroup group = (ViewObjectGroup) viewObject;
                result.addAll(group.getViewObjectList());
            } else {
                result.add(viewObject);
            }
        }
        list.clear();
        list.addAll(result);
    }

    private void reduceToSize(List<ViewObject> list) {
        if (list.size() <= 30) {
            return;
        }
        String uuidFlag = "";
        List<ViewObject> delList = new ArrayList<>();
        for (ViewObject viewObject : list) {
            if (!(viewObject.getData() instanceof NewsModel)) {
                continue;
            }
            NewsModel model = (NewsModel) viewObject.getData();
            if (TextUtils.isEmpty(uuidFlag)) {
                uuidFlag = model.getUuid();
                delList.add(viewObject);
            } else if (uuidFlag.equals(model.getUuid())) {
                delList.add(viewObject);
            } else {
                break;
            }
        }
        if (delList.size() == 0) {
            return;
        }
        list.removeAll(delList);
        reduceToSize(list);
    }

    private void removeChildVoList(List<ViewObject> list) {
        for (Iterator<ViewObject> iterator = list.iterator(); iterator.hasNext(); ) {
            ViewObject viewObject = iterator.next();
            if (viewObject.getParent() != null) {
                iterator.remove();
            }
        }
    }

    private void changeStickNewsStatus(List<ViewObject> currentItems, List<ViewObject> newItems, boolean onLoad) {
        if (currentItems.size() == 0 || newItems.size() == 0) {
            return;
        }
        List<ViewObject> currentStickyList = getStickyViewObject(currentItems);
        List<ViewObject> newStickyList = getStickyViewObject(newItems);
        if (!onLoad) {
            newItems.removeAll(newStickyList);
        }
        if (currentStickyList.size() == 0) {
            return;
        }

        if (onLoad) {
            if (newStickyList.size() == 0) {
                clearStickyNews(currentStickyList);
            } else {
                List<ViewObject> crossList = getCrossList(currentStickyList, newStickyList);
                List<ViewObject> subList = getSubList(currentStickyList, newStickyList);
                clearStickyNews(subList);
                currentItems.removeAll(crossList);
            }
        } else {
            updateLastStickyNewsTime(currentStickyList);
        }
    }

    private void clearStickyNews(List<ViewObject> currentStickyList) {
        Observable.defer(() -> Observable.fromIterable(currentStickyList)
                .filter(viewObject -> viewObject.getData() instanceof NewsModel)
                .map(ViewObject::getData)
                .filter(it -> it != null)
                .cast(NewsModel.class)
        ).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(it -> {
            NewsModel.NewsData newsItemInfoModel = it.getData();
            newsItemInfoModel.setIs_stick(0);
            it.setData(newsItemInfoModel);
//            NewsListDbHelper.getInstance().insert(it);
        }, Throwable::printStackTrace);
    }

    private void updateLastStickyNewsTime(List<ViewObject> currentStickyList) {
        long currentTimeMillis = System.currentTimeMillis();
        Observable.defer(() -> Observable.fromIterable(currentStickyList)
                .filter(viewObject -> viewObject.getData() instanceof NewsModel)
                .map(ViewObject::getData)
                .cast(NewsModel.class)
        ).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(it -> {
            it.setTime(currentTimeMillis - 1);
//            NewsListDbHelper.getInstance().insert(it);
        }, Throwable::printStackTrace);
    }

    private List<ViewObject> getStickyViewObject(List<ViewObject> rawList) {
        List<ViewObject> list = new ArrayList<>();
        for (ViewObject vo : rawList) {
            if (vo.getData() == null || !(vo.getData() instanceof NewsModel)) {
                continue;
            }
            NewsModel model = (NewsModel) vo.getData();
            if (model.getData() == null) {
                continue;
            }
            int is_stick = model.getData().getIs_stick();
            if (is_stick == 1) {
                list.add(vo);
            }
        }
        return list;
    }

    private List<ViewObject> getCrossList(List<ViewObject> list1, List<ViewObject> list2) {
        List<ViewObject> result = new ArrayList<>();
        for (ViewObject vo1 : list1) {
            if (!(vo1.getData() instanceof NewsModel)) {
                continue;
            }
            NewsModel model1 = (NewsModel) vo1.getData();
            for (ViewObject vo2 : list2) {
                if (!(vo2.getData() instanceof NewsModel)) {
                    continue;
                }
                NewsModel model2 = (NewsModel) vo2.getData();
                if (model1.getGid().equals(model2.getGid())) {
                    if (model1.getData() != null && model2.getData() != null) {
                        if (model1.getData().getTitle().equals(model2.getData().getTitle())) {
                            result.add(vo1);
                        }
                    } else {
                        result.add(vo1);
                    }
                    break;
                }
            }
        }
        return result;
    }

    private List<ViewObject> getSubList(List<ViewObject> list1, List<ViewObject> list2) {
        List<ViewObject> result = new ArrayList<>();
        for (ViewObject vo1 : list1) {
            boolean flag = true;
            if (!(vo1.getData() instanceof NewsModel)) {
                continue;
            }
            NewsModel model1 = (NewsModel) vo1.getData();
            for (ViewObject vo2 : list2) {
                if (!(vo2.getData() instanceof NewsModel)) {
                    continue;
                }
                NewsModel model2 = (NewsModel) vo2.getData();
                if (model1.getGid().equals(model2.getGid())) {
                    if (model1.getData() != null && model2.getData() != null) {
                        if (model1.getData().getTitle().equals(model2.getData().getTitle())) {
                            flag = false;
                            break;
                        }
                    } else {
                        flag = false;
                        break;
                    }
                    break;
                }
            }
            if (flag) {
                result.add(vo1);
            }
        }
        return result;
    }

}
