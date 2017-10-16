package com.sunfusheng.viewobject.viewobject.factory;

import android.content.Context;

import com.sunfusheng.viewobject.delegate.action.factory.ActionDelegateFactory;
import com.sunfusheng.viewobject.viewobject.ViewObject;

public interface ViewObjectFactory {

    ViewObject model2ViewObject(Object model, Context context, ActionDelegateFactory actionDelegateFactory);
}
