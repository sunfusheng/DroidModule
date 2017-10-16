package com.sunfusheng.news.news;

import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.sunfusheng.base.db.news.NewsListDbHelper;
import com.sunfusheng.base.model.BaseModel;
import com.sunfusheng.base.model.NewsModel;
import com.sunfusheng.base.net.Api;
import com.sunfusheng.infostream.DataSource.InfoStreamDataList;
import com.sunfusheng.infostream.DataSource.InfoStreamDataSource;
import com.sunfusheng.infostream.InfoStreamContract;
import com.sunfusheng.news.net.ApiService;
import com.sunfusheng.news.preference.NewsSharedPreferences;
import com.sunfusheng.utils.ListUtil;
import com.sunfusheng.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sunfusheng.base.Constants.TAG_TYPE_NEWS_GROUP;
import static com.sunfusheng.base.Constants.TAG_TYPE_NEWS_MODULE;

/**
 * Created by sunfusheng on 2017/5/24.
 */
public class NewsDataSource implements InfoStreamDataSource {

    final private static int ITEM_COUNT_PER_REQUEST = 12;
    final private static int REFRESH_COUNT_RESET_INTERVAL = 3 * 60 * 60 * 1000;

    final private String cid;
    final private String is_paging;
    private String city_code;
    private boolean is_persist = true;
    private String last = "";
    private String tips = "";

    private int refreshCount[] = {1, 1};
    private List<NewsModel> newsList = new CopyOnWriteArrayList<>();


    public static NewsDataSource createInstance(String cid, String is_paging, String city_code, boolean is_persist) {
        if (TextUtils.isEmpty(cid)) {
            return null;
        }
        return new NewsDataSource(cid, is_paging, city_code, is_persist);
    }

    private NewsDataSource(String cid, String is_paging, String city_code, boolean is_persist) {
        this.cid = cid;
        this.is_paging = is_paging;
        this.city_code = city_code;
        this.is_persist = is_persist;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Observable<Pair<Integer, InfoStreamDataList>> load(InfoStreamContract.LoadType loadType) {
        return Observable.concat(
                Observable.just(loadType)
                        .filter(it -> it == InfoStreamContract.LoadType.TYPE_BOTH || it == InfoStreamContract.LoadType.TYPE_LOCAL)
                        .flatMap(it -> loadLocalData()),
                Observable.just(loadType)
                        .filter(it -> it == InfoStreamContract.LoadType.TYPE_BOTH || it == InfoStreamContract.LoadType.TYPE_REMOTE)
                        .flatMap(__ -> requestNewsList(false)
                                .map(it -> new Pair<>(SourceType.RemoteSource, new InfoStreamDataList(tips, it)))
                                .doOnNext(it -> it.second.setData(removeDuplicateData(it.second.getData(), false)))
                        ))
                .doOnNext(it -> cacheNewsList(it.second.getData(), false))
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<Pair<Integer, InfoStreamDataList>> loadLocalData() {
        if (!Utils.isEmpty(newsList) && is_persist) {
            return Observable.just(new Pair<>(SourceType.LocalSource, new InfoStreamDataList(newsList)));
        }
        return requestPersistedNewsList()
                .subscribeOn(Schedulers.io())
                .map(it -> new Pair<>(SourceType.LocalSource, new InfoStreamDataList(it)));
    }

    @Override
    public Observable<Pair<Integer, InfoStreamDataList>> loadMore() {
        return Observable.defer(() -> requestPersistedNewsList()
                .filter(it -> !Utils.isEmpty(it))
                .switchIfEmpty(Observable.defer(() -> requestNewsList(true)))
                .map(it -> removeDuplicateData(it, true))
                .doOnNext(it -> cacheNewsList(it, true))
                .map(it -> new Pair<>(SourceType.RemoteSource, new InfoStreamDataList(it))))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<List<NewsModel>> requestNewsList(boolean loadMore) {
        int offset = 0;
        if (isPaging() && loadMore) {
            offset = newsList.size();
        }
        final long[] time = {System.currentTimeMillis()};
        return Api.getService(Api.ServiceType.GENERAL, ApiService.class)
                .getNewsList(cid, is_paging, Integer.toString(offset), loadMore ? "2" : "1", Integer.toString(getRefreshCount(loadMore)), city_code, last)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(it -> tips = it.getData().getTips())
                .map(BaseModel::getData)
                .map(InfoStreamDataList::getData)
                .flatMap(Observable::fromIterable)
                .doOnNext(it -> it.setChannel(cid))
                .doOnNext(it -> it.setTime(time[0]++))
                .toList().toObservable()
                .filter(it -> !Utils.isEmpty(it) || is_persist)
                .doOnNext(this::setLast)
                .doOnNext(it -> setLastRefreshTime(loadMore, System.currentTimeMillis()))
                .doOnNext(it -> persistNewsList(it, loadMore))
                .doOnError(Throwable::printStackTrace);
    }

    public void enablePersist(boolean is_persist) {
        this.is_persist = is_persist;
    }

    private boolean isPaging() {
        return "1".equals(is_paging);
    }

    private long getLastRefreshTime(boolean isLoadMore) {
        return NewsSharedPreferences.getInstance().getLastRefreshTime(cid, isLoadMore);
    }

    private void setLastRefreshTime(boolean isLoadMore, long time) {
        NewsSharedPreferences.getInstance().setLastRefreshTime(cid, isLoadMore, time);
    }

    private void setLast(List<NewsModel> list) {
        if (Utils.isEmpty(list)) {
            return;
        }

        last = list.get(list.size() - 1).getLast();
        if (last == null) {
            last = "";
        }
    }

    private void persistNewsList(List<NewsModel> list, boolean loadMore) {
        if (!is_persist || Utils.isEmpty(list)) {
            return;
        }
        Schedulers.io().createWorker().schedule(() -> NewsListDbHelper.getInstance().saveNewsList(list, cid, loadMore, isPaging()));
    }

    private Observable<List<NewsModel>> requestPersistedNewsList() {
        return Observable.defer(() -> Observable.just(NewsListDbHelper.getInstance().getNewsList(newsList.size(), ITEM_COUNT_PER_REQUEST, cid)))
                .subscribeOn(Schedulers.io())
                .filter(it -> it != null && it.size() > 0);
    }

    private int getRefreshCount(boolean isLoadMore) {
        if (System.currentTimeMillis() - getLastRefreshTime(isLoadMore) > REFRESH_COUNT_RESET_INTERVAL) {
            refreshCount[isLoadMore ? 1 : 0] = 1;
        }
        return refreshCount[isLoadMore ? 1 : 0]++;
    }

    private void cacheNewsList(List<NewsModel> list, boolean loadMore) {
        if (Utils.isEmpty(list)) {
            return;
        }

        if (loadMore) {
            newsList.addAll(list);
        } else if (isPaging()) {
            newsList = list;
        } else {
            newsList.addAll(0, list);
        }
    }

    private List<NewsModel> removeDuplicateData(List<NewsModel> list, boolean loadMore) {
        if (ListUtil.isEmpty(list)) {
            return list;
        }

        if (list == newsList) {
            return list;
        }

        List<NewsModel> result = new ArrayList<>(list);
        if (loadMore || !isPaging()) {
            removeDuplicateData(result);
        }
        return result;
    }

    private void removeDuplicateData(List<NewsModel> list) {
        for (int i = list.size() - 1; i >= 0; i--) {
            NewsModel item = list.get(i);

            if (isDuplicateData(newsList, list.get(i))) {
                list.remove(i);
                continue;
            }

            if (item.getData() != null && !ListUtil.isEmpty(item.getData().getNews())) {
                for (int j = item.getData().getNews().size() - 1; j >= 0; j--) {
                    if (isDuplicateData(newsList, item.getData().getNews().get(j))) {
                        item.getData().getNews().remove(j);
                    }
                }
            }
        }
    }

    private boolean isDuplicateData(List<NewsModel> list, NewsModel item) {
        for (NewsModel news : list) {
            if (item.getGid() != null && news.getGid() != null && item.getGid().equals(news.getGid())) {
                return true;
            }

            if (item.getModule_id() != null && news.getModule_id() != null && item.getModule_id().equals(news.getModule_id())) {
                return true;
            }

            if (news.getData() != null && !ListUtil.isEmpty(news.getData().getNews())) {
                return isDuplicateData(news.getData().getNews(), item);
            }
        }
        return false;
    }

    @Override
    public long lastUpdateTime() {
        return Math.max(getLastRefreshTime(false), getLastRefreshTime(true));
    }

    @Override
    public boolean remove(Object obj) {
        if (!(obj instanceof NewsModel)) {
            return false;
        }
        NewsModel news = (NewsModel) obj;
        removeFromNewsList(news);
        Schedulers.io().createWorker().schedule(() -> NewsListDbHelper.getInstance().delete(news));
        return true;
    }

    private void removeFromNewsList(NewsModel news) {
        Observable.fromIterable(newsList)
                .subscribeOn(Schedulers.io())
                .flatMap(it -> {
                    if (it.getType() == TAG_TYPE_NEWS_GROUP || it.getType() == TAG_TYPE_NEWS_MODULE) {
                        return Observable.fromIterable(it.getData().getNews()).map(model -> new Pair<>(model, it));
                    } else {
                        return Observable.just(new Pair<NewsModel, NewsModel>(it, null));
                    }
                })
                .filter(it -> it.first.equals(news))
                .take(1)
                .subscribe(it -> {
                    if (it.second == null) {
                        newsList.remove(it.first);
                    } else {
                        it.second.getData().getNews().remove(it.first);
                    }
                }, Throwable::printStackTrace);
    }
}
