package com.sunfusheng.infostream.base;

public interface BasePresenterWithLifecycle extends BasePresenter {

    void onResume();

    void onPause();
}
