package com.sunfusheng.viewobject.viewobject;

public interface LifeCycleNotifySource {

    void registerLifeCycleNotify(ViewObject notify);

    void unregisterLifeCycleNotify(ViewObject notify);
}
