package com.sunfusheng.viewobject.helper.group;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

public class ViewObjectGroupTransformer implements ObservableTransformer<GroupableItem, List<ViewObjectGroupDataBase>> {

    public interface DataGroupCreator {
        ViewObjectGroupDataBase createDataGroup(String key);
    }

    private Subject<ViewObjectGroupDataBase> objectSubject = ReplaySubject.create();
    private Map<String, ViewObjectGroupDataBase> groupMap = new HashMap<>();
    private DataGroupCreator dataGroupCreator;

    static public ViewObjectGroupTransformer create() {
        return create(null);
    }

    static public ViewObjectGroupTransformer create(DataGroupCreator dataGroupCreator) {
        ViewObjectGroupTransformer transformer = new ViewObjectGroupTransformer();
        transformer.dataGroupCreator = dataGroupCreator;
        if (transformer.dataGroupCreator == null) {
            transformer.dataGroupCreator = ViewObjectGroupDataBase::new;
        }

        return transformer;
    }

    private ViewObjectGroupTransformer() {
    }

    @Override
    public Observable<List<ViewObjectGroupDataBase>> apply(Observable<GroupableItem> source) {
        source.groupBy(GroupableItem::generateKey)
                .subscribe(groupObservable -> {
                    if (!groupMap.containsKey(groupObservable.getKey())) {
                        groupMap.put(groupObservable.getKey(), dataGroupCreator.createDataGroup(groupObservable.getKey()));
                        objectSubject.onNext(groupMap.get(groupObservable.getKey()));
                    }
                    groupObservable.subscribe(it -> groupMap.get(groupObservable.getKey()).getDataList().add(it), Throwable::printStackTrace);
                }, objectSubject::onError, objectSubject::onComplete);

        return objectSubject.toList().toObservable();
    }
}
