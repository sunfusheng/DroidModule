package com.sunfusheng.infostream.anotations;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by sunfusheng on 2017/5/24.
 */
@IntDef({LoadingStatus.LOADING, LoadingStatus.SUCCEED, LoadingStatus.FAILED, LoadingStatus.EMPTY})
@Retention(RetentionPolicy.SOURCE)
public @interface LoadingStatus {

    int LOADING = 0;
    int SUCCEED = 1;
    int FAILED = 2;
    int EMPTY = 3;
}
