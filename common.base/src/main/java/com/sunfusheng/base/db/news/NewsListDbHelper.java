package com.sunfusheng.base.db.news;

import android.text.TextUtils;

import com.sunfusheng.base.db.DbHelper;
import com.sunfusheng.base.db.DbQueryParams;
import com.sunfusheng.base.model.NewsModel;
import com.sunfusheng.utils.actions.ActionDispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Sort;

/**
 * @author sunfusheng on 2017/8/24.
 */
public class NewsListDbHelper extends DbHelper<NewsDbModel> {

    private static final int MAX_COUNT = 1000;
    private ChildNewsDbHelper helper = null;

    private List<INewsDataDbChange> dbChangeListenerList = new CopyOnWriteArrayList<>();

    private static class Holder {
        private static NewsListDbHelper instance = new NewsListDbHelper();
    }

    public static NewsListDbHelper getInstance() {
        return Holder.instance;
    }

    private NewsListDbHelper() {
        helper = new ChildNewsDbHelper();
    }

    public List<NewsModel> getNewsList(int offset, int limit, String cid) {
        DbQueryParams queryParams = new DbQueryParams(NewsDbModel.class);
        if (offset == 0) {
            queryParams.appendSelection(new DbQueryParams.SelectionArgs(DbQueryParams.SelectionType.EQUAL, "channel", cid));
            long count = count(queryParams);
            if (count > MAX_COUNT) {
                queryParams.sortArgs = new DbQueryParams.SortArgs("time", Sort.ASCENDING);
                queryParams.limit = MAX_COUNT / 10;
                List<NewsDbModel> delList = queryAll(queryParams);
                for (NewsDbModel dbModel : delList) {
                    helper.delByParentGid(dbModel.getGid());
                    ActionDispatcher.dispatchAction(dbChangeListenerList, INewsDataDbChange::deleteNewsData, dbModel.getGid());
                }
                deleteAll(queryParams);
            }
        }
        queryParams = new DbQueryParams(NewsDbModel.class);
        queryParams.sortArgs = new DbQueryParams.SortArgs("index", Sort.ASCENDING);
        queryParams.limit = limit;
        queryParams.offset = offset;
        queryParams.appendSelection(new DbQueryParams.SelectionArgs(DbQueryParams.SelectionType.EQUAL, "channel", cid));
        return Observable.just(queryParams)
                .map(this::queryAll)
                .flatMap(Observable::fromIterable)
                .map(NewsDbModel::dbModel2Model)
                .toList().blockingGet();
    }

    public void saveNewsList(List<NewsModel> list, String cid, boolean loadMore, boolean paging) {
        if (loadMore) {
            if (!paging) {
                appendTail(new ArrayList<>(list), cid);
            }
        } else if (paging) {
            replaceNewsList(new ArrayList<>(list), cid);
        } else {
            appendToHead(new ArrayList<>(list), cid);
        }
    }

    private void replaceNewsList(List<NewsModel> list, String cid) {
        int index = 0;
        DbQueryParams params = new DbQueryParams(NewsDbModel.class);
        params.appendSelection(new DbQueryParams.SelectionArgs(DbQueryParams.SelectionType.EQUAL, "channel", cid));
        List<NewsDbModel> refactorNewsItemDbModels = queryAll(params);
        for (NewsDbModel dbModel : refactorNewsItemDbModels) {
            ActionDispatcher.dispatchAction(dbChangeListenerList, INewsDataDbChange::deleteNewsData, dbModel.getGid());
            NewsModel model = queryByGid(dbModel.getGid());
            if (model != null) {
                delete(model);
            }
        }
        for (NewsModel model : list) {
            model.setIndex(index++);
        }
        insert(list);
    }

    private void appendToHead(List<NewsModel> list, String cid) {
        DbQueryParams params = new DbQueryParams(NewsDbModel.class);
        params.appendSelection(new DbQueryParams.SelectionArgs(DbQueryParams.SelectionType.EQUAL, "channel", cid));
        params.sortArgs = new DbQueryParams.SortArgs("index", Sort.ASCENDING);
        NewsDbModel first = queryFirst(params);
        final long[] index = {0};
        if (first != null) {
            index[0] = first.getIndex();
        }
        params = new DbQueryParams(NewsDbModel.class);
        params.appendSelection(new DbQueryParams.SelectionArgs(DbQueryParams.SelectionType.EQUAL, "channel", cid));

        Observable.just(list)
                .subscribeOn(Schedulers.io())
                .doOnNext(it -> helper.saveChildNewsList(it))
                .flatMap(Observable::fromIterable)
                .doOnNext(it -> it.setIndex(index[0]++ - list.size()))
                .map(NewsDbModel::model2DbModel)
                .toList()
                .subscribe(this::insertOrUpdate, Throwable::printStackTrace);
    }

    private void appendTail(List<NewsModel> list, String cid) {
        DbQueryParams params = new DbQueryParams(NewsDbModel.class);
        params.appendSelection(new DbQueryParams.SelectionArgs(DbQueryParams.SelectionType.EQUAL, "channel", cid));
        params.sortArgs = new DbQueryParams.SortArgs("index", Sort.DESCENDING);
        NewsDbModel first = queryFirst(params);
        final long[] index = {0};
        if (first != null) {
            index[0] = first.getIndex();
        }
        params = new DbQueryParams(NewsDbModel.class);
        params.appendSelection(new DbQueryParams.SelectionArgs(DbQueryParams.SelectionType.EQUAL, "channel", cid));
        Observable.just(list)
                .subscribeOn(Schedulers.io())
                .doOnNext(it -> helper.saveChildNewsList(it))
                .flatMap(Observable::fromIterable)
                .doOnNext(it -> it.setIndex(index[0]++))
                .map(NewsDbModel::model2DbModel)
                .toList()
                .subscribe(this::insertOrUpdate, Throwable::printStackTrace);
    }

    public void clearStickNews(NewsModel model) {
        NewsModel.NewsData data = model.getData();
        data.setIs_stick(0);
        model.setData(data);
        insertOrUpdate(NewsDbModel.model2DbModel(model));
    }

    public Observable<List<NewsModel>> queryByChannel(String channel) {
        DbQueryParams params = new DbQueryParams(NewsDbModel.class);
        params.appendSelection(new DbQueryParams.SelectionArgs(DbQueryParams.SelectionType.EQUAL, "channel", channel));
        params.sortArgs = new DbQueryParams.SortArgs("index", Sort.ASCENDING);
        return Observable.just(params)
                .subscribeOn(Schedulers.io())
                .map(this::queryAll)
                .flatMap(Observable::fromIterable)
                .filter(it -> it != null)
                .map(NewsDbModel::dbModel2Model)
                .filter(it -> it != null)
                .toList().toObservable();
    }

    public NewsModel queryByGid(String gid) {
        DbQueryParams params = new DbQueryParams(NewsDbModel.class);
        params.appendSelection(new DbQueryParams.SelectionArgs(DbQueryParams.SelectionType.EQUAL, "gid", gid));
        NewsDbModel dbModel = queryFirst(params);
        if (dbModel != null) {
            return NewsDbModel.dbModel2Model(dbModel);
        } else {
            return queryChildItem(gid);
        }
    }

    public void insert(NewsModel model) {
        helper.saveChildNewsList(model);
        insertOrUpdate(NewsDbModel.model2DbModel(model));
    }

    private void insert(List<NewsModel> list) {
        helper.saveChildNewsList(list);
        List<NewsDbModel> dbList = new ArrayList<>();
        for (NewsModel model : list) {
            dbList.add(NewsDbModel.model2DbModel(model));
        }
        insertOrUpdate(dbList);
    }

    public void update(NewsModel model) {
        DbQueryParams params = new DbQueryParams(NewsDbModel.class);
        params.appendSelection(new DbQueryParams.SelectionArgs(DbQueryParams.SelectionType.EQUAL, "gid", model.getGid()));
        NewsDbModel refactorNewsItemDbModel = queryFirst(params);
        if (refactorNewsItemDbModel != null) {
            insert(model);
            return;
        }
        String parentGid = helper.getParentGid(model.getGid());
        if (TextUtils.isEmpty(parentGid)) {
            return;
        }
        params = new DbQueryParams(NewsDbModel.class);
        params.appendSelection(new DbQueryParams.SelectionArgs(DbQueryParams.SelectionType.EQUAL, "gid", parentGid));
        NewsDbModel dbModel = queryFirst(params);
        if (dbModel == null) {
            return;
        }
        NewsModel parentModel = NewsDbModel.dbModel2Model(dbModel);
        if (parentModel == null || parentModel.getData() == null || parentModel.getData().getNews() == null
                || parentModel.getData().getNews().size() == 0) {
            return;
        }
        List<NewsModel> childList = parentModel.getData().getNews();
        boolean flag = false;
        for (int i = 0; i < childList.size(); i++) {
            if (model.getGid().equals(childList.get(i).getGid())) {
                flag = true;
                childList.remove(i);
                childList.add(i, model);
            }
        }
        if (flag) {
            insertOrUpdate(NewsDbModel.model2DbModel(parentModel));
        }
    }

    private NewsModel queryChildItem(String gid) {
        String parentGid = helper.getParentGid(gid);
        if (TextUtils.isEmpty(parentGid)) {
            return null;
        }
        DbQueryParams params = new DbQueryParams(NewsDbModel.class);
        params.appendSelection(new DbQueryParams.SelectionArgs(DbQueryParams.SelectionType.EQUAL, "gid", parentGid));
        NewsDbModel dbModel = queryFirst(params);
        if (dbModel == null) {
            return null;
        }
        NewsModel model = NewsDbModel.dbModel2Model(dbModel);
        if (model == null || model.getData() == null || model.getData().getNews() == null) {
            return null;
        }
        for (NewsModel childModel : model.getData().getNews()) {
            if (childModel.getGid().equals(gid)) {
                return childModel;
            }
        }
        return null;
    }

    public void delete(NewsModel model) {
        helper.delByParentGid(model.getGid());
        DbQueryParams params = new DbQueryParams(NewsDbModel.class);
        params.appendSelection(new DbQueryParams.SelectionArgs(DbQueryParams.SelectionType.EQUAL, "gid", model.getGid()));
        deleteAll(params);
    }

    public static void register(INewsDataDbChange listener) {
        getInstance().dbChangeListenerList.add(listener);
    }

    public static void unregister(INewsDataDbChange listener) {
        getInstance().dbChangeListenerList.remove(listener);
    }
}
