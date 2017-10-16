package com.sunfusheng.viewobject;

import android.content.Context;

import com.sunfusheng.viewobject.delegate.action.factory.ActionDelegateFactory;
import com.sunfusheng.viewobject.viewobject.ViewObject;
import com.sunfusheng.viewobject.viewobject.factory.ViewObjectFactory;
import com.sunfusheng.viewobject.viewobject.factory.ViewObjectProviderBase;

import io.reactivex.functions.Function;
import io.reactivex.functions.Function4;

public class ViewObjectRegister extends ViewObjectProviderBase {

    private static class Holder {
        private static ViewObjectRegister instance = new ViewObjectRegister();
    }

    public static ViewObjectRegister getInstance() {
        return Holder.instance;
    }

    private ViewObjectRegister() {
    }

    public <T, K> void registerViewObjectCreator(Class<T> modelClass, Function<T, K> keyGenerator, K key, Function4<T, Context, ActionDelegateFactory, ViewObjectFactory, ViewObject> viewObjectCreator) {
        super.registerViewObjectCreator(modelClass, keyGenerator, key, viewObjectCreator);
    }

    public <T> void registerViewObjectCreator(Class<T> modelClass, Function4<T, Context, ActionDelegateFactory, ViewObjectFactory, ViewObject> viewObjectCreator) {
        super.registerViewObjectCreator(modelClass, viewObjectCreator);
    }
}
