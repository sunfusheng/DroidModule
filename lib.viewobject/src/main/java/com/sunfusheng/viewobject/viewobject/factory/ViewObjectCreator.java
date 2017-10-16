package com.sunfusheng.viewobject.viewobject.factory;

import android.content.Context;

import com.sunfusheng.viewobject.delegate.action.factory.ActionDelegateFactory;
import com.sunfusheng.viewobject.viewobject.ViewObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Function;
import io.reactivex.functions.Function4;

public class ViewObjectCreator<T, K> implements ViewObjectFactory {

    private Map<Object, Function4<T, Context, ActionDelegateFactory, ViewObjectFactory, ViewObject>> viewObjectCreatorMethodMap = new HashMap<>();
    protected Function<T, K> keyGenerator = null;

    @SuppressWarnings("unchecked")
    @Override
    public final ViewObject model2ViewObject(Object model, Context context, ActionDelegateFactory actionDelegateFactory) {
        try {
            T concreteModel = (T) model;
            if (keyGenerator != null) {
                K key = keyGenerator.apply(concreteModel);
                if (keyGenerator.apply(concreteModel) == null || !viewObjectCreatorMethodMap.containsKey(key)) {
                    return null;
                }
                return viewObjectCreatorMethodMap.get(key).apply(concreteModel, context, actionDelegateFactory, this);
            } else {
                return viewObjectCreatorMethodMap.get(null).apply(concreteModel, context, actionDelegateFactory, this);
            }
        } catch (Throwable e) {
            return null;
        }
    }

    protected final void registerCreatorMethod(K key, Function4<T, Context, ActionDelegateFactory, ViewObjectFactory, ViewObject> factoryFunction) {
        viewObjectCreatorMethodMap.put(key, factoryFunction);
    }

    protected final void registerCreatorMethod(Function4<T, Context, ActionDelegateFactory, ViewObjectFactory, ViewObject> factoryFunction) {
        viewObjectCreatorMethodMap.put(null, factoryFunction);
    }
}
