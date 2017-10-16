package com.sunfusheng.infostream.DataSource;

import com.sunfusheng.viewobject.helper.ViewObjectComparator;

import java.util.List;

import io.reactivex.Observable;

public interface InfoStreamPushDataSource extends InfoStreamDataSource {

    Observable<List<Object>> onNewItemObservable();

    Observable<List<ViewObjectComparator>> onRemoveItemObservable();
}
