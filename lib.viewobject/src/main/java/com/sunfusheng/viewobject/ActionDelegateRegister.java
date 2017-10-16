package com.sunfusheng.viewobject;

import android.content.Context;

import com.sunfusheng.utils.actions.Action4;
import com.sunfusheng.viewobject.delegate.action.factory.AbsActionDelegateProvider;
import com.sunfusheng.viewobject.viewobject.ViewObject;

public class ActionDelegateRegister extends AbsActionDelegateProvider {

    private static class Holder {
        private static ActionDelegateRegister instance = new ActionDelegateRegister();
    }

    public static ActionDelegateRegister getInstance() {
        return Holder.instance;
    }

    private ActionDelegateRegister() {
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
