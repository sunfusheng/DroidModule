package com.sunfusheng.infostream.base;

public interface BaseViewWithLifecycle<T extends BasePresenter> extends BaseView<T> {

    void onResume();

    void onPause();
}
