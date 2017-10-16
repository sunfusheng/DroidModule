package com.sunfusheng.infostream.HeaderProvider;

import android.content.Context;

import com.sunfusheng.viewobject.delegate.action.factory.ActionDelegateFactory;
import com.sunfusheng.viewobject.viewobject.ViewObject;
import com.sunfusheng.viewobject.viewobject.factory.ViewObjectFactory;

import java.util.List;

import io.reactivex.Observable;

public interface HeaderProvider {

    Observable<List<ViewObject>> getHeader(Context context, ActionDelegateFactory actionDelegateFactory, ViewObjectFactory viewObjectFactory);

    int getFirstVisibleHeader();

    void init();

    void unInit();

    boolean showInBottom();

    boolean syncWithDataSource();
}
