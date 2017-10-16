package com.sunfusheng.utils;

import android.content.Context;

@SuppressWarnings("unused")
public class ApplicationContextHolder {

    public static Context getContext() {
        return ApplicationStatus.getApplicationContext();
    }

}
