package com.sunfusheng.viewobject.viewobject.factory;

import android.content.Context;

import com.sunfusheng.viewobject.delegate.action.factory.ActionDelegateFactory;
import com.sunfusheng.viewobject.viewobject.ViewObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Function;
import io.reactivex.functions.Function4;

public abstract class ViewObjectProviderBase implements ViewObjectFactory {

    private Map<Class, ViewObjectCreator> viewObjectCreatorMap = new HashMap<>();

    @Override
    public ViewObject model2ViewObject(Object model, Context context, ActionDelegateFactory actionDelegateFactory) {
        ViewObjectCreator viewObjectCreator = viewObjectCreatorMap.get(model.getClass());
        if (viewObjectCreator == null) {
            return null;
        }
        return viewObjectCreator.model2ViewObject(model, context, actionDelegateFactory);
    }

    @SuppressWarnings("unchecked")
    protected <T, K> void registerViewObjectCreator(Class<T> modelClass, Function<T, K> keyGenerator, K key, Function4<T, Context, ActionDelegateFactory, ViewObjectFactory, ViewObject> creatorMethod) {
        ViewObjectCreator viewObjectCreator = viewObjectCreatorMap.get(modelClass);
        if (viewObjectCreator == null) {
            viewObjectCreator = new ViewObjectCreator<T, K>();
            viewObjectCreator.keyGenerator = keyGenerator;
            viewObjectCreatorMap.put(modelClass, viewObjectCreator);
        }
        viewObjectCreator.registerCreatorMethod(key, creatorMethod);
    }

    @SuppressWarnings("unchecked")
    protected <T> void registerViewObjectCreator(Class<T> modelClass, Function4<T, Context, ActionDelegateFactory, ViewObjectFactory, ViewObject> creatorMethod) {
        ViewObjectCreator viewObjectCreator = viewObjectCreatorMap.get(modelClass);
        if (viewObjectCreator == null) {
            viewObjectCreator = new ViewObjectCreator<T, Void>();
            viewObjectCreatorMap.put(modelClass, viewObjectCreator);
        }
        viewObjectCreator.registerCreatorMethod(creatorMethod);
    }
}
