package com.sunfusheng.infostream.anotations;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by sunfusheng on 2017/5/24.
 */
@IntDef({FooterStatus.IDLE, FooterStatus.LOADING, FooterStatus.FULL, FooterStatus.ERROR, FooterStatus.GONE})
@Retention(RetentionPolicy.SOURCE)
public @interface FooterStatus {

    int IDLE = 0;
    int LOADING = 1;
    int FULL = 2;
    int ERROR = 3;
    int GONE = 4;
}
