package com.sunfusheng.viewobject.delegate.action.factory;

import com.sunfusheng.viewobject.delegate.action.OnActionRaisedListener;

public interface ActionDelegateFactory {

    OnActionRaisedListener createActionDelegate(Object obj);
}
