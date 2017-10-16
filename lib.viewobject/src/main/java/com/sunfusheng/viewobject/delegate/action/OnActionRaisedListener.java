package com.sunfusheng.viewobject.delegate.action;

import android.content.Context;

import com.sunfusheng.viewobject.viewobject.ViewObject;

// 已注册的点击行为被唤起，点击行为得到执行
public interface OnActionRaisedListener {

    void onActionRaised(Context context, Class<? extends ViewObject> voClass, int actionId, Object data, ViewObject<?> viewObject) throws Exception;
}
