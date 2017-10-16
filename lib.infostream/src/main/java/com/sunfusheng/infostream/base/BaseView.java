package com.sunfusheng.infostream.base;

import android.content.Context;

public interface BaseView<T extends BasePresenter> {

    Context getContext();

    void init();

    void unInit();

    void setPresenter(T presenter);
}
