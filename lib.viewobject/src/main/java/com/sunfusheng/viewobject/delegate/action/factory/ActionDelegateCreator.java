package com.sunfusheng.viewobject.delegate.action.factory;

import android.content.Context;
import android.util.SparseArray;

import com.sunfusheng.utils.actions.Action4;
import com.sunfusheng.viewobject.delegate.action.OnActionRaisedListener;
import com.sunfusheng.viewobject.viewobject.ViewObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunfusheng on 2017/5/10.
 */
public class ActionDelegateCreator<T> implements OnActionRaisedListener {

    private Map<Class<? extends ViewObject>, Action4<Context, Integer, T, ViewObject<?>>> actionMapByViewObject = new HashMap<>();
    private SparseArray<Action4<Context, Integer, T, ViewObject<?>>> actionMapByActionId = new SparseArray<>();

    @SuppressWarnings("unchecked")
    @Override
    public final void onActionRaised(Context context, Class<? extends ViewObject> voClass, int actionId, Object data, ViewObject<?> viewObject) {
        try {
            Action4<Context, Integer, T, ViewObject<?>> action = actionMapByViewObject.get(voClass);
            if (action != null) {
                action.call(context, actionId, (T) data, viewObject);
            }

            action = actionMapByActionId.get(actionId);
            if (action != null) {
                action.call(context, actionId, (T) data, viewObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void registerAction(Class<? extends ViewObject> voClass, Action4<Context, Integer, T, ViewObject<?>> action) {
        actionMapByViewObject.put(voClass, action);
    }

    public final void registerAction(int actionId, Action4<Context, Integer, T, ViewObject<?>> action) {
        actionMapByActionId.put(actionId, action);
    }

    public boolean hasActionForViewObject(Class<? extends ViewObject> voClass) {
        Class clazz = voClass;
        Action4<Context, Integer, T, ViewObject<?>> action;
        do {
            action = actionMapByViewObject.get(clazz);
            if (action != null) {
                if (!actionMapByViewObject.containsKey(voClass)) {
                    actionMapByViewObject.put(voClass, action);
                }
                return true;
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);

        return false;
    }

    public boolean hasActionForActionId(int actionId) {
        return actionMapByActionId.get(actionId) != null;
    }
}
