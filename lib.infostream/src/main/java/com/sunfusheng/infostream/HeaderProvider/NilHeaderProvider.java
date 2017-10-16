package com.sunfusheng.infostream.HeaderProvider;

import android.content.Context;

import com.sunfusheng.viewobject.delegate.action.factory.ActionDelegateFactory;
import com.sunfusheng.viewobject.viewobject.ViewObject;
import com.sunfusheng.viewobject.viewobject.factory.ViewObjectFactory;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class NilHeaderProvider implements HeaderProvider {

    @Override
    public Observable<List<ViewObject>> getHeader(Context context, ActionDelegateFactory actionDelegateFactory, ViewObjectFactory viewObjectFactory) {
        return Observable.just(new ArrayList<>());
    }

    @Override
    public int getFirstVisibleHeader() {
        return 0;
    }

    @Override
    public void init() {
    }

    @Override
    public void unInit() {
    }

    @Override
    public boolean showInBottom() {
        return false;
    }

    @Override
    public boolean syncWithDataSource() {
        return false;
    }
}
