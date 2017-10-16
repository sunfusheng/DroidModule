package com.sunfusheng.viewobject.viewobject;

import android.content.Context;

import com.sunfusheng.viewobject.ViewObjectRegister;
import com.sunfusheng.viewobject.delegate.action.factory.ActionDelegateFactory;
import com.sunfusheng.viewobject.viewobject.factory.ViewObjectFactory;
import com.sunfusheng.viewobject.viewobject.factory.ViewObjectProviderBase;

import io.reactivex.functions.Function;
import io.reactivex.functions.Function4;

public class ViewObjectProvider extends ViewObjectProviderBase {

    @Override
    public ViewObject model2ViewObject(Object model, Context context, ActionDelegateFactory actionDelegateFactory) {
        ViewObject viewObject = super.model2ViewObject(model, context, actionDelegateFactory);
        if (viewObject != null) {
            return viewObject;
        }
        return ViewObjectRegister.getInstance().model2ViewObject(model, context, actionDelegateFactory);
    }

    public <T, K> void registerViewObjectCreator(Class<T> modelClass, Function<T, K> keyGenerator, K key, Function4<T, Context, ActionDelegateFactory, ViewObjectFactory, ViewObject> viewObjectCreator) {
        super.registerViewObjectCreator(modelClass, keyGenerator, key, viewObjectCreator);
    }

    public <T> void registerViewObjectCreator(Class<T> modelClass, Function4<T, Context, ActionDelegateFactory, ViewObjectFactory, ViewObject> viewObjectCreator) {
        super.registerViewObjectCreator(modelClass, viewObjectCreator);
    }
}
