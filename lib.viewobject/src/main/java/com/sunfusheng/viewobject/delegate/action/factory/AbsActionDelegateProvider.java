package com.sunfusheng.viewobject.delegate.action.factory;

import android.content.Context;

import com.sunfusheng.utils.actions.Action4;
import com.sunfusheng.viewobject.delegate.action.OnActionRaisedListener;
import com.sunfusheng.viewobject.viewobject.ViewObject;

import java.util.HashMap;
import java.util.Map;

public abstract class AbsActionDelegateProvider implements ActionDelegateFactory {

    protected Map<Class, ActionDelegateCreator> actionDelegateMap = new HashMap<>();
    protected ActionDelegateCreator<Object> actionDelegateWithoutData = new ActionDelegateCreator<>();

    @SuppressWarnings("unchecked")
    @Override
    public OnActionRaisedListener createActionDelegate(Object obj) {
        Class clazz = obj == null ? Void.class : obj.getClass();
        ActionDelegateCreator adCreator;
        do {
            adCreator = actionDelegateMap.get(clazz);
            clazz = clazz.getSuperclass();
        } while (adCreator == null && clazz != null);

        final ActionDelegateCreator actionDelegateCreator = adCreator;
        return (context, voClass, actionId, data, viewObject) -> {
            if (actionDelegateCreator != null && (actionDelegateCreator.hasActionForViewObject(voClass) || actionDelegateCreator.hasActionForActionId(actionId))) {
                actionDelegateCreator.onActionRaised(context, voClass, actionId, data, viewObject);
            }

            if (actionDelegateWithoutData.hasActionForViewObject(voClass) || actionDelegateWithoutData.hasActionForActionId(actionId)) {
                actionDelegateWithoutData.onActionRaised(context, voClass, actionId, data, viewObject);
            }
        };
    }

    protected void registerActionDelegate(Class<? extends ViewObject> viewObjectClass, Action4<Context, Integer, Object, ViewObject<?>> actionDelegate) {
        actionDelegateWithoutData.registerAction(viewObjectClass, actionDelegate);
    }

    @SuppressWarnings("unchecked")
    protected <T> void registerActionDelegate(Class<? extends ViewObject> viewObjectClass, Class<T> modelClass, Action4<Context, Integer, T, ViewObject<?>> actionDelegate) {
        ActionDelegateCreator actionDelegateCreator = actionDelegateMap.get(modelClass);
        if (actionDelegateCreator == null) {
            actionDelegateCreator = new ActionDelegateCreator<T>();
            actionDelegateMap.put(modelClass, actionDelegateCreator);
        }
        actionDelegateCreator.registerAction(viewObjectClass, actionDelegate);
    }

    protected void registerActionDelegate(int actionId, Action4<Context, Integer, Object, ViewObject<?>> actionDelegate) {
        actionDelegateWithoutData.registerAction(actionId, actionDelegate);
    }

    @SuppressWarnings("unchecked")
    protected <T> void registerActionDelegate(int actionId, Class<T> modelClass, Action4<Context, Integer, T, ViewObject<?>> actionDelegate) {
        ActionDelegateCreator actionDelegateCreator = actionDelegateMap.get(modelClass);
        if (actionDelegateCreator == null) {
            actionDelegateCreator = new ActionDelegateCreator<T>();
            actionDelegateMap.put(modelClass, actionDelegateCreator);
        }
        actionDelegateCreator.registerAction(actionId, actionDelegate);
    }
}