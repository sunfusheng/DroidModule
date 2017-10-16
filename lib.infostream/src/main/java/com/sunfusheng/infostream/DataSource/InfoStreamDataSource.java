package com.sunfusheng.infostream.DataSource;

import android.support.annotation.IntDef;
import android.support.v4.util.Pair;

import com.sunfusheng.infostream.InfoStreamContract;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.reactivex.Observable;

public interface InfoStreamDataSource {

    @IntDef({SourceType.LocalSource, SourceType.RemoteSource})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SourceType {
        int LocalSource = 1;
        int RemoteSource = 2;
    }

    Observable<Pair<Integer, InfoStreamDataList>> load(InfoStreamContract.LoadType loadType);

    Observable<Pair<Integer, InfoStreamDataList>> loadMore();

    long lastUpdateTime();

    boolean remove(Object item);
}
