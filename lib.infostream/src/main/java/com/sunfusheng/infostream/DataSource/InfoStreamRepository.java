package com.sunfusheng.infostream.DataSource;

import android.support.v4.util.Pair;

import com.sunfusheng.infostream.InfoStreamContract;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.schedulers.Schedulers;

@SuppressWarnings("unused")
public class InfoStreamRepository implements InfoStreamDataSource {

    private InfoStreamDataSource localDataSource;
    private InfoStreamDataSource remoteDataSource;
    private DataPersist dataPersist;

    public static InfoStreamRepository createInstance(InfoStreamDataSource localDataSource,
                                                      InfoStreamDataSource remoteDataSource,
                                                      DataPersist dataPersist) {
        InfoStreamRepository instance = new InfoStreamRepository();
        instance.setDataSource(localDataSource, remoteDataSource, dataPersist);
        return instance;
    }

    private InfoStreamRepository() {
    }

    public void setDataSource(InfoStreamDataSource localDataSource,
                              InfoStreamDataSource remoteDataSource,
                              DataPersist dataPersist) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
        this.dataPersist = dataPersist;
    }

    @Override
    public Observable<Pair<Integer, InfoStreamDataList>> load(InfoStreamContract.LoadType loadType) {
        if (loadType == InfoStreamContract.LoadType.TYPE_LOCAL) {
            return localDataSource.load(InfoStreamContract.LoadType.TYPE_LOCAL).subscribeOn(Schedulers.io())
                    .onErrorResumeNext(Observable.just(new Pair<>(SourceType.LocalSource, new InfoStreamDataList(new ArrayList<>()))))
                    .flatMap(pair -> {
                        if (pair.second == null || pair.second.getData() == null || pair.second.getData().size() == 0) {
                            return remoteDataSource.load(InfoStreamContract.LoadType.TYPE_REMOTE).subscribeOn(Schedulers.io()).compose(persistData(false));
                        } else {
                            return Observable.just(pair);
                        }
                    });
        } else if (loadType == InfoStreamContract.LoadType.TYPE_REMOTE) {
            return remoteDataSource.load(InfoStreamContract.LoadType.TYPE_REMOTE).subscribeOn(Schedulers.io()).compose(persistData(false));
        } else if (loadType == InfoStreamContract.LoadType.TYPE_BOTH) {
            return Observable.concat(
                    localDataSource.load(InfoStreamContract.LoadType.TYPE_LOCAL).subscribeOn(Schedulers.io()).onErrorReturn(throwable -> new Pair<>(SourceType.LocalSource, new InfoStreamDataList(new ArrayList<>()))),
                    remoteDataSource.load(InfoStreamContract.LoadType.TYPE_REMOTE).subscribeOn(Schedulers.io()).compose(persistData(false))
            );
        } else {
            return Observable.just(new Pair<>(SourceType.LocalSource, new InfoStreamDataList(new ArrayList<>())));
        }
    }

    @Override
    public Observable<Pair<Integer, InfoStreamDataList>> loadMore() {
        if (remoteDataSource != null) {
            return remoteDataSource.loadMore().subscribeOn(Schedulers.io()).compose(persistData(true));
        } else {
            return Observable.just(null);
        }
    }

    @Override
    public long lastUpdateTime() {
        if (remoteDataSource != null) {
            return remoteDataSource.lastUpdateTime();
        } else if (localDataSource != null) {
            return localDataSource.lastUpdateTime();
        } else {
            return 0;
        }
    }

    @Override
    public boolean remove(Object item) {
        return localDataSource != null && localDataSource.remove(item);
    }

    @SuppressWarnings("unchecked")
    private ObservableTransformer<Pair<Integer, InfoStreamDataList>, Pair<Integer, InfoStreamDataList>> persistData(boolean isLoadMore) {
        return observable -> observable.filter(it -> dataPersist != null)
                .doOnNext(it -> dataPersist.persist(it.second.getData(), isLoadMore));
    }
}
