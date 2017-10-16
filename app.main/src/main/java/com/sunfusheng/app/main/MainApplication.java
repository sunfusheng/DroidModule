package com.sunfusheng.app.main;

import android.app.Application;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.sunfusheng.base.Constants;
import com.sunfusheng.base.db.RealmConfig;
import com.sunfusheng.router.Router;
import com.sunfusheng.utils.ApplicationStatus;

/**
 * Created by sunfusheng on 2017/5/23.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Constants.isDebug = BuildConfig.isDebug;

        Logger.init("DroidModule").logLevel(Constants.isDebug? LogLevel.FULL : LogLevel.NONE);

        ApplicationStatus.initialize(this);

        Router.init(this);

        RealmConfig.init(this);
    }
}
