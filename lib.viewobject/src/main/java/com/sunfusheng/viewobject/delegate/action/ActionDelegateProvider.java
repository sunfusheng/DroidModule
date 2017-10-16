package com.sunfusheng.viewobject.delegate.action;

import android.content.Context;

import com.sunfusheng.utils.actions.Action4;
import com.sunfusheng.viewobject.ActionDelegateRegister;
import com.sunfusheng.viewobject.delegate.action.factory.AbsActionDelegateProvider;
import com.sunfusheng.viewobject.delegate.action.factory.ActionDelegateCreator;
import com.sunfusheng.viewobject.viewobject.ViewObject;

public class ActionDelegateProvider extends AbsActionDelegateProvider {

    @SuppressWarnings("unchecked")
    @Override
    public OnActionRaisedListener createActionDelegate(Object obj) {
        Class clazz = (obj == null) ? Void.class : obj.getClass();
        ActionDelegateCreator adCreator;
        do {
            adCreator = actionDelegateMap.get(clazz);
            clazz = clazz.getSuperclass();
        } while (adCreator == null && clazz != null);

        final ActionDelegateCreator actionDelegateCreator = adCreator;
        return (context, voClass, actionId, data, viewObject) -> {
            boolean handled = false;
            if (actionDelegateCreator != null && (actionDelegateCreator.hasActionForViewObject(voClass) || actionDelegateCreator.hasActionForActionId(actionId))) {
                actionDelegateCreator.onActionRaised(context, voClass, actionId, data, viewObject);
                handled = true;
            }

            if (actionDelegateWithoutData.hasActionForViewObject(voClass) || actionDelegateWithoutData.hasActionForActionId(actionId)) {
                actionDelegateWithoutData.onActionRaised(context, voClass, actionId, data, viewObject);
                handled = true;
            }

            if (!handled) {
                OnActionRaisedListener actionListener = ActionDelegateRegister.getInstance().createActionDelegate(obj);
                if (actionListener == null) {
                    return;
                }
                actionListener.onActionRaised(context, voClass, actionId, data, viewObject);
            }
        };
    }

    @Override
    public void registerActionDelegate(Class<? extends ViewObject> viewObjectClass, Action4<Context, Integer, Object, ViewObject<?>> actionDelegate) {
        super.registerActionDelegate(viewObjectClass, actionDelegate);
    }

    @Override
    public <T> void registerActionDelegate(Class<? extends ViewObject> viewObjectClass, Class<T> modelClass, Action4<Context, Integer, T, ViewObject<?>> actionDelegate) {
        super.registerActionDelegate(viewObjectClass, modelClass, actionDelegate);
    }

    @Override
    public void registerActionDelegate(int actionId, Action4<Context, Integer, Object, ViewObject<?>> actionDelegate) {
        super.registerActionDelegate(actionId, actionDelegate);
    }

    @Override
    public <T> void registerActionDelegate(int actionId, Class<T> modelClass, Action4<Context, Integer, T, ViewObject<?>> actionDelegate) {
        super.registerActionDelegate(actionId, modelClass, actionDelegate);
    }
}